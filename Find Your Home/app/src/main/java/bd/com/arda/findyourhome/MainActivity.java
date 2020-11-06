package bd.com.arda.findyourhome;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.Settings;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.IntentCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import bd.com.arda.findyourhome.adapter.FiltterTotal;
import bd.com.arda.findyourhome.adapter.ToletDetailsFragment;
import bd.com.arda.findyourhome.adapter.ToletAdapter;
import bd.com.arda.findyourhome.map.BestLocation;
import bd.com.arda.findyourhome.model.ToletListModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback {
    ListView lv_tolet;
    String URL_GET_TOTLET_DATA = "http://103.91.54.60/apps/FYH/FYH_GET_TOLET_ALL.php";
    ProgressDialog pd;
    ArrayList<ToletListModel> listtofcustomer;
    ArrayList<String> VEHICLE_ID=new ArrayList<>();
    ArrayList<String> LATTITUDE=new ArrayList<>();
    ArrayList<String> LOGLITUTDE=new ArrayList<>();
    private GoogleMap mMap;
    LinearLayout layout_map,layout_list;
    ArrayList<LatLng> locAll=null;
    Timer timer=null;
    double lat=0.0,lon=0.0;
    int zoomFlag=0;
    Marker marker=null;
    HashMap<String,Marker> hashMapMarker=null;
    BestLocation location=null;
    SharedPreferences sharedPreferences;
    String USER_ACC_ID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences=getSharedPreferences("FYH_UserData",MODE_PRIVATE);
        USER_ACC_ID=sharedPreferences.getString("USER_ACC_ID","");

        layout_map=findViewById(R.id.layout_map);
        layout_list=findViewById(R.id.layout_list);
        lv_tolet=findViewById(R.id.lv_tolet);
        listtofcustomer = new ArrayList<>();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


/*        CheckGPS();
        location=new BestLocation(MainActivity.this);
        lat=location.getlat();
        lon=location.getLon();*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                FiltterTotal filtterTotal = new FiltterTotal();
                //toletDetailsFragment.setArguments(bundle);
                filtterTotal.show(getSupportFragmentManager(), filtterTotal.getTag());
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        GetToletType();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locAll = new ArrayList<>();
        for(int i=0;i<VEHICLE_ID.size();i++){
            LatLng latLng = new LatLng(Double.valueOf(LATTITUDE.get(i)),Double.valueOf(LOGLITUTDE.get(i)));
            mMap.addMarker(new MarkerOptions().position(latLng).title(VEHICLE_ID.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_address)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
          //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 1000, null);

            locAll.add(latLng);
            LatLng sydney4 = new LatLng(lat, lon);
            if(lat!=0.0&&lon!=0.0) {
                if(zoomFlag==1){
                    marker = hashMapMarker.get("XYZ");
                    marker.remove();
                    hashMapMarker.remove("XYZ");
                }
                try {
                    hashMapMarker = new HashMap<>();
                    marker = mMap.addMarker(new MarkerOptions().position(sydney4).title("Your location").icon(BitmapDescriptorFactory.fromResource(R.drawable.my_icon)));
                    hashMapMarker.put("XYZ",marker);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney4));
                    //mMap.setMyLocationEnabled(true);
                    if(zoomFlag==0){
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 1000, null);
                    }
                    zoomFlag=1;
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 1000, null);

        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                updateView();
            }
        }, 0, 60000);
    }

    public void updateView () {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {


            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            layout_map.setVisibility(View.VISIBLE);
            layout_list.setVisibility(View.GONE);
        }else if(id == R.id.action_list_view){
            layout_map.setVisibility(View.GONE);
            layout_list.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(this,Profilepage.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this,MyToletList.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this,LogInPage.class));
        } else if (id == R.id.nav_manage) {
            Intent intent=new Intent(MainActivity.this,PostTolet.class);
            intent.putExtra("OpenType","");
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            SharedPreferences settings = getSharedPreferences("FYH_UserData", Context.MODE_PRIVATE);
            settings.edit().clear().commit();
            Intent intent = new Intent(getApplicationContext(), LogInPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckGPS();
        location=new BestLocation(MainActivity.this);
        lat=location.getlat();
        lon=location.getLon();

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

    private void GetToletType() {
        pd = ProgressDialog.show(MainActivity.this, "Getting Tolet","Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_TOTLET_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseDataForGetToletType(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_USER_ACC_ID",USER_ACC_ID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseDataForGetToletType(String response) {

        try {

                JSONArray jArray = new JSONArray(response);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data1 = jArray.getJSONObject(i);
                    VEHICLE_ID.add(json_data1.getString("ADDRESS"));
                    LATTITUDE.add(json_data1.getString("LATTITUDE"));
                    LOGLITUTDE.add(json_data1.getString("LOGLITUTDE"));

                }

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    ToletListModel toletListModel=new ToletListModel(
                            json_data.getString("TOLET_INFO_ID"),
                            json_data.getString("USER_ACC_ID"),
                            json_data.getString("TOLET_NAME"),
                            json_data.getString("TOLET_DETAILS"),
                            json_data.getString("ADDRESS"),
                            json_data.getString("LATTITUDE"),
                            json_data.getString("LOGLITUTDE"),
                            json_data.getString("PRICE"),
                            json_data.getString("BATHS"),
                            json_data.getString("BEDS"),
                            json_data.getString("FLOORS"),
                            json_data.getString("AVAILABLE_FROM"),
                            json_data.getString("CONTACT_PERSON_NM"),
                            json_data.getString("CONTACT_PERSON_PHN"),
                            json_data.getString("CONTACT_PERSON_EML"),
                            json_data.getString("TOLET_TYPE_ID"),
                            json_data.getString("TOLET_TYPE_NAME"),
                            json_data.getString("PRODUCT_IMAGE"),
                            json_data.getString("CREATE_DATA"),
                            json_data.getString("CREATE_BY")
                    );
                    listtofcustomer.add(toletListModel);

                }
                if (pd != null) {
                    pd.dismiss();
                }
                ToletAdapter adapter = new ToletAdapter(getApplicationContext(), R.layout.tolet_list_adapter, listtofcustomer);
                lv_tolet.setAdapter(adapter);
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                lv_tolet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        bundle.putString("OpenType","");
                        bundle.putString("CONTACT_PERSON_NM", listtofcustomer.get(position).getCONTACT_PERSON_NM());
                        bundle.putString("CONTACT_PERSON_PHN", listtofcustomer.get(position).getCONTACT_PERSON_PHN());
                        bundle.putString("CONTACT_PERSON_EML", listtofcustomer.get(position).getCONTACT_PERSON_EML());
                        bundle.putString("FLOORS", listtofcustomer.get(position).getFLOORS());
                        bundle.putString("ADDRESS", listtofcustomer.get(position).getADDRESS());
                        bundle.putString("AVAILABLE_FROM", listtofcustomer.get(position).getAVAILABLE_FROM());
                        bundle.putString("BATHS", listtofcustomer.get(position).getBATHS());
                        bundle.putString("BEDS", listtofcustomer.get(position).getBEDS());
                        bundle.putString("LOGLITUTDE", listtofcustomer.get(position).getLOGLITUTDE());
                        bundle.putString("LATTITUDE", listtofcustomer.get(position).getLATTITUDE());
                        bundle.putString("PRICE", listtofcustomer.get(position).getPRICE());
                        bundle.putString("TOLET_DETAILS", listtofcustomer.get(position).getTOLET_DETAILS());
                        bundle.putString("TOLET_TYPE_ID", listtofcustomer.get(position).getTOLET_TYPE_ID());
                        bundle.putString("TOLET_NAME", listtofcustomer.get(position).getTOLET_NAME());
                        ToletDetailsFragment toletDetailsFragment = new ToletDetailsFragment();
                        toletDetailsFragment.setArguments(bundle);
                        toletDetailsFragment.show(getSupportFragmentManager(), toletDetailsFragment.getTag());
                    }
                });

        } catch (JSONException e) {
            if (pd != null) {
                pd.dismiss();
            }
            e.printStackTrace();
        }

    }

}
