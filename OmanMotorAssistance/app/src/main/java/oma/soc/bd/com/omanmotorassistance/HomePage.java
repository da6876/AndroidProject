package oma.soc.bd.com.omanmotorassistance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.jaredrummler.materialspinner.MaterialSpinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import oma.soc.bd.com.omanmotorassistance.adapter.SubmitService;
import oma.soc.bd.com.omanmotorassistance.map.BestLocation;

import static oma.soc.bd.com.omanmotorassistance.LogInPage.UrlPath;


public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    MaterialSpinner spinner;
    MaterialButton btn_service_submit;

    View parentLayout;
    SharedPreferences sharedPreferences;
    ArrayList<String> SEV_PRO_NAME = new ArrayList<>();
    ArrayList<String> USER_LATITUDE_SP = new ArrayList<>();
    ArrayList<String> USER_LONGITUDE_SP = new ArrayList<>();

    List<String> SERVICE_TYPE_ID = new ArrayList<String>();
    List<String> SERVICE_TYPE_NAME = new ArrayList<String>();
    List<String> SERVICE_CHARGE = new ArrayList<String>();

    private GoogleMap mMap;
    ArrayList<LatLng> locAll = null;
    TextView tv_userName, tv_user_Email;
    Timer timer = null;
    double latitude = 0.0, longitude = 0.0;
    int zoomFlag = 0;
    Marker marker = null;
    HashMap<String, Marker> hashMapMarker = null;
    String urlGetServiceList = UrlPath+"OAA_GET_SERVICE_LIST.php",
            urlGetLocationist = UrlPath+"OAA_USER_INFO_SP_LOCATION.php";
    String str_service_type_id = "", str_service_name = "", USER_INFO_ID = "", USER_PHONE = "", USER_EMAIL = "", USER_INFO_NAME = "", LOCATION_CODE = "";
    ArrayAdapter<String> dataAdapter;

    BestLocation location = null;
    Geocoder geocoder;
    List<Address> addresses = null;
    String str_address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        CheckGPS();
        sharedPreferences = getSharedPreferences("OAA_DATA", Context.MODE_PRIVATE);
        USER_INFO_ID = sharedPreferences.getString("USER_INFO_ID", "");
        USER_PHONE = sharedPreferences.getString("USER_PHONE", "");
        USER_EMAIL = sharedPreferences.getString("USER_EMAIL", "");
        LOCATION_CODE = sharedPreferences.getString("LOCATION_CODE", "");
        USER_INFO_NAME = sharedPreferences.getString("USER_INFO_NAME", "");

        geocoder = new Geocoder(this, Locale.getDefault());

        tv_userName = findViewById(R.id.tv_userName);
        tv_user_Email = findViewById(R.id.tv_user_Email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        parentLayout = findViewById(android.R.id.content);


        spinner = findViewById(R.id.spinner);
        btn_service_submit = findViewById(R.id.btn_service_submit);
        btn_service_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (str_service_type_id.equals("")) {
                    showSnankBar("Select service Type !!");
                } else {
                    if (latitude != 0.0 && longitude != 0.0) {
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            str_address = addresses.get(0).getAddressLine(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("str_service_type_id", str_service_type_id);
                        bundle.putString("str_service_name", str_service_name);
                        bundle.putString("User_address", str_address);
                        bundle.putString("latitude", String.valueOf(latitude));
                        bundle.putString("longitude", String.valueOf(longitude));
                        bundle.putString("USER_INFO_ID", USER_INFO_ID);
                        bundle.putString("USER_PHONE", USER_PHONE);
                        bundle.putString("USER_EMAIL", USER_EMAIL);
                        bundle.putString("LOCATION_CODE", LOCATION_CODE);
                        SubmitService toletDetailsFragment = new SubmitService();
                        toletDetailsFragment.setArguments(bundle);
                        toletDetailsFragment.show(getSupportFragmentManager(), toletDetailsFragment.getTag());
                    } else {
                        showSnankBar("Could Not Find Your Location !!");

                    }
                }
            }
        });
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                str_service_type_id = SERVICE_TYPE_ID.get(position);
                str_service_name = SERVICE_TYPE_NAME.get(position);
            }
        });
        getServiceList();
        //getLocationList();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView tv_userName = (TextView) headerView.findViewById(R.id.tv_userName);
        TextView tv_user_Email = (TextView) headerView.findViewById(R.id.tv_user_Email);
        tv_userName.setText(USER_INFO_NAME);
        tv_user_Email.setText(USER_EMAIL);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomePage.this);
    }

    private void getServiceList() {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, urlGetServiceList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SERVICE_TYPE_ID.add("00000");
                SERVICE_TYPE_NAME.add("Select Your Service");
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
                        SERVICE_TYPE_ID.add(jsonObject2.getString("SERVICE_TYPE_ID"));
                        SERVICE_TYPE_NAME.add(jsonObject2.getString("SERVICE_TYPE_NAME"));
                        SERVICE_CHARGE.add(jsonObject2.getString("SERVICE_CHARGE"));
                    }

                } catch (JSONException e) {
                    Toast.makeText(HomePage.this, e.toString(), Toast.LENGTH_LONG);
                }


                dataAdapter = new ArrayAdapter<String>(HomePage.this,
                        android.R.layout.simple_spinner_item, SERVICE_TYPE_NAME);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getLocationList() {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, urlGetLocationist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                        SEV_PRO_NAME.add(jsonObject2.getString("SEV_PRO_NAME"));
                        USER_LATITUDE_SP.add(jsonObject2.getString("USER_LATITUDE_SP"));
                        USER_LONGITUDE_SP.add(jsonObject2.getString("USER_LONGITUDE_SP"));
                    }

                } catch (JSONException e) {
                    Toast.makeText(HomePage.this, e.toString(), Toast.LENGTH_LONG);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_LOCATION_CODE", LOCATION_CODE);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(HomePage.this, HomePage.class));
            finish();
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(HomePage.this, ServicePage.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(HomePage.this, ProfilePage.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(HomePage.this, AboutPage.class));
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_send) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("LOGIN_STATUS", "");
            editor.commit();
            Intent goNew = new Intent(HomePage.this, LogInPage.class);
            goNew.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goNew);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locAll = new ArrayList<>();
        for (int i = 0; i < SEV_PRO_NAME.size(); i++) {
            LatLng latLng = new LatLng(Double.valueOf(USER_LATITUDE_SP.get(i)), Double.valueOf(USER_LONGITUDE_SP.get(i)));
            mMap.addMarker(new MarkerOptions().position(latLng).title(SEV_PRO_NAME.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            locAll.add(latLng);
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateView();
            }
        }, 0, 10000);


    }

    public void updateView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LatLng sydney4 = new LatLng(latitude, longitude);
                if (latitude != 0.0 && longitude != 0.0) {
                    if (zoomFlag == 1) {
                        marker = hashMapMarker.get("XYZ");
                        marker.remove();
                        hashMapMarker.remove("XYZ");
                    }
                    hashMapMarker = new HashMap<>();
                    marker = mMap.addMarker(new MarkerOptions().position(sydney4).title("Your location").icon(BitmapDescriptorFactory.fromResource(R.drawable.my_loc)));
                    hashMapMarker.put("XYZ", marker);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney4));
                    //mMap.setMyLocationEnabled(true);
                    if (zoomFlag == 0) {
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 1100, null);
                    }

                    /*for (int a = 0; a < locAll.size(); a++) {
                        String url = getDirectionsUrl(sydney4, locAll.get(a));

                        MapsActivity.DownloadTask downloadTask = new MapsActivity.DownloadTask();
                        downloadTask.execute(url);
                    }*/
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 1100, null);
                    zoomFlag = 1;

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckGPS();
        /*CustomarNumberSp customarNumberSp=new CustomarNumberSp(this);
        customarNumberSp.execute();
        GangLatLon gangLatLon=new GangLatLon(this);
        gangLatLon.execute();*/
        getLocationList();

        location = new BestLocation(HomePage.this);
        latitude = location.getlat();
        longitude = location.getLon();

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

    public void CheckGPS() {
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
}
