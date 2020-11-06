package oma.soc.bd.com.omanmotorassistance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import oma.soc.bd.com.omanmotorassistance.map.MapsPage;
import oma.soc.bd.com.omanmotorassistance.model.ServiceList;

import static oma.soc.bd.com.omanmotorassistance.LogInPage.UrlPath;

public class ServicePage extends AppCompatActivity {
    ListView service_item;
    ArrayList<ServiceList> listtofcustomer;
    private ProgressDialog pd;
    String url_get_user_service = UrlPath+"OAA_GET_USER_SERVICE_DATA.php";
    View parentLayout;
    String P_USER_INFO_ID="";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_page);

        service_item = findViewById(R.id.service_item);
        parentLayout = findViewById(android.R.id.content);
        sharedPreferences = getSharedPreferences("OAA_DATA", Context.MODE_PRIVATE);
        P_USER_INFO_ID= sharedPreferences.getString("USER_INFO_ID","");
        listtofcustomer = new ArrayList<>();


        getUserService();
        pd = ProgressDialog.show(ServicePage.this, "Getting Service Information",
                "Please wait...");
    }


    private void getUserService() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_get_user_service, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parcaeLoginResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnankBar(error.toString());
                if (pd != null) {
                    pd.dismiss();
                }
                Toast.makeText(ServicePage.this,"Server Time Out",Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_USER_INFO_ID",P_USER_INFO_ID);
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void parcaeLoginResponse(String toString) {
        try {
            JSONObject jsonObject = new JSONObject(toString);
            String responce = jsonObject.getString("responce");
            JSONObject jsonObject1 = new JSONObject(responce);
            String status_code = jsonObject1.getString("status_code");
            String msg = jsonObject1.getString("msg");
            String values = jsonObject1.getString("values");
            if (status_code.equals("500")) {
                if (pd != null) {
                    pd.dismiss();
                }
                JSONArray jsonArray=new JSONArray(values.toString());
                for (int i =0; i< jsonArray.length();i++) {
                    JSONObject jsonObject2 =jsonArray.getJSONObject(i);
                    ServiceList serviceList=new ServiceList(
                            jsonObject2.getString("SERVICE_TYPE_NAME"),
                            jsonObject2.getString("SERVICE_ID"),
                            jsonObject2.getString("SERVICE_STATUS"),
                            jsonObject2.getString("SERVICE_CHARGE"),
                            jsonObject2.getString("USER_INFO_NAME_SP"),
                            jsonObject2.getString("USER_INFO_ID_SP")
                    );
                    listtofcustomer.add(serviceList);
                }
                ServiceAdapter serviceAdapter=new ServiceAdapter(getApplicationContext(),R.layout.service_adapter,listtofcustomer);
                service_item.setAdapter(serviceAdapter);
                service_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (!listtofcustomer.get(i).getServiceStatus().equals("F")){
                            Intent intent=new Intent(ServicePage.this, MapsPage.class);
                            intent.putExtra("USER_INFO_ID_SP",listtofcustomer.get(i).getServiceByID());
                            intent.putExtra("USER_INFO_NAME_SP",listtofcustomer.get(i).getServiceByName());
                            startActivity(intent);
                        }
                    }
                });

            }else{
                if (pd != null) {
                    pd.dismiss();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (pd != null) {
                pd.dismiss();
            }
        }

    }

    public void showSnankBar(String value) {
        Snackbar.make(parentLayout, value, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }
}
