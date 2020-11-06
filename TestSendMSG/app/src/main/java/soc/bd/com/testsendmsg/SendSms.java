package soc.bd.com.testsendmsg;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class SendSms extends AppCompatActivity {
    EditText edt_number,edt_msg;
    ImageButton btn_send_sms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_sms);
        edt_msg=findViewById(R.id.edt_msg);
        edt_number=findViewById(R.id.edt_number);
        btn_send_sms=findViewById(R.id.btn_send_sms);
        btn_send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number=edt_number.getText().toString();
                String msg=edt_msg.getText().toString();
                int msgLenth=msg.length();
                /*if (msgLenth<=60) {
                    SimUtil.sendSMS(SendSms.this, 0, number, null, msg, null, null);
                }else {
                    SimUtil.sendMultipartTextSMS(SendSms.this, 0, number, null, msg, null, null);
                }*/
                ArrayList<String> messageList = SmsManager.getDefault().divideMessage(msg);
                if (messageList.size() > 1) {
                    SimUtil.sendMultipartTextSMS(SendSms.this, 0, number, null, messageList, null, null);
                } else {
                    SimUtil.sendSMS(SendSms.this, 0, number, null, msg, null, null);
                }
            }
        });

    }

    private void sendSms() {
        String phoneNo = edt_number.getText().toString();
        String message = edt_msg.getText().toString();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            try
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                edt_number.setText("");
                edt_msg.setText("");
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"SMS failed, please try again.",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
            }
        }

    }


}
