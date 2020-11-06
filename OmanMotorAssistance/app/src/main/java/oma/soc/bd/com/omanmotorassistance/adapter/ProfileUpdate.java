package oma.soc.bd.com.omanmotorassistance.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oma.soc.bd.com.omanmotorassistance.LogInPage;
import oma.soc.bd.com.omanmotorassistance.R;

import static oma.soc.bd.com.omanmotorassistance.LogInPage.UrlPath;


public class ProfileUpdate extends BottomSheetDialogFragment {
    View parentLayout;

    TextView edt_fast_name, edt_last_name, edt_email_address, edt_Phone_number, edt_password;
    MaterialButton btn_submit;
    String USER_INFO_ID, USER_FAST_NAME, USER_LAST_NAME, USER_PHONE, str_location_code="", USER_EMAIL,
            LOCATION_CODES, USER_ADDRESS, USER_LATITUDE, USER_LONGITUDE, USER_PASSWORD;
    String urlUpdatePorfile = UrlPath + "OAA_UPDATE_USER.php";
    private ProgressDialog pd;
    View v;
    MaterialSpinner spinner_location;

    String urlGetLocationList = UrlPath + "OAA_GET_LOCATION_ALL.php";
    List<String> LOCATION_CODE = new ArrayList<String>();
    List<String> LOCATION_NAME = new ArrayList<String>();
    List<String> LOCATION_STATUS = new ArrayList<String>();
    ArrayAdapter<String> dataAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.profile_update, container, false);


        btn_submit = v.findViewById(R.id.btn_submit);
        edt_fast_name = v.findViewById(R.id.edt_fast_name);
        edt_last_name = v.findViewById(R.id.edt_last_name);
        edt_email_address = v.findViewById(R.id.edt_email_address);
        spinner_location = v.findViewById(R.id.spinner_location);
        edt_Phone_number = v.findViewById(R.id.edt_Phone_number);
        edt_password = v.findViewById(R.id.edt_password);
        getLocationList();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            USER_INFO_ID = bundle.getString("USER_INFO_ID");
            USER_FAST_NAME = bundle.getString("USER_FAST_NAME");
            USER_LAST_NAME = bundle.getString("USER_LAST_NAME");
            USER_PHONE = bundle.getString("USER_PHONE");
            USER_EMAIL = bundle.getString("USER_EMAIL");
            LOCATION_CODES = bundle.getString("LOCATION_CODE");
            USER_ADDRESS = bundle.getString("USER_ADDRESS");
            USER_LATITUDE = bundle.getString("USER_LATITUDE");
            USER_LONGITUDE = bundle.getString("USER_LONGITUDE");
            USER_PASSWORD = bundle.getString("USER_PASSWORD");
        }

        if (USER_FAST_NAME.equals("") || USER_FAST_NAME.equals(null) || USER_FAST_NAME.equals("null")) {
            edt_fast_name.setText("");
        } else {
            edt_fast_name.setText(USER_FAST_NAME);
        }

        if (USER_LAST_NAME.equals("") || USER_LAST_NAME.equals(null) || USER_LAST_NAME.equals("null")) {
            edt_last_name.setText("");
        } else {
            edt_last_name.setText(USER_LAST_NAME);
        }

        if (USER_EMAIL.equals("") || USER_EMAIL.equals(null) || USER_EMAIL.equals("null")) {
            edt_email_address.setText("");
        } else {
            edt_email_address.setText(USER_EMAIL);
        }

        if (USER_PHONE.equals("") || USER_PHONE.equals(null) || USER_PHONE.equals("null")) {
            edt_Phone_number.setText("");
        } else {
            edt_Phone_number.setText(USER_PHONE);
        }

        if (USER_PASSWORD.equals("") || USER_PASSWORD.equals(null) || USER_PASSWORD.equals("null")) {
            edt_password.setText("");
        } else {
            edt_password.setText(USER_PASSWORD);
        }


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataCheck();
            }

        });

        return v;
    }


    private void dataCheck() {
        if (!isInternetAvailable()) {
            Toast.makeText(getContext(), "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            if (!edt_fast_name.getText().toString().equals("")) {
                if (!edt_last_name.getText().toString().equals("")) {
                    if (!edt_email_address.getText().toString().equals("")) {
                        if (!edt_Phone_number.getText().toString().equals("")) {
                            if (!edt_password.getText().toString().equals("")) {
                                if (!str_location_code.equals("")) {
                                    UpdateProfile();
                                    pd = ProgressDialog.show(getContext(), "Checking Information",
                                            "Please wait...");
                                } else {
                                    Toast.makeText(getContext(), "Please Select Your Location", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Enter your Password ", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Please Add your Phone ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please Add Your Address", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please Add Your Last Name", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getContext(), "Please Add Fast Name", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void UpdateProfile() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpdatePorfile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_LOCATION_CODE", str_location_code);
                params.put("P_USER_INFO_NAME", edt_fast_name.getText().toString() + ' ' + edt_last_name.getText().toString());
                params.put("P_USER_FAST_NAME", edt_fast_name.getText().toString());
                params.put("P_USER_LAST_NAME", edt_last_name.getText().toString());
                params.put("P_USER_PASSWORD", edt_password.getText().toString());
                params.put("P_USER_PHONE", edt_Phone_number.getText().toString());
                params.put("P_USER_EMAIL", edt_email_address.getText().toString());
                params.put("P_USER_INFO_ID", USER_INFO_ID);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    protected void parseData(String response) {

        try {
            JSONObject obj = new JSONObject(response);
            String responcse = obj.getString("responce");
            JSONObject obj2 = new JSONObject(responcse);
            String status_code = obj2.getString("status_code");
            String msg = obj2.getString("msg");
            String values = obj2.getString("values");
            if (status_code.equals("500")) {
                if (pd != null) {
                    pd.dismiss();
                }
                alertDialog(getContext(), msg, values);
            } else {
                if (pd != null) {
                    pd.dismiss();
                }
                alertDialogF(getContext(), msg, values);

            }
        } catch (JSONException e) {
            if (pd != null) {
                pd.dismiss();
            }
            e.printStackTrace();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
        }

    }

    protected void alertDialog(android.content.Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(getActivity(), LogInPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dismiss();
                        dialog.dismiss();
                    }
                }).show();
    }

    protected void alertDialogF(android.content.Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        dismiss();
                    }
                }).show();
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
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
                }


                dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, LOCATION_NAME);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }


}
