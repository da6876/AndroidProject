package oma.soc.bd.com.omanmotorassistance;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static oma.soc.bd.com.omanmotorassistance.LogInPage.UrlPath;

public class RegistrationPage extends AppCompatActivity {
    FloatingActionButton fab_registation;
    TextInputEditText edt_fast_name, edt_last_name, edt_email_address, edt_Phone_number, edt_password;
    String str_singUp_type = "", str_full_name = "", str_fast_name = "", str_last_name = "", str_email_address = "",
            str_phone_number = "", str_password = "", str_location_code = "";
    TextView tv_msg, tv_title;
    Button buttonOk;
    String URLline = UrlPath+"OAA_INSERT_NEW_USER.php";
    String urlGetLocationList = UrlPath+"OAA_GET_LOCATION_ALL.php";
    List<String> LOCATION_CODE = new ArrayList<String>();
    List<String> LOCATION_NAME = new ArrayList<String>();
    List<String> LOCATION_STATUS = new ArrayList<String>();
    ArrayAdapter<String> dataAdapter;
    SharedPreferences sharedPreferences;
    MaterialSpinner spinner_location;
    View parentLayout;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);

        parentLayout = findViewById(android.R.id.content);

        sharedPreferences = getSharedPreferences("OAA_DATA", Context.MODE_PRIVATE);
        spinner_location = findViewById(R.id.spinner_location);
        fab_registation = findViewById(R.id.fab_registation);
        getLocationList();
        fab_registation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });
        edt_fast_name = findViewById(R.id.edt_fast_name);
        edt_last_name = findViewById(R.id.edt_last_name);
        edt_email_address = findViewById(R.id.edt_email_address);
        edt_Phone_number = findViewById(R.id.edt_Phone_number);
        edt_password = findViewById(R.id.edt_password);
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                checkInfo();
                return false;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            str_singUp_type = bundle.getString("SingUpType");
            if (str_singUp_type.equals("Google")) {
                str_full_name = bundle.getString("fullname");
                str_fast_name = bundle.getString("fastName");
                str_last_name = bundle.getString("lastName");
                str_email_address = bundle.getString("emailName");

                edt_fast_name.setText(str_fast_name);
                edt_last_name.setText(str_last_name);
                edt_email_address.setText(str_email_address);
            } else {
                str_phone_number = bundle.getString("PhoneNo");

                edt_Phone_number.setText(str_phone_number);
            }
        }

    }

    private void checkInfo(){
        if (!isInternetAvailable())
        {
            alertDialog(RegistrationPage.this,"Message","Check Your Internet Connection !!");
        }else {
            str_fast_name = edt_fast_name.getText().toString().trim();
            str_last_name = edt_last_name.getText().toString().trim();
            str_phone_number = edt_Phone_number.getText().toString().trim();
            str_email_address = edt_email_address.getText().toString().trim();
            str_password = edt_password.getText().toString().trim();
            if (!str_fast_name.equals("")) {
                if (!str_last_name.equals("")) {
                    if (!str_phone_number.equals("")) {
                        if (!str_email_address.equals("")) {
                            if (!str_password.equals("")) {
                                if (!str_location_code.equals("")) {
                                    pd = ProgressDialog.show(RegistrationPage.this, "Checking Information",
                                            "Please wait...");
                                    registationNow();
                                } else {
                                    showSnackbar("Select Your Location");
                                }
                            } else {
                                showSnackbar("Enter Your Password");
                            }
                        } else {
                            showSnackbar("Enter Your Email");
                        }
                    } else {
                        showSnackbar("Enter Your Phone Number");
                    }
                } else {
                    showSnackbar("Enter Your Last Name");
                }
            } else {
                showSnackbar("Enter Your Fast Name");
            }
        }
    }

    private void showSnackbar(String value) {
        Snackbar.make(parentLayout, value, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    private void getLocationList() {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, urlGetLocationList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LOCATION_CODE.add("00000");
                LOCATION_NAME.add("Select Your Location");
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String responce = jsonObject.getString("responce");
                    JSONObject jsonObject1 = new JSONObject(responce);
                    String status_code = jsonObject1.getString("status_code");
                    String msg = jsonObject1.getString("msg");
                    String values = jsonObject1.getString("values");
                    JSONArray jsonArray = new JSONArray(values.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        LOCATION_CODE.add(jsonObject2.getString("LOCATION_CODE"));
                        LOCATION_NAME.add(jsonObject2.getString("LOCATION_NAME"));
                        LOCATION_STATUS.add(jsonObject2.getString("LOCATION_STATUS"));
                    }

                } catch (JSONException e) {
                    Toast.makeText(RegistrationPage.this, e.toString(), Toast.LENGTH_LONG);
                }


                dataAdapter = new ArrayAdapter<String>(RegistrationPage.this,
                        android.R.layout.simple_spinner_item, LOCATION_NAME);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_location.setAdapter(dataAdapter);
                spinner_location.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                        str_location_code = LOCATION_CODE.get(position);
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void registationNow() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null) {
                    pd.dismiss();
                }
                parceData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pd != null) {
                    pd.dismiss();
                }
                showFailedDialog(RegistrationPage.this, "Failed", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (str_singUp_type.equals("Google")) {
                    params.put("P_USER_INFO_NAME", str_full_name);
                } else {
                    params.put("P_USER_INFO_NAME", str_fast_name + " " + str_last_name);
                }
                params.put("P_LOCATION_CODE", str_location_code);
                params.put("P_USER_FAST_NAME", str_fast_name);
                params.put("P_USER_LAST_NAME", str_last_name);
                params.put("P_USER_PHONE", str_phone_number);
                params.put("P_USER_EMAIL", str_email_address);
                params.put("P_USER_PASSWORD", str_password);
                params.put("P_USER_REFER_NO", "");
                params.put("P_USER_POINT", "");
                params.put("P_USER_ADDRESS", "");
                params.put("P_USER_LATITUDE", "");
                params.put("P_USER_LONGITUDE", "");
                params.put("P_USER_STATUS", "A");
                params.put("P_CREATE_BY", str_fast_name);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void parceData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            String responces = jsonObject.getString("responce");
            JSONObject jsonObject1 = new JSONObject(responces.toString());
            String status_code = jsonObject1.getString("status_code");
            String msg = jsonObject1.getString("msg");
            String values = jsonObject1.getString("values");
            if (status_code.equals("500")) {
                showSuccessDialog(msg, values);
            } else {
                showFailedDialog(RegistrationPage.this, msg, values);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showFailedDialog(Context context, String title, String msg) {
        new AlertDialog.Builder(context).setIcon(R.drawable.ic_face).setTitle(title).setMessage(msg).
                setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void showSuccessDialog(String msg, String values) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationPage.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.success_dilog, null);
        tv_msg = view.findViewById(R.id.tv_msg);
        tv_title = view.findViewById(R.id.tv_title);
        tv_msg.setText(values);
        tv_title.setText(msg);
        buttonOk = view.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Reg_status", "Success");
                editor.commit();
                Intent goNew = new Intent(RegistrationPage.this, LogInPage.class);
                goNew.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goNew);
                finish();
            }
        });
        builder.setView(view);
        builder.show();
    }

    public void  alertDialog(Context context, String title, String message) {new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            }).show();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) RegistrationPage.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
