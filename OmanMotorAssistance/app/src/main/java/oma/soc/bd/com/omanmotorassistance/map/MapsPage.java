package oma.soc.bd.com.omanmotorassistance.map;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.material.snackbar.Snackbar;

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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import oma.soc.bd.com.omanmotorassistance.HomePage;
import oma.soc.bd.com.omanmotorassistance.R;

import static oma.soc.bd.com.omanmotorassistance.LogInPage.UrlPath;

public class MapsPage extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    TextView tvComplaintNo,tvDistances,tvTimeTakeApx;
    LinearLayout LL_SHOW;
    View parentLayout;

    Polyline polyline=null;
    MaterialButton btn_show_service_details,btn_hide_service_details;
    String glat="0.0",glon="0.0",gId="";
    private Marker currentLocationMarker;
    ArrayList<LatLng> locAll=null;
    Timer timer=null;
    int zoomFlag=0;
    Marker marker=null;
    HashMap<String,Marker> hashMapMarker=null;
    String time1="",time2="";
    String USER_INFO_ID_SP="",USER_INFO_NAME_SP="",USER_ADDRESS_SP="",STATUS_TYPE="",MSG="",v_LATTITUDE="0.0",
            v_LOGLITUTDE="0.0",SERVICE_ID="",TOKEN_NO="",LOCATION_CODE="",LOCATION_NAME="",SERVICE_TYPE_ID="",
            SERVICE_CHARGE="",SERVICE_TYPE_NAME="",USER_INFO_ID="",MOBILE_NUM="",SERVICE_STATUS="",REMARKS="",
            USER_ADDRESS="",USER_LATITUDE="",USER_LONGITUDE="",CREATE_DATA="",CREATE_BY="",UPDATE_DATA="",
            SEV_PRO_NAME="",SEV_DELAIL="",
            URLline = UrlPath+"OAA_GET_SERVICE_LIST_FOR_SP.php",
            URLgetlocation_SP = UrlPath+"OAA_GET_USER_INFO_SP_LOC.php";

    ImageButton iv_phone,ic_sms;
    TextView tv_mobile,tv_service_pro_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map_header);

        LL_SHOW=findViewById(R.id.LL_SHOW);
        btn_show_service_details=findViewById(R.id.btn_show_service_details);
        btn_show_service_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_hide_service_details.setVisibility(View.VISIBLE);
                btn_show_service_details.setVisibility(View.GONE);
                LL_SHOW.setVisibility(View.VISIBLE);
            }
        });
        parentLayout = findViewById(android.R.id.content);

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
        tv_service_pro_name=findViewById(R.id.tv_service_pro_name);
        iv_phone=findViewById(R.id.iv_phone);
        ic_sms=findViewById(R.id.ic_sms);

        tvComplaintNo = (TextView) findViewById(R.id.tvComplaintNo);
        tvDistances = (TextView) findViewById(R.id.tvDistance);
        tvTimeTakeApx = (TextView) findViewById(R.id.tvTimeTakeApx);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            USER_INFO_ID_SP = bundle.getString("USER_INFO_ID_SP");
            USER_INFO_NAME_SP = bundle.getString("USER_INFO_NAME_SP");
        }


        tv_service_pro_name.setText(USER_INFO_NAME_SP);
        iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + MOBILE_NUM));
                startActivity(callIntent);
            }
        });
        ic_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", MOBILE_NUM, null)));
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GetServiceInfo();
    }

    private void GetServiceInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String responcse = obj.getString("responce");
                            JSONObject obj2 = new JSONObject(responcse);
                            String status_code = obj2.getString("status_code");
                            String msg = obj2.getString("msg");
                            String values = obj2.getString("values");
                            if (status_code.equals("500")) {
                                JSONArray jsonArray = new JSONArray(values.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    SERVICE_ID=jsonObject2.getString("SERVICE_ID");
                                    TOKEN_NO=jsonObject2.getString("TOKEN_NO");
                                    LOCATION_CODE=jsonObject2.getString("LOCATION_CODE");
                                    LOCATION_NAME=jsonObject2.getString("LOCATION_NAME");
                                    SERVICE_TYPE_ID=jsonObject2.getString("SERVICE_TYPE_ID");
                                    SERVICE_CHARGE=jsonObject2.getString("SERVICE_CHARGE");
                                    SERVICE_TYPE_NAME=jsonObject2.getString("SERVICE_TYPE_NAME");
                                    USER_INFO_ID=jsonObject2.getString("USER_INFO_ID");
                                    MOBILE_NUM=jsonObject2.getString("MOBILE_NUM");
                                    SERVICE_STATUS=jsonObject2.getString("SERVICE_STATUS");
                                    REMARKS=jsonObject2.getString("REMARKS");
                                    USER_ADDRESS=jsonObject2.getString("USER_ADDRESS");
                                    USER_LATITUDE=jsonObject2.getString("USER_LATITUDE");
                                    USER_LONGITUDE=jsonObject2.getString("USER_LONGITUDE");
                                    CREATE_DATA=jsonObject2.getString("CREATE_DATA");
                                    CREATE_BY=jsonObject2.getString("CREATE_BY");
                                    UPDATE_DATA=jsonObject2.getString("UPDATE_DATA");
                                    SEV_PRO_NAME=jsonObject2.getString("SEV_PRO_NAME");
                                    SEV_DELAIL=jsonObject2.getString("SEV_DELAIL");
                                    USER_INFO_NAME_SP=jsonObject2.getString("USER_INFO_NAME_SP");
                                    USER_ADDRESS_SP=jsonObject2.getString("USER_ADDRESS_SP");
                                }

                                tv_mobile.setText(MOBILE_NUM);


                                timer = new Timer();
                                timer.schedule(new TimerTask()
                                {
                                    @Override
                                    public void run()
                                    {
                                        GetLocation_SP();
                                        updateView();

                                    }
                                }, 0, 10*1000);
                            } else {
                                JSONArray jsonArray = new JSONArray(values.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    STATUS_TYPE=jsonObject2.getString("STATUS_TYPE");
                                    MSG=jsonObject2.getString("MSG");
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MapsPage.this, e.toString(), Toast.LENGTH_LONG);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MapsPage.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(MapsPage.this);
        requestQueue.add(stringRequest);
    }

    private void GetLocation_SP() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLgetlocation_SP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String responcse = obj.getString("responce");
                            JSONObject obj2 = new JSONObject(responcse);
                            String status_code = obj2.getString("status_code");
                            String msg = obj2.getString("msg");
                            String values = obj2.getString("values");
                            if (status_code.equals("500")) {
                                JSONArray jsonArray = new JSONArray(values.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    glat=jsonObject2.getString("USER_LATITUDE_SP");
                                    glon=jsonObject2.getString("USER_LONGITUDE_SP");
                                    //LOCATION_CODE=jsonObject2.getString("USER_ADDRESS_SP");
                                }

                            } else {
                                JSONArray jsonArray = new JSONArray(values.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    STATUS_TYPE=jsonObject2.getString("STATUS_TYPE");
                                    MSG=jsonObject2.getString("MSG");
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MapsPage.this, e.toString(), Toast.LENGTH_LONG);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MapsPage.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_USER_INFO_ID_SP", USER_INFO_ID_SP);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(MapsPage.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void updateView () {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(glat.equals("0.0")&&glon.equals("0.0")){
                    glat=v_LATTITUDE;
                    glon=v_LOGLITUTDE;
                    showSnankBar("Service Provider Location Not Found !!");

                }
                /*if (strSERVICE_STATUS.equals("P")){
                    strSts="Dear Sir/Madam, Your Complaint No : " + strTOKEN_NO +". has been submitted successfully.\" +\n" +
                            "                            \" Your Problem will be resolved very soon.";
                    glat="23.728254";
                    glon="90.411120";
                    gId="DPDC Car";
                }else if(strSERVICE_STATUS.equals("F")){
                    strSts="F";
                }else if(strSERVICE_STATUS.equals("R")){
                    strSts="R";
                }else if(strSERVICE_STATUS.equals("W")){
                    strSts="W";
                }
                tvDistance.setText("");*/


                if (null != currentLocationMarker) {
                    currentLocationMarker.remove();
                }
                LatLng gangLoc = new LatLng(Double.valueOf(glat), Double.valueOf(glon));
                currentLocationMarker=mMap.addMarker(new MarkerOptions().position(gangLoc).title(SERVICE_TYPE_NAME).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(gangLoc));


                LatLng custLoc = new LatLng(Double.valueOf(USER_LATITUDE), Double.valueOf(USER_LONGITUDE));
                if(Double.valueOf(USER_LATITUDE)!=0.0&&Double.valueOf(USER_LONGITUDE)!=0.0) {
                    if(zoomFlag==1){
                        marker = hashMapMarker.get("XYZ");
                        marker.remove();
                        hashMapMarker.remove("XYZ");
                    }
                    hashMapMarker = new HashMap<>();
                    marker = mMap.addMarker(new MarkerOptions().position(custLoc).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.my_loc)));
                    hashMapMarker.put("XYZ",marker);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(custLoc));
                    //mMap.setMyLocationEnabled(true);
                    if(zoomFlag==0){
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                    }

                    //for (int a = 0; a < locAll.size(); a++) {
                    String url = getDirectionsUrl(custLoc, gangLoc);

                    MapsPage.DownloadTask downloadTask = new MapsPage.DownloadTask();
                    downloadTask.execute(url);
                    //}
                    LatLng latLngA = new LatLng(Double.valueOf(glat), Double.valueOf(glon));
                    LatLng latLngB = new LatLng(Double.valueOf(USER_LATITUDE), Double.valueOf(USER_LONGITUDE));

                    Location locationA = new Location("point A");
                    locationA.setLatitude(latLngA.latitude);
                    locationA.setLongitude(latLngA.longitude);
                    Location locationB = new Location("point B");
                    locationB.setLatitude(latLngB.latitude);
                    locationB.setLongitude(latLngB.longitude);

                    double distance = locationA.distanceTo(locationB);
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

                        time1=strTime.substring(0,2);
                        time2=strTime.substring(3,5);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(!glat.equals("0.0")&&!glon.equals("0.0")){
                        /*tvDistance.setText("Dear Sir/Madam, A gang has been assigned against Your Complaint No : "+strTOKEN_NO+". Contact Person :  "+strUSER_SHORT_NAME+" ,  "+strMOBILE+" .\n" +
                                "and Distance" + String.valueOf(distance)+" km. It will take approximately " + strTime + " hours.");*/
                        tvComplaintNo.setText(SERVICE_TYPE_NAME);
                        tvDistances.setText(String.valueOf(distance)+" Km");
                        tvTimeTakeApx.setText(time1+"h : "+time2+"m");
                    }
                    zoomFlag=1;
                }
            }
        });
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

    /** A method to download json data from url */
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

            MapsPage.ParserTask parserTask = new MapsPage.ParserTask();

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
        timer.cancel();
        startActivity(new Intent(MapsPage.this, HomePage.class));
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckGPS();
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

}
