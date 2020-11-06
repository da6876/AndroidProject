package serviceprovideroma.soc.bd.com.serviceprovideroma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

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

import serviceprovideroma.soc.bd.com.serviceprovideroma.adapter.ServiceAdapterPandding;
import serviceprovideroma.soc.bd.com.serviceprovideroma.map.MapsActivity;
import serviceprovideroma.soc.bd.com.serviceprovideroma.model.ServiceListPandding;

import static serviceprovideroma.soc.bd.com.serviceprovideroma.LogInPage.UrlPath;

public class ServicePage extends AppCompatActivity {
    ListView service_item;
    ArrayList<ServiceListPandding> serviceListPanddings;
    private ProgressDialog pd;
    String url_get_user_service = UrlPath + "OAA_GET_SERVICE_LIST_FOR_SP.php";
    View parentLayout;
    String USER_INFO_ID_SP = "";
    SharedPreferences sharedPreferences;
    LinearLayout layout_noting, layout_Show_post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_page);

        service_item = findViewById(R.id.service_item);


        layout_noting = findViewById(R.id.layout_noting);
        layout_Show_post = findViewById(R.id.layout_Show_post);
        parentLayout = findViewById(android.R.id.content);
        sharedPreferences = getSharedPreferences("SP_DATA", Context.MODE_PRIVATE);
        USER_INFO_ID_SP = sharedPreferences.getString("USER_INFO_ID_SP", "0");
        serviceListPanddings = new ArrayList<>();
        if (!isInternetAvailable()) {
            showSnankBar("No Internet Connection");
        }
        serviceListForUser();
    }

    private void serviceListForUser() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_get_user_service, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parcaeLoginResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showSnankBar(error.toString());
                if (pd != null) {
                    pd.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                JSONArray jsonArray = new JSONArray(values.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    ServiceListPandding serviceList = new ServiceListPandding(
                            jsonObject2.getString("SERVICE_ID"),
                            jsonObject2.getString("SERVICE_TYPE_NAME"),
                            jsonObject2.getString("TOKEN_NO"),
                            jsonObject2.getString("LOCATION_NAME"),
                            jsonObject2.getString("SERVICE_CHARGE"),
                            jsonObject2.getString("MOBILE_NUM"),
                            jsonObject2.getString("SERVICE_STATUS"),
                            jsonObject2.getString("REMARKS"),
                            jsonObject2.getString("USER_ADDRESS"),
                            jsonObject2.getString("USER_LATITUDE"),
                            jsonObject2.getString("USER_LONGITUDE"),
                            jsonObject2.getString("CREATE_DATA"),
                            jsonObject2.getString("USER_INFO_NAME"),
                            jsonObject2.getString("USER_PHONE"),
                            jsonObject2.getString("USER_EMAIL")
                    );
                    serviceListPanddings.add(serviceList);
                }
                if (serviceListPanddings.size() <= 0) {
                    layout_noting.setVisibility(View.VISIBLE);
                    layout_Show_post.setVisibility(View.GONE);
                } else {
                    layout_noting.setVisibility(View.GONE);
                    layout_Show_post.setVisibility(View.VISIBLE);
                    ServiceAdapterPandding serviceAdapterPandding = new ServiceAdapterPandding(getApplicationContext(), R.layout.service_page_pandding, serviceListPanddings);
                    service_item.setAdapter(serviceAdapterPandding);


                    service_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (!serviceListPanddings.get(i).getSERVICE_STATUS().equals("F")) {
                                Intent intent = new Intent(ServicePage.this, MapsActivity.class);
                                intent.putExtra("SERVICE_ID", serviceListPanddings.get(i).getSERVICE_ID());
                                intent.putExtra("SERVICE_TYPE_NAME", serviceListPanddings.get(i).getSERVICE_TYPE_NAME());
                                intent.putExtra("TOKEN_NO", serviceListPanddings.get(i).getTOKEN_NO());
                                intent.putExtra("LOCATION_NAME", serviceListPanddings.get(i).getLOCATION_NAME());
                                intent.putExtra("SERVICE_CHARGE", serviceListPanddings.get(i).getSERVICE_CHARGE());
                                intent.putExtra("MOBILE_NUM", serviceListPanddings.get(i).getMOBILE_NUM());
                                intent.putExtra("REMARKS", serviceListPanddings.get(i).getREMARKS());
                                intent.putExtra("USER_ADDRESS", serviceListPanddings.get(i).getUSER_ADDRESS());
                                intent.putExtra("USER_LATITUDE", serviceListPanddings.get(i).getUSER_LATITUDE());
                                intent.putExtra("USER_LONGITUDE", serviceListPanddings.get(i).getUSER_LONGITUDE());
                                intent.putExtra("CREATE_DATA", serviceListPanddings.get(i).getCREATE_DATA());
                                intent.putExtra("USER_INFO_NAME", serviceListPanddings.get(i).getUSER_INFO_NAME());
                                intent.putExtra("USER_PHONE", serviceListPanddings.get(i).getUSER_PHONE());
                                intent.putExtra("USER_EMAIL", serviceListPanddings.get(i).getUSER_EMAIL());
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }

            } else {
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

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) ServicePage.this
                .getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
