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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.provider.Settings;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import serviceprovideroma.soc.bd.com.serviceprovideroma.adapter.ServiceAdapterPandding;
import serviceprovideroma.soc.bd.com.serviceprovideroma.map.GetLocationInfo;
import serviceprovideroma.soc.bd.com.serviceprovideroma.map.MapsActivity;
import serviceprovideroma.soc.bd.com.serviceprovideroma.model.ServiceListPandding;

import static serviceprovideroma.soc.bd.com.serviceprovideroma.LogInPage.UrlPath;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    SharedPreferences sharedPreferences;
    LinearLayout LL_Map, LL_PanddingList;
    ListView lv_panddingList;
    ArrayList<ServiceListPandding> serviceListPanddings;
    String USER_LATITUDE_SP = "", USER_EMAIL_SP = "", USER_INFO_ID_SP = "", USER_INFO_NAME_SP = "";
    private ProgressDialog pd;
    String url_get_user_service = UrlPath + "OAA_GET_SERVICE_LIST_FOR_SP.php";
    String url_send_location = UrlPath + "OAA_UPD_USER_INFO_SP_LOC.php";
    LinearLayout layout_noting, layout_Show_post;
    Timer timer = null;
    GetLocationInfo getLastLocation=null;
    Geocoder geocoder;
    int zoomFlag = 0;
    Marker marker = null;
    HashMap<String, Marker> hashMapMarker = null;
    double latitude = 0.0, longitude = 0.0;
    List<Address> addresses = null;
    String str_address = "", str_laticude = "", str_longicude = "",
            str_location_code = "", STATUS_TYPE = "", MSG = "", P_USER_STATUS_SP = "O";
    MenuItem menuOffline, menuOnline;
    View parentLayout;
    boolean onlineStatus = true;
    MaterialButton btn_show_service_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("SP_DATA", Context.MODE_PRIVATE);
        USER_LATITUDE_SP = sharedPreferences.getString("USER_LATITUDE_SP", "0");
        USER_LATITUDE_SP = sharedPreferences.getString("USER_LATITUDE_SP", "0");
        USER_INFO_ID_SP = sharedPreferences.getString("USER_INFO_ID_SP", "0");
        USER_INFO_NAME_SP = sharedPreferences.getString("USER_INFO_NAME_SP", "0");
        USER_EMAIL_SP = sharedPreferences.getString("USER_EMAIL_SP", "0");

        parentLayout = findViewById(android.R.id.content);
        btn_show_service_list = findViewById(R.id.btn_show_service_list);
        btn_show_service_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LL_PanddingList.setVisibility(View.VISIBLE);
                LL_Map.setVisibility(View.GONE);
            }
        });
        LL_Map = findViewById(R.id.LL_Map);
        LL_PanddingList = findViewById(R.id.LL_PanddingList);
        lv_panddingList = findViewById(R.id.lv_panddingList);
        layout_noting = findViewById(R.id.layout_noting);
        layout_Show_post = findViewById(R.id.layout_Show_post);
        geocoder = new Geocoder(this, Locale.getDefault());

        serviceListPanddings = new ArrayList<>();
        loginUser();



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
        tv_userName.setText(USER_INFO_NAME_SP);
        tv_user_Email.setText(USER_EMAIL_SP);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!isInternetAvailable()) {
            showSnankBar("No Internet Connection");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (P_USER_STATUS_SP.equals("O")) {
                    updateView();
                }
            }
        }, 0, 20000);

    }

    public void updateView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LatLng sydney4 = new LatLng(latitude, longitude);
                if (latitude != 0.0 && longitude != 0.0) {
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        str_laticude = String.valueOf(latitude);
                        str_longicude = String.valueOf(longitude);
                        str_address = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (zoomFlag == 1) {
                        marker = hashMapMarker.get("XYZ");
                        marker.remove();
                        hashMapMarker.remove("XYZ");
                    }

                    hashMapMarker = new HashMap<>();
                    marker = mMap.addMarker(new MarkerOptions().position(sydney4).title("Your location : " + str_address).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                    hashMapMarker.put("XYZ", marker);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney4));
                    //mMap.setMyLocationEnabled(true);
                    if (zoomFlag == 0) {
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                    }
                    zoomFlag = 1;

                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 1000, null);

                    SendLocation();
                } else {
                    Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }

            }
        });
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            alertDialog1(this, "", "Are Your sure exit!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menuOffline = menu.findItem(R.id.action_offline);
        menuOnline = menu.findItem(R.id.action_online);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            LL_Map.setVisibility(View.VISIBLE);
            LL_PanddingList.setVisibility(View.GONE);
        } else if (id == R.id.action_list_view) {
            LL_PanddingList.setVisibility(View.VISIBLE);
            LL_Map.setVisibility(View.GONE);

        } else if (id == R.id.action_offline) {
            P_USER_STATUS_SP = "O";
            UpdateStatus();
            menuOffline.setVisible(false);
            menuOnline.setVisible(true);
            onlineStatus = true;
            showSnankBar("You Are Online Now !");

        } else if (id == R.id.action_online) {
            P_USER_STATUS_SP = "L";
            UpdateStatus();
            menuOffline.setVisible(true);
            menuOnline.setVisible(false);
            onlineStatus = false;
            showSnankBar("You Are Offline Now !");
            //timer.cancel();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(MainActivity.this, ProfilePage.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainActivity.this, ServicePage.class));
            timer.cancel();
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, AboutPage.class));
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_send) {
            Intent goNew = new Intent(MainActivity.this, LogInPage.class);
            goNew.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goNew);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loginUser() {
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
                    lv_panddingList.setAdapter(serviceAdapterPandding);

                    lv_panddingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
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
                            timer.cancel();
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

    public void SendLocation() {

        StringRequest stringRequests = new StringRequest(Request.Method.POST, UrlPath + "OAA_UPD_USER_INFO_SP_LOC.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
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
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }
                        Toast.makeText(MainActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(MainActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (pd != null) {
                        pd.dismiss();
                    }
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
                parameters.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);
                parameters.put("P_USER_LATITUDE_SP", str_laticude);
                parameters.put("P_USER_LONGITUDE_SP", str_longicude);
                parameters.put("P_USER_ADDRESS_SP", str_address);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequests);
    }

    public void UpdateStatus() {

        StringRequest stringRequests = new StringRequest(Request.Method.POST, UrlPath + "OAA_USER_SP_STATUS_UPDATE.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
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
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }
                        //showSnankBar(STATUS_TYPE + ", " + MSG);
                        //Toast.makeText(MainActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();

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
                       // showSnankBar(STATUS_TYPE + ", " + MSG);
                        //Toast.makeText(MainActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (pd != null) {
                        pd.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Server Don't Response !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_USER_STATUS_SP", P_USER_STATUS_SP);
                parameters.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequests);
        }

    @Override
    protected void onResume() {
            super.onResume();
            CheckGPS();
            if (android.os.Build.VERSION.SDK_INT > 22) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                } else {
                    getLastLocation = new GetLocationInfo(MainActivity.this);
                    latitude = getLastLocation.getlat();
                    longitude = getLastLocation.getLon();
                }
            } else {
                getLastLocation = new GetLocationInfo(MainActivity.this);
                latitude = getLastLocation.getlat();
                longitude = getLastLocation.getLon();

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

    public void alertDialog1(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        P_USER_STATUS_SP = "L";
                        UpdateStatus();
                        Intent goNew = new Intent(MainActivity.this, LogInPage.class);
                        goNew.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(goNew);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) MainActivity.this
                .getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
