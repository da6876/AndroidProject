package bd.com.arda.findyourhome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.arda.findyourhome.adapter.MyToletAdapter;
import bd.com.arda.findyourhome.adapter.ToletAdapter;
import bd.com.arda.findyourhome.adapter.ToletDetailsFragment;
import bd.com.arda.findyourhome.model.MyToletListModel;
import bd.com.arda.findyourhome.model.ToletListModel;

public class MyToletList extends AppCompatActivity {
    ListView lv_myTolet_post;
    LinearLayout layout_noting,layout_Show_post;
    ToletAdapter toletAdapter;
    String URL_GET_TOTLET_DATA = "http://103.91.54.60/apps/FYH/FYH_GET_TOLET_USER.php";
    ProgressDialog pd;
    ArrayList<MyToletListModel> mylisttofcustomer;
    SharedPreferences sharedPreferences;
    String str_accout_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_tolet_list);

        sharedPreferences=getSharedPreferences("FYH_UserData", Context.MODE_PRIVATE);
        str_accout_id=sharedPreferences.getString("USER_ACC_ID","0");
        layout_noting=findViewById(R.id.layout_noting);
        layout_Show_post=findViewById(R.id.layout_Show_post);
        lv_myTolet_post=findViewById(R.id.lv_myTolet_post);
        mylisttofcustomer = new ArrayList<>();

        GetMyToletList();
    }


    private void GetMyToletList() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_TOTLET_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseDataForGetToletType(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Toast.makeText(MyToletList.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_USER_ACC_ID",str_accout_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseDataForGetToletType(String response) {

        try {
            JSONObject obj = new JSONObject(response);
            String responcse = obj.getString("responce");
            JSONObject obj2 = new JSONObject(responcse);
            String status_code = obj2.getString("status_code");
            String msg = obj2.getString("msg");
            String values = obj2.getString("values");
            if (status_code.equals("200")) {
                if (pd != null) {
                    pd.dismiss();
                }
            } else {
                if (pd != null) {
                    pd.dismiss();
                }
                JSONArray jArray = new JSONArray(values.toString());

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    MyToletListModel toletListModel=new MyToletListModel(
                            json_data.getString("TOLET_INFO_ID"),
                            json_data.getString("USER_ACC_ID"),
                            json_data.getString("TOLET_NAME"),
                            json_data.getString("TOLET_DETAILS"),
                            json_data.getString("ADDRESS"),
                            json_data.getString("LATTITUDE"),
                            json_data.getString("LOGLITUTDE"),
                            json_data.getString("PRICE"),
                            json_data.getString("BATHS"),
                            json_data.getString("BEDS"),
                            json_data.getString("FLOORS"),
                            json_data.getString("AVAILABLE_FROM"),
                            json_data.getString("CONTACT_PERSON_NM"),
                            json_data.getString("CONTACT_PERSON_PHN"),
                            json_data.getString("CONTACT_PERSON_EML"),
                            json_data.getString("TOLET_TYPE_ID"),
                            json_data.getString("TOLET_TYPE_NAME"),
                            json_data.getString("CREATE_DATA"),
                            json_data.getString("CREATE_BY")
                    );
                    mylisttofcustomer.add(toletListModel);

                }

                if (mylisttofcustomer.size()<=0){
                    layout_noting.setVisibility(View.VISIBLE);
                    layout_Show_post.setVisibility(View.GONE);
                }else {
                    layout_noting.setVisibility(View.GONE);
                    layout_Show_post.setVisibility(View.VISIBLE);
                    MyToletAdapter adapter = new MyToletAdapter(getApplicationContext(), R.layout.my_tolet_list_adapter, mylisttofcustomer);
                    lv_myTolet_post.setAdapter(adapter);
                    lv_myTolet_post.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Bundle bundle = new Bundle();
                            bundle.putString("OpenType","Single");
                            bundle.putString("CONTACT_PERSON_NM", mylisttofcustomer.get(position).getCONTACT_PERSON_NM());
                            bundle.putString("CONTACT_PERSON_PHN", mylisttofcustomer.get(position).getCONTACT_PERSON_PHN());
                            bundle.putString("CONTACT_PERSON_EML", mylisttofcustomer.get(position).getCONTACT_PERSON_EML());
                            bundle.putString("FLOORS", mylisttofcustomer.get(position).getFLOORS());
                            bundle.putString("ADDRESS", mylisttofcustomer.get(position).getADDRESS());
                            bundle.putString("AVAILABLE_FROM", mylisttofcustomer.get(position).getAVAILABLE_FROM());
                            bundle.putString("BATHS", mylisttofcustomer.get(position).getBATHS());
                            bundle.putString("BEDS", mylisttofcustomer.get(position).getBEDS());
                            bundle.putString("LOGLITUTDE", mylisttofcustomer.get(position).getLOGLITUTDE());
                            bundle.putString("LATTITUDE", mylisttofcustomer.get(position).getLATTITUDE());
                            bundle.putString("PRICE", mylisttofcustomer.get(position).getPRICE());
                            bundle.putString("TOLET_DETAILS", mylisttofcustomer.get(position).getTOLET_DETAILS());
                            bundle.putString("TOLET_TYPE_ID", mylisttofcustomer.get(position).getTOLET_TYPE_ID());
                            bundle.putString("TOLET_NAME", mylisttofcustomer.get(position).getTOLET_NAME());
                            ToletDetailsFragment toletDetailsFragment = new ToletDetailsFragment();
                            toletDetailsFragment.setArguments(bundle);
                            toletDetailsFragment.show(getSupportFragmentManager(), toletDetailsFragment.getTag());
                        }
                    });
                }

            }
        } catch (JSONException e) {
            if (pd != null) {
                pd.dismiss();
            }
            e.printStackTrace();
        }

    }
}
