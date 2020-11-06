package soc.bd.com.testsendmsg;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv_sms;
    ArrayList<SmsModel> smsModelArrayList;
    SmsAdapter smsAdapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //Method to read sms and load into listview
        readSms();
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading sms");

        smsModelArrayList = new ArrayList<>();
        lv_sms = (ListView) findViewById(R.id.lv_sms);
        smsAdapter = new SmsAdapter(MainActivity.this,smsModelArrayList);
        lv_sms.setAdapter(smsAdapter);
    }

    public void readSms(){

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContentResolver().query(uri, null, null ,null,null);
        startManagingCursor(c);
        // Read the sms data
        if(c.moveToFirst()) {
            for(int i = 0; i < c.getCount(); i++) {

                String mobile = c.getString(c.getColumnIndexOrThrow("address")).toString();
                String message = c.getString(c.getColumnIndexOrThrow("body")).toString();

                //adding item to array list
                smsModelArrayList.add(new SmsModel(mobile, message));
                c.moveToNext();
            }
        }
        c.close();
        progressDialog.dismiss();
        // notifying listview adapter
        smsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item3:
                startActivity(new Intent(MainActivity.this,SendSms.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
