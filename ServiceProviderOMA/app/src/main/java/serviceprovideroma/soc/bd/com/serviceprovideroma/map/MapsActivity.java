package serviceprovideroma.soc.bd.com.serviceprovideroma.map;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import serviceprovideroma.soc.bd.com.serviceprovideroma.MainActivity;
import serviceprovideroma.soc.bd.com.serviceprovideroma.R;

import static serviceprovideroma.soc.bd.com.serviceprovideroma.LogInPage.UrlPath;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    SharedPreferences sharedPreferences;
    private GoogleMap mMap;
    GetLocationInfo getLastLocation=null;
    Timer timer=null;
    Timer timerVacleTracking=null;
    TextView tvComplaintNo,tvDistances,tvTimeTakeApx;
    ImageButton iv_phone,ic_sms;
    TextView tv_mobile,tv_service_pro_name;

    int zoomFlag=0;
    Marker marker=null;
    HashMap<String,Marker> hashMapMarker=null;
    LatLng mymen=null;
    Polyline polyline=null;
    private Marker currentLocationMarker;
    SharedPreferences sp;
    String str_address = "",STATUS_TYPE = "", MSG = "", USER_INFO_ID_SP = "",USER_INFO_NAME_SP = "", SERVICE_ID = "",SERVICE_TYPE_NAME = "",
            TOKEN_NO = "", LOCATION_NAME = "", SERVICE_CHARGE = "", MOBILE_NUM = "", USER_LATITUDE = "", USER_LONGITUDE = "",
            CREATE_DATA = "", USER_INFO_NAME = "", USER_PHONE = "", USER_EMAIL = "", USER_ADDRESS = "", REMARKS = "", SERVICE_STATUS = "W";
    int intTIMER =0;
    double latitude = 0.0, longitude = 0.0;
    MaterialButton btn_show_service_details,btn_hide_service_details,btn_finish;
    int flagWorking=0;
    LinearLayout LL_SHOW;
    Geocoder geocoder;
    List<Address> addresses = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_service);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        sharedPreferences = getSharedPreferences("SP_DATA", Context.MODE_PRIVATE);
        USER_INFO_NAME_SP = sharedPreferences.getString("USER_INFO_NAME_SP", "0");
        LL_SHOW=findViewById(R.id.LL_SHOW);
        btn_finish=findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateServiceStatusFinish();
            }
        });
        btn_show_service_details=findViewById(R.id.btn_show_service_details);
        btn_show_service_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_hide_service_details.setVisibility(View.VISIBLE);
                btn_show_service_details.setVisibility(View.GONE);
                LL_SHOW.setVisibility(View.VISIBLE);
            }
        });
        btn_hide_service_details=findViewById(R.id.btn_hide_service_details);
        btn_hide_service_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_show_service_details.setVisibility(View.VISIBLE);
                btn_hide_service_details.setVisibility(View.GONE);
                LL_SHOW.setVisibility(View.GONE);
            }
        });
        tv_mobile=findViewById(R.id.tv_mobile);
        tv_service_pro_name=findViewById(R.id.tv_customer_name);
        iv_phone=findViewById(R.id.iv_phone);
        ic_sms=findViewById(R.id.ic_sms);
        ServiceStatusStart();
        tvComplaintNo = (TextView) findViewById(R.id.tvComplaintNo);
        tvDistances = (TextView) findViewById(R.id.tvDistance);
        tvTimeTakeApx = (TextView) findViewById(R.id.tvTimeTakeApx);
        Bundle bl=getIntent().getExtras();
        if(bl!=null){
            SERVICE_ID=bl.getString("SERVICE_ID");
            SERVICE_TYPE_NAME=bl.getString("SERVICE_TYPE_NAME");
            TOKEN_NO=bl.getString("TOKEN_NO");
            LOCATION_NAME=bl.getString("LOCATION_NAME");
            SERVICE_CHARGE=bl.getString("SERVICE_CHARGE");
            MOBILE_NUM=bl.getString("MOBILE_NUM");
            USER_ADDRESS=bl.getString("USER_ADDRESS");
            REMARKS=bl.getString("REMARKS");
            USER_LATITUDE=bl.getString("USER_LATITUDE");
            USER_LONGITUDE=bl.getString("USER_LONGITUDE");
            CREATE_DATA=bl.getString("CREATE_DATA");
            USER_INFO_NAME=bl.getString("USER_INFO_NAME");
            USER_PHONE=bl.getString("USER_PHONE");
            USER_EMAIL=bl.getString("USER_EMAIL");
        }
        tv_service_pro_name.setText(USER_INFO_NAME);
        tv_mobile.setText(USER_PHONE);
        iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + USER_PHONE));
                startActivity(callIntent);
            }
        });
        ic_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", USER_PHONE, null)));
            }
        });
        sharedPreferences = getSharedPreferences("SP_DATA", Context.MODE_PRIVATE);
        USER_INFO_ID_SP = sharedPreferences.getString("USER_INFO_ID_SP", "0");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mymen = new LatLng(Double.valueOf(USER_LATITUDE),Double.valueOf(USER_LONGITUDE));

        mMap.addMarker(new MarkerOptions().position(mymen).title(USER_INFO_NAME+"\nAddress( "+USER_ADDRESS+" )").icon(BitmapDescriptorFactory.fromResource(R.drawable.my_loc)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mymen));


        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                updateView();
                if(flagWorking==1){
                    SERVICE_STATUS="W";
                    UpdateStatus();
                }
            }
        }, 0, 8000);
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

                        JSONArray jsonArray = new JSONArray(values.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }
                        //showSnankBar(STATUS_TYPE + ", " + MSG);
                        Toast.makeText(MapsActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();
                        UpdateServiceStatus();
                    } else {

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

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Server Don't Response !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_USER_STATUS_SP", "W");
                parameters.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(stringRequests);
    }

    public void ServiceStatusStart() {

        StringRequest stringRequests = new StringRequest(Request.Method.POST, UrlPath + "OAA_SERVICE_STATUS_UPDATE.php", new Response.Listener<String>() {
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

                        JSONArray jsonArray = new JSONArray(values.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }

                        Toast.makeText(MapsActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();

                    } else {

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

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Server Don't Response !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_SERVICE_ID", SERVICE_ID);
                parameters.put("P_SERVICE_STATUS", "S");
                parameters.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(stringRequests);
    }

    public void UpdateServiceStatus() {

        StringRequest stringRequests = new StringRequest(Request.Method.POST, UrlPath + "OAA_SERVICE_STATUS_UPDATE.php", new Response.Listener<String>() {
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

                        JSONArray jsonArray = new JSONArray(values.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }

                        Toast.makeText(MapsActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();

                    } else {

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

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Server Don't Response !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_SERVICE_ID", SERVICE_ID);
                parameters.put("P_SERVICE_STATUS", SERVICE_STATUS);
                parameters.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(stringRequests);
    }

    public void UpdateServiceStatusFinish() {
        timer.cancel();
        StringRequest stringRequests = new StringRequest(Request.Method.POST, UrlPath + "OAA_SERVICE_STATUS_UPDATE.php", new Response.Listener<String>() {
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

                        JSONArray jsonArray = new JSONArray(values.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }

                        alertDialog(MapsActivity.this,STATUS_TYPE,"Well Done, You Complected Service Successfully. ");


                    } else {

                        JSONArray jsonArray = new JSONArray(values.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }
                        // showSnankBar(STATUS_TYPE + ", " + MSG);
                        Toast.makeText(MapsActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Server Don't Response !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("P_SERVICE_ID", SERVICE_ID);
                parameters.put("P_SERVICE_STATUS", "F");
                parameters.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(stringRequests);
    }

    public void updateView () {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                latitude=getLastLocation.getlat();
                longitude=getLastLocation.getLon();
                LatLng sydney4 = new LatLng(latitude, longitude);
                if(latitude!=0.0&&longitude!=0.0) {
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        str_address = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SendLocation();
                    if(zoomFlag==1){
                        marker = hashMapMarker.get("XYZ");
                        marker.remove();
                        hashMapMarker.remove("XYZ");
                    }
                    hashMapMarker = new HashMap<>();
                    if (null != currentLocationMarker) {
                        currentLocationMarker.remove();
                    }
                    currentLocationMarker=marker = mMap.addMarker(new MarkerOptions().position(sydney4).title(USER_INFO_NAME_SP+"("+str_address+")").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                    hashMapMarker.put("XYZ",marker);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney4));
                    //mMap.setMyLocationEnabled(true);
                    if(zoomFlag==0){
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 5000, null);
                    }

                    //for (int a = 0; a < locAll.size(); a++) {
                    String url = getDirectionsUrl(sydney4, mymen);

                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                    //}
                    LatLng latLngB = new LatLng(latitude, longitude);

                    Location locationA = new Location("point A");
                    locationA.setLatitude(mymen.latitude);
                    locationA.setLongitude(mymen.longitude);
                    Location locationB = new Location("point B");
                    locationB.setLatitude(latLngB.latitude);
                    locationB.setLongitude(latLngB.longitude);

                    double distance = locationA.distanceTo(locationB);
                    if(distance<300){
                        flagWorking=1;
                    }
                    distance = distance / 1000;
                    double time = distance * 10;
                    DecimalFormat df = new DecimalFormat("#.##");
                    distance = Double.valueOf(df.format(distance));
                    SimpleDateFormat sdf = new SimpleDateFormat("mm");

                    String strTime = null;
                    try {
                        Date dt = sdf.parse(String.valueOf(time));
                        sdf = new SimpleDateFormat("HH:mm");
                        strTime = sdf.format(dt);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //time=Math.ceil(time);
                    tvComplaintNo.setText(SERVICE_TYPE_NAME);
                    tvDistances.setText(String.valueOf(distance)+" Km");
                    tvTimeTakeApx.setText(strTime + " hours.");
                   /* tvDistance.setText("DPDC Service car no. 18 is on the way. Distance " + String.valueOf(distance)
                            + " km. It will take approximately " + strTime + " hours.");*/
                    zoomFlag=1;

                }else {
                    Toast.makeText(MapsActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

                        JSONArray jsonArray = new JSONArray(values.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }
                        Toast.makeText(MapsActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();

                    } else {

                        JSONArray jsonArray = new JSONArray(values.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }
                        Toast.makeText(MapsActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

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
                parameters.put("P_USER_LATITUDE_SP", String.valueOf(latitude));
                parameters.put("P_USER_LONGITUDE_SP", String.valueOf(longitude));
                parameters.put("P_USER_ADDRESS_SP", str_address);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(stringRequests);
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){


        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            try{
                //tvDistance.setText("DPDC Service car no. 18 is on the way. Distance "+distance +" km. It will take approximately "+duration+" minutes.");
                if (null != polyline) {
                    polyline.remove();
                }
                polyline=mMap.addPolyline(lineOptions);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MapsActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckGPS();
        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                getLastLocation = new GetLocationInfo(MapsActivity.this);
                latitude = getLastLocation.getlat();
                longitude = getLastLocation.getLon();
            }
        } else {
            getLastLocation = new GetLocationInfo(MapsActivity.this);
            latitude = getLastLocation.getlat();
            longitude = getLastLocation.getLon();

        }
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
                        startActivity(new Intent(MapsActivity.this,MainActivity.class));
                        finish();
                        timer.cancel();
                        dialog.dismiss();
                    }
                }).show();
    }

}
