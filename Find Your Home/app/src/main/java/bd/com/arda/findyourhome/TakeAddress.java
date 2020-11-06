package bd.com.arda.findyourhome;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class TakeAddress extends AppCompatActivity {
    FloatingActionButton fab_Compleate;
    MaterialButton btn_No;
    TextInputEditText edt_address, edt_latitude, edt_longtitude;
    String URLline = "http://103.91.54.60/apps/FYH/FYH_USER_REGISTATION.php";
    private ProgressDialog pd;
    FusedLocationProviderClient mFusedLocationClient;
    Geocoder geocoder;
    List<Address> addresses;
    String ssaddress = "", area = "", postcode = "", fullAddress = "",
            str_Phone = "", str_Email = "", str_Password = "", str_Name = "", str_Gender = "", REFER_ID = "", address = "", latichud = "", longuchud = "";
    private static final int PLACE_PICKER_REQUEST = 1;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_address);

        sharedpreferences = getSharedPreferences("FYH_UserData", Context.MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            str_Phone = bundle.getString("Phone");
        str_Email = bundle.getString("Email");
        str_Password = bundle.getString("Password");
        str_Name = bundle.getString("Name");
        str_Gender = bundle.getString("Gender");

        edt_address = findViewById(R.id.edt_address);
        edt_latitude = findViewById(R.id.edt_latitude);
        edt_longtitude = findViewById(R.id.edt_longtitude);
        btn_No = findViewById(R.id.btn_No);
        btn_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleMap();
            }
        });

        fab_Compleate = findViewById(R.id.fab_Compleate);
        fab_Compleate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = edt_address.getText().toString().trim();
                latichud = edt_latitude.getText().toString().trim();
                longuchud = edt_longtitude.getText().toString().trim();
                if (!address.equals("")) {
                    if (!latichud.equals("") && !longuchud.equals("")) {
                        pd = ProgressDialog.show(TakeAddress.this, "Checking Information",
                                "Please wait...");
                        loginUser();
                    } else {
                        Snackbar.make(v, "Select Your Address.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(v, "Select Your Location.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        String SALTCHARS = "FYH123456789";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        REFER_ID = salt.toString();
    }

    private void goToGoogleMap() {

        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(intentBuilder.build(TakeAddress.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException es) {
            es.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stringBuilder = new StringBuilder();
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                final CharSequence address = place.getAddress();

                if (address.equals(""))
                    try {
                        addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                        ssaddress = addresses.get(0).getAddressLine(0);
                        area = addresses.get(0).getLocality();
                        postcode = addresses.get(0).getPostalCode();
                        fullAddress = ssaddress + "," + area + "," + postcode;
                        edt_address.setText(fullAddress);
                        edt_latitude.setText(latitude);
                        edt_longtitude.setText(longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                else
                    edt_address.setText(address);
                edt_latitude.setText(latitude);
                edt_longtitude.setText(longitude);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loginUser() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(TakeAddress.this,response,Toast.LENGTH_LONG).show();
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Toast.makeText(TakeAddress.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_USER_NAME", str_Name);
                params.put("P_USER_EMAIL", str_Email);
                params.put("P_USER_PASSWORD", str_Password);
                params.put("P_LATTITUDE", edt_latitude.getText().toString().trim());
                params.put("P_LOGLITUTDE", edt_longtitude.getText().toString().trim());
                params.put("P_ADDRESS", edt_address.getText().toString().trim());
                params.put("P_USER_REFER_ID", REFER_ID);
                params.put("P_USER_PHONE", str_Phone);
                params.put("P_STATUS", "");
                params.put("P_GENDER", str_Gender);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
            if (status_code.equals("200")) {
                if (pd != null) {
                    pd.dismiss();
                }
                alertDialog(TakeAddress.this, msg, values);
            } else {
                if (pd != null) {
                    pd.dismiss();
                }
                alertDialog1(TakeAddress.this, msg, values);
            }
        } catch (JSONException e) {
            if (pd != null) {
                pd.dismiss();
            }
            e.printStackTrace();
        }

    }

    public void alertDialog1(android.content.Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Registation_status", "Ok");

                        editor.commit();
                        Intent goNew = new Intent(TakeAddress.this, LogInPage.class);
                        goNew.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(goNew);
                        finish();
                    }
                }).show();
    }

    public void alertDialog(android.content.Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
    }
}

