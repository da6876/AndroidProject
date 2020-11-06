package soc.bd.com.findmehere;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import soc.bd.com.findmehere.mappage.GetLocationInfo;

public class LoginPage extends AppCompatActivity {
    RelativeLayout rellay1, rellay2;
    Button btn_logIn, btn_SingUp, btn_ForgetPassword;
    EditText edt_UserName, edt_Password;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };
    public static String rootUrl = "http://103.91.54.60/apps/FMH/";

    GetLocationInfo getLastLocation;
    List<Address> addresses = null;
    String str_address = "", str_laticude = "", str_longicude = "";
    double lat = 0.0, lon = 0.0;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);


        geocoder = new Geocoder(this, Locale.getDefault());
        btn_logIn = findViewById(R.id.btn_logIn);
        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });
        btn_SingUp = findViewById(R.id.btn_SingUp);
        btn_SingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, OptionLogin.class));
            }
        });
        btn_ForgetPassword = findViewById(R.id.btn_ForgetPassword);
        btn_ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, FastPage.class));
            }
        });
        edt_UserName = findViewById(R.id.edt_UserName);
        edt_Password = findViewById(R.id.edt_Password);
        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash
    }

    private void checkInfo() {
        if (!isInternetAvailable()) {
            showDialog("Message", "Please Check Your Internet Connection");
        } else {
            if (edt_UserName.getText().toString().trim().equals("")) {
                if (edt_Password.getText().toString().trim().equals("")) {
                    if (lat != 0.0 && lon != 0.0) {
                        logInNow();
                    } else {
                        showDialog("Message", "Could not find your Location");
                    }
                } else {
                    showDialog("Message", "Enter Your User Name");
                }
            } else {
                showDialog("Message", "Enter Your User Password");
            }
        }
    }

    private void logInNow() {
        startActivity(new Intent(LoginPage.this, Home.class));
    }

    private void showDialog(String Title, String Msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Title);
        builder.setMessage(Msg);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) LoginPage.this
                .getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
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
                                public void onClick(DialogInterface dialog, int arg1) {
                                    Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(gps);
                                    dialog.dismiss();
                                }
                            }).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckGPS();
        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(LoginPage.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginPage.this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                getLastLocation = new GetLocationInfo(LoginPage.this);
                lat = getLastLocation.getlat();
                lon = getLastLocation.getLon();
            }
        } else {
            getLastLocation = new GetLocationInfo(LoginPage.this);
            lat = getLastLocation.getlat();
            lon = getLastLocation.getLon();
        }
    }

}
