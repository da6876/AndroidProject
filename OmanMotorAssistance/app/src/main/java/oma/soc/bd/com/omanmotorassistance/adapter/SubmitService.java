package oma.soc.bd.com.omanmotorassistance.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import oma.soc.bd.com.omanmotorassistance.HomePage;
import oma.soc.bd.com.omanmotorassistance.R;
import oma.soc.bd.com.omanmotorassistance.map.BestLocation;
import oma.soc.bd.com.omanmotorassistance.map.MapsPage;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static oma.soc.bd.com.omanmotorassistance.LogInPage.UrlPath;


public class SubmitService extends BottomSheetDialogFragment {
    String ADDRESS = "", USER_INFO_ID = "", USER_PHONE = "", LOGLITUTDE = "", LATTITUDE = "",
            str_service_name = "", str_service_type_id = "", USER_EMAIL = "", LOCATION_CODE = "",
            STATUS_TYPE = "", MSG = "", USER_INFO_NAME_SP = "", USER_ADDRESS_SP = "", USER_INFO_ID_SP = "",
            USER_LATITUDE_SP = "", USER_LONGITUDE_SP = "", USER_PHONE_SP = "",USER_EMAIL_SP = "";
    View parentLayout;

    TextView edt_service_name, edt_remark, edt_phone, edt_address, edt_latitude, edt_longtitude, edt_email;
    MaterialButton btn_submit;
    String URLline = UrlPath+"OAA_ADD_SERVICE.php",TOKEN_NO="";
    private ProgressDialog pd;
    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.submit_service, container, false);


        btn_submit = v.findViewById(R.id.btn_submit);
        edt_service_name = v.findViewById(R.id.edt_service_name);
        edt_phone = v.findViewById(R.id.edt_phone);
        edt_email = v.findViewById(R.id.edt_email);
        edt_remark = v.findViewById(R.id.edt_remark);
        edt_address = v.findViewById(R.id.edt_address);
        edt_latitude = v.findViewById(R.id.edt_latitude);
        edt_longtitude = v.findViewById(R.id.edt_longtitude);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            str_service_type_id = bundle.getString("str_service_type_id");
            str_service_name = bundle.getString("str_service_name");
            USER_INFO_ID = bundle.getString("USER_INFO_ID");
            ADDRESS = bundle.getString("User_address");
            LATTITUDE = bundle.getString("latitude");
            LOGLITUTDE = bundle.getString("longitude");
            USER_PHONE = bundle.getString("USER_PHONE");
            USER_EMAIL = bundle.getString("USER_EMAIL");
            LOCATION_CODE = bundle.getString("LOCATION_CODE");
        }

        if (str_service_name.equals("") || str_service_name.equals(null) || str_service_name.equals("null")) {
            edt_service_name.setText("");
        } else {
            edt_service_name.setText(str_service_name);
        }

        if (USER_PHONE.equals("") || USER_PHONE.equals(null) || USER_PHONE.equals("null")) {
            edt_phone.setText("");
        } else {
            edt_phone.setText(USER_PHONE);
        }

        if (USER_EMAIL.equals("") || USER_EMAIL.equals(null) || USER_EMAIL.equals("null")) {
            edt_email.setText("");
        } else {
            edt_email.setText(USER_EMAIL);
        }

        if (ADDRESS.equals("") || ADDRESS.equals(null) || ADDRESS.equals("null")) {
            edt_address.setText("");
        } else {
            edt_address.setText(ADDRESS);
        }

        if (LOGLITUTDE.equals("") || LOGLITUTDE.equals(null) || LOGLITUTDE.equals("null")) {
            edt_longtitude.setText("");
        } else {
            edt_longtitude.setText(LOGLITUTDE);
        }

        if (LATTITUDE.equals("") || LATTITUDE.equals(null) || LATTITUDE.equals("null")) {
            edt_latitude.setText("");
        } else {
            edt_latitude.setText(LATTITUDE);
        }

        //edt_address.setText("Latitude = "+lat+"Longitude = "+lon);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dataCheck();
            }

        });
        String SALTCHARS = "FYH123456789";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        TOKEN_NO = salt.toString();
        return v;
    }


    private void dataCheck() {
            if (!edt_phone.getText().toString().equals("")) {
                    if (!edt_address.getText().toString().equals("")) {
                        if (!edt_latitude.getText().toString().equals("")) {
                            if (!edt_longtitude.getText().toString().equals("")) {
                                submitService();
                                pd = ProgressDialog.show(getContext(), "Checking Information",
                                        "Please wait...");
                            } else {
                                Toast.makeText(getContext(), "Could Not Find The Location ", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Could Not Find The Location", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(),"Please Add Your Address", Toast.LENGTH_LONG).show();
                    }

            } else {
                Toast.makeText(getContext(), "Please Add Phone", Toast.LENGTH_LONG).show();
            }


    }

    private void submitService() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
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
                params.put("P_TOKEN_NO", TOKEN_NO);
                params.put("P_USER_INFO_ID", USER_INFO_ID);
                params.put("P_MOBILE_NUM", edt_phone.getText().toString());
                params.put("P_SERVICE_TYPE_ID", str_service_type_id);
                params.put("P_SERVICE_STATUS", "P");
                params.put("P_REMARKS", edt_remark.getText().toString());
                params.put("P_USER_ADDRESS", edt_address.getText().toString());
                params.put("P_USER_LATITUDE", edt_latitude.getText().toString());
                params.put("P_USER_LONGITUDE", edt_longtitude.getText().toString());
                params.put("P_LOCATION_CODE", LOCATION_CODE);
                params.put("P_CREATE_BY", USER_INFO_ID);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void parseData(String response) {

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
                JSONArray jsonArray = new JSONArray(values.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    STATUS_TYPE=jsonObject2.getString("STATUS_TYPE");
                    MSG=jsonObject2.getString("MSG");
                    USER_INFO_ID_SP=jsonObject2.getString("USER_INFO_ID_SP");
                    USER_INFO_NAME_SP=jsonObject2.getString("USER_INFO_NAME_SP");
                    USER_ADDRESS_SP=jsonObject2.getString("USER_ADDRESS_SP");
                    USER_EMAIL_SP=jsonObject2.getString("USER_EMAIL_SP");
                    USER_PHONE_SP=jsonObject2.getString("USER_PHONE_SP");
                    USER_LATITUDE_SP=jsonObject2.getString("USER_LATITUDE_SP");
                    USER_LONGITUDE_SP=jsonObject2.getString("USER_LONGITUDE_SP");
                }
                //alertDialog(getContext(), STATUS_TYPE, MSG);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.provider_dilog, null);
                final TextView tv_tolet_con_name =  mView.findViewById(R.id.tv_tolet_con_name);
                final TextView tv_address =  mView.findViewById(R.id.tv_address);
                final TextView tv_mobile =  mView.findViewById(R.id.tv_mobile);
                final TextView tv_email1 =  mView.findViewById(R.id.tv_email1);
                final MaterialButton btn_submit =  mView.findViewById(R.id.btn_submit);

                tv_tolet_con_name.setText(USER_INFO_NAME_SP);
                tv_address.setText(USER_ADDRESS_SP);
                tv_mobile.setText(USER_PHONE_SP);
                tv_email1.setText(USER_EMAIL_SP);

                mBuilder.setView(mView);
                final AlertDialog dialogs = mBuilder.create();
                dialogs.show();
                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext(),MapsPage.class);
                        intent.putExtra("USER_INFO_ID_SP",USER_INFO_ID_SP);
                        intent.putExtra("USER_INFO_NAME_SP",USER_INFO_NAME_SP);
                        startActivity(intent);
                        //dialogInterface.dismiss();
                        dialogs.dismiss();
                    }
                });


            } else {
                if (pd != null) {
                    pd.dismiss();
                }JSONArray jsonArray = new JSONArray(values.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    STATUS_TYPE=jsonObject2.getString("STATUS_TYPE");
                    MSG=jsonObject2.getString("MSG");
                }
                alertDialog1(getContext(),STATUS_TYPE, MSG);

            }
        } catch (JSONException e) {
            if (pd != null) {
                pd.dismiss();
            }
            e.printStackTrace();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
        }

    }

    public void alertDialog1(android.content.Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        //submitService();
                        dialog.dismiss();
                    }
                }).show();
    }

}
