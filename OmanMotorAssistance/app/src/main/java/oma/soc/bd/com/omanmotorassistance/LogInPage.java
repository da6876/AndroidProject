package oma.soc.bd.com.omanmotorassistance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import oma.soc.bd.com.omanmotorassistance.map.BestLocation;

public class LogInPage extends AppCompatActivity {
    FloatingActionButton fab_log_in;
    TextInputEditText edt_phone_number, edt_Password;
    public static String UrlPath = "http://103.91.54.60/apps/FYH/";
    String loginUrl = UrlPath+"OAA_USER_LOGIN.php";
    View parentLayout;
    SharedPreferences sharedPreferences;
    String str_address="",str_laticude="",str_longicude="",str_Login_status="",STATUS_TYPE="",MSG="",strChkRemenber="",strusername="",strpassword="";
    private ProgressDialog pd;
    CheckBox cbSavePass;
    double lat=0.0,lon=0.0;
    BestLocation bestLocation;
    boolean b;
    TextView tv_sing_here;
    List<Address> addresses = null;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_page);

        sharedPreferences = getSharedPreferences("OAA_DATA", Context.MODE_PRIVATE);
        str_Login_status=sharedPreferences.getString("LOGIN_STATUS","0");

        geocoder = new Geocoder(this, Locale.getDefault());

        parentLayout = findViewById(android.R.id.content);
        cbSavePass = findViewById(R.id.cbSavePass);
        tv_sing_here = findViewById(R.id.tv_sing_here);
        tv_sing_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInPage.this,LogInOption.class));
            }
        });
        edt_phone_number = findViewById(R.id.edt_phone_number);
        edt_Password = findViewById(R.id.edt_Password);
        edt_Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                checkLoginInfo();
                return false;
            }
        });
        fab_log_in = findViewById(R.id.fab_log_in);
        fab_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLoginInfo();
            }
        });
        strChkRemenber = sharedPreferences.getString("Check", "");
        if (strChkRemenber.equals("1")) {
            strusername = sharedPreferences.getString("username", "");
            strpassword = sharedPreferences.getString("password", "");
            //strChkRemenber=sp.getString("Check", "1");
            edt_phone_number.setText(strusername);
            edt_Password.setText(strpassword);
            // cbSavePass.setChecked(true);
        }

    }

    private void checkLoginInfo(){
        if (!isInternetAvailable())
        {
            alertDialog(LogInPage.this,"Message","Check Your Internet Connection !!");
        }else {

            if (lat != 0.0 && lon != 0.0) {
                if (!edt_phone_number.getText().toString().trim().equals("")) {
                    if (!edt_Password.getText().toString().trim().equals("")) {
                        try {
                            addresses = geocoder.getFromLocation(lat, lon, 1);
                            str_address = addresses.get(0).getAddressLine(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        loginUser();
                        pd = ProgressDialog.show(LogInPage.this, "Checking Information","Please wait...");
                    } else {
                        alertDialog(LogInPage.this, "Message", "Enter Your Email or Phone No");
                    }
                } else {
                    alertDialog(LogInPage.this, "Message", "Enter The Password");
                }
            }else{
                alertDialog1(LogInPage.this,"Message","Could not find your Location");

            }
        }
    }

    private void loginUser() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parcaeLoginResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnankBar(error.toString());

                alertDialog(LogInPage.this,"Sorry","Connection Time Out !");
                if (pd != null) {
                    pd.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_USER_NAME", edt_phone_number.getText().toString().trim());
                parameters.put("P_USER_LATITUDE", String.valueOf(lat));
                parameters.put("P_USER_LONGITUDE", String.valueOf(lon));
                parameters.put("P_USER_ADDRESS", str_address);
                parameters.put("P_USER_PASSWORD", edt_Password.getText().toString().trim());
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void parcaeLoginResponse(String toString) {

        if (pd != null) {
            pd.dismiss();
        }
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
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USER_INFO_ID", jsonObject2.getString("USER_INFO_ID"));
                    editor.putString("USER_INFO_NAME", jsonObject2.getString("USER_INFO_NAME"));
                    editor.putString("USER_FAST_NAME", jsonObject2.getString("USER_FAST_NAME"));
                    editor.putString("USER_LAST_NAME", jsonObject2.getString("USER_LAST_NAME"));
                    editor.putString("USER_PHONE", jsonObject2.getString("USER_PHONE"));
                    editor.putString("USER_EMAIL", jsonObject2.getString("USER_EMAIL"));
                    editor.putString("USER_REFER_NO", jsonObject2.getString("USER_REFER_NO"));
                    editor.putString("USER_POINT", jsonObject2.getString("USER_POINT"));
                    editor.putString("LOCATION_CODE", jsonObject2.getString("LOCATION_CODE"));
                    editor.putString("USER_ADDRESS", str_address);
                    editor.putString("USER_LATITUDE", str_laticude);
                    editor.putString("USER_LONGITUDE", str_longicude);
                    editor.putString("USER_PASSWORD", edt_Password.getText().toString());
                    editor.putString("USER_STATUS", jsonObject2.getString("USER_STATUS"));
                    editor.putString("LOGIN_STATUS", "OK");
                    editor.commit();
                    editor.apply();
                    b = cbSavePass.isChecked();
                    if (b) {
                        editor.putString("Check", "1");
                        editor.putString("username", edt_phone_number.getText().toString());
                        editor.putString("password", edt_Password.getText().toString());
                    } else {
                        editor.putString("Check", "0");
                    }
                    editor.commit();
                }
                startActivity(new Intent(LogInPage.this,HomePage.class));
                finish();

            }else{
                if (pd != null) {
                    pd.dismiss();
                }
                JSONArray jsonArray=new JSONArray(values.toString());
                for (int i =0; i< jsonArray.length();i++) {
                    JSONObject jsonObject2 =jsonArray.getJSONObject(i);

                      STATUS_TYPE=jsonObject2.getString("STATUS_TYPE");
                     MSG= jsonObject2.getString("MSG");
                }
                alertDialog(LogInPage.this,STATUS_TYPE,MSG);
            }
        } catch (JSONException e) {
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
        ConnectivityManager cm = (ConnectivityManager) LogInPage.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void CheckGPS() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("No GPS Data")
                    .setMessage("Please enable your gps settings")
                    .setPositiveButton("Enable",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int arg1) {
                                    Intent gps = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(gps);
                                    dialog.dismiss();
                                }
                            }).show();
        }
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

    public void  alertDialog1(Context context, String title, String message) {new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    // TODO Auto-generated method stub
                    CheckGPS();
                }
            }).show();
    }

    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(LogInPage.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LogInPage.this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }else{
                bestLocation = new BestLocation(LogInPage.this);
                lat = bestLocation.getlat();
                lon = bestLocation.getLon();
            }
        } else {
            bestLocation = new BestLocation(LogInPage.this);
            lat = bestLocation.getlat();
            lon = bestLocation.getLon();
        }
        CheckGPS();
    }

}
