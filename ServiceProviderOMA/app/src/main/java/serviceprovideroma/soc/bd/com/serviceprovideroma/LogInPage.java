package serviceprovideroma.soc.bd.com.serviceprovideroma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import serviceprovideroma.soc.bd.com.serviceprovideroma.map.GetLocationInfo;
import serviceprovideroma.soc.bd.com.serviceprovideroma.model.CheckAssementService;

public class LogInPage extends AppCompatActivity {
    FloatingActionButton fab_log_in;
    TextInputEditText edt_phone_number, edt_Password;
    public static String UrlPath="http://103.91.54.60/apps/FYH/";
    String loginUrl = UrlPath+"OAA_USER_LOGIN_SP.php";
    View parentLayout;
    SharedPreferences sharedPreferences;
    Geocoder geocoder;
    TextView tv_sing_up;
    GetLocationInfo getLastLocation;
    List<Address> addresses = null;
    String str_address = "", str_laticude = "", str_longicude = "",strusername = "",strpassword = "",
            str_Login_status = "", STATUS_TYPE = "", MSG = "", strChkRemenber = "";
    private ProgressDialog pd;
    double lat = 0.0, lon = 0.0;
    boolean b;
    CheckBox cbSavePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_page);

        sharedPreferences = getSharedPreferences("SP_DATA", Context.MODE_PRIVATE);
        str_Login_status = sharedPreferences.getString("LOGIN_STATUS", "0");

        parentLayout = findViewById(android.R.id.content);
        tv_sing_up = findViewById(R.id.tv_sing_up);
        tv_sing_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInPage.this,LogInOption.class));
            }
        });

        startService(new Intent(this, CheckAssementService.class));
        cbSavePass = findViewById(R.id.cbSavePass);


        edt_phone_number = findViewById(R.id.edt_phone_number);
        edt_Password = findViewById(R.id.edt_Password);
        edt_Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                checkLogInInfo();
                return false;
            }
        });
        fab_log_in = findViewById(R.id.fab_log_in);
        fab_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLogInInfo();
            }
        });
        strChkRemenber = sharedPreferences.getString("Check", "");
        if (strChkRemenber.equals("1")) {
            strusername = sharedPreferences.getString("username", "");
            strpassword = sharedPreferences.getString("password", "");
            edt_phone_number.setText(strusername);
            edt_Password.setText(strpassword);
        }

        geocoder = new Geocoder(this, Locale.getDefault());

    }

    private void checkLogInInfo() {
        if (isInternetAvailable()) {
            if (!edt_phone_number.getText().toString().trim().equals("")) {
                if (!edt_Password.getText().toString().trim().equals("")) {
                    if (lat != 0.0 && lon != 0.0) {
                        try {
                            addresses = geocoder.getFromLocation(lat, lon, 1);
                            str_laticude = String.valueOf(lat);
                            str_longicude = String.valueOf(lon);
                            str_address = addresses.get(0).getAddressLine(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        loginUser();
                        pd = ProgressDialog.show(LogInPage.this, "Checking Information",
                                "Please wait...");
                    } else {
                        alertDialog1(LogInPage.this, "Message", "Could not find your Location");
                    }
                } else {
                    alertDialog(LogInPage.this, "Message","Enter Your Email or Phone No");
                }
            } else {
                alertDialog(LogInPage.this, "Message","Enter The Password");
            }
        } else {
            alertDialog(LogInPage.this, "Message", "Check Your Internet Connection !!");
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
                alertDialog(LogInPage.this, "Sorry","Server Don't Responded ! ");
                if (pd != null) {
                    pd.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_USER_NAME_SP", edt_phone_number.getText().toString().trim());
                parameters.put("P_USER_LATITUDE_SP", str_laticude);
                parameters.put("P_USER_LONGITUDE_SP", str_longicude);
                parameters.put("P_USER_ADDRESS_SP", str_address);
                parameters.put("P_USER_PASSWORD_SP", edt_Password.getText().toString().trim());
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
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USER_INFO_ID_SP", jsonObject2.getString("USER_INFO_ID_SP"));
                    editor.putString("LOCATION_CODE", jsonObject2.getString("LOCATION_CODE"));
                    editor.putString("USER_INFO_NAME_SP", jsonObject2.getString("USER_INFO_NAME_SP"));
                    editor.putString("USER_FAST_NAME_SP", jsonObject2.getString("USER_FAST_NAME_SP"));
                    editor.putString("USER_LAST_NAME_SP", jsonObject2.getString("USER_LAST_NAME_SP"));
                    editor.putString("USER_PASSWORD", jsonObject2.getString("USER_PASSWORD"));
                    editor.putString("USER_PHONE_SP", jsonObject2.getString("USER_PHONE_SP"));
                    editor.putString("USER_EMAIL_SP", jsonObject2.getString("USER_EMAIL_SP"));
                    editor.putString("USER_REFER_NO_SP", jsonObject2.getString("USER_REFER_NO_SP"));
                    editor.putString("USER_POINT_SP", jsonObject2.getString("USER_POINT_SP"));
                    editor.putString("USER_ADDRESS_SP", jsonObject2.getString("USER_ADDRESS_SP"));
                    editor.putString("USER_LATITUDE_SP", jsonObject2.getString("USER_LATITUDE_SP"));
                    editor.putString("USER_LONGITUDE_SP", jsonObject2.getString("USER_LONGITUDE_SP"));
                    editor.putString("USER_STATUS_SP", jsonObject2.getString("USER_STATUS_SP"));
                    editor.putString("SEV_PRO_NAME", jsonObject2.getString("SEV_PRO_NAME"));
                    editor.putString("SEV_DELAIL", jsonObject2.getString("SEV_DELAIL"));
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
                startActivity(new Intent(LogInPage.this, MainActivity.class));
                finish();

            } else {
                if (pd != null) {
                    pd.dismiss();
                }
                JSONArray jsonArray = new JSONArray(values.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                    STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                    MSG = jsonObject2.getString("MSG");
                }
                alertDialog(LogInPage.this, STATUS_TYPE,MSG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (pd != null) {
                pd.dismiss();
            }
        }

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

    public void alertDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
    }

    public void alertDialog1(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        CheckGPS();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckGPS();
        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(LogInPage.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LogInPage.this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                getLastLocation = new GetLocationInfo(LogInPage.this);
                lat = getLastLocation.getlat();
                lon = getLastLocation.getLon();
            }
        } else {
            getLastLocation = new GetLocationInfo(LogInPage.this);
            lat = getLastLocation.getlat();
            lon = getLastLocation.getLon();
        }
    }
}
