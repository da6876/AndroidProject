package serviceprovideroma.soc.bd.com.serviceprovideroma.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import serviceprovideroma.soc.bd.com.serviceprovideroma.MainActivity;
import serviceprovideroma.soc.bd.com.serviceprovideroma.R;
import serviceprovideroma.soc.bd.com.serviceprovideroma.ServicePage;

import static serviceprovideroma.soc.bd.com.serviceprovideroma.LogInPage.UrlPath;

public class CheckAssementService extends Service {

    public static final long NOTIFY_INTERVAL = 10*1000; // 10 seconds
    String STATUS_TYPE = "", MSG = "", USER_INFO_ID_SP = "";
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    SharedPreferences sharedPreferences;
    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences("SP_DATA", Context.MODE_PRIVATE);
        USER_INFO_ID_SP = sharedPreferences.getString("USER_INFO_ID_SP", "0");
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    GetAssiment();
                }

            });
        }

    }

    public void GetAssiment() {

        StringRequest stringRequests = new StringRequest(Request.Method.POST, UrlPath + "OAA_CHECK_SER_FR_US_SP.php", new Response.Listener<String>() {
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

                        addNotification();
                        //Toast.makeText(MainActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();
                    } else {

                        JSONArray jsonArray = new JSONArray(values.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            STATUS_TYPE = jsonObject2.getString("STATUS_TYPE");
                            MSG = jsonObject2.getString("MSG");
                        }
                        //Toast.makeText(MainActivity.this, STATUS_TYPE + "," + MSG, Toast.LENGTH_SHORT).show();
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
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequests);
    }

/*    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification() {
        // Sets an ID for the notification, so it can be updated.
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle(STATUS_TYPE)
                .setContentText(MSG)
                .setSmallIcon(R.drawable.logo_oma)
                .setChannelId(CHANNEL_ID)
                .build();


    }*/

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_logos)
                        .setContentTitle(STATUS_TYPE)
                        .setContentText(MSG)
                        .setSound(soundUri);

        Intent notificationIntent = new Intent(this, ServicePage.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}