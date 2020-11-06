package oma.soc.bd.com.omanmotorassistance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberVerify extends AppCompatActivity {
    FloatingActionButton fab_verify_Phone;
    public static String phoneVerificationId = "";
    TextInputEditText edt_phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_verify);

        edt_phone_number = findViewById(R.id.edt_phone_number);
        edt_phone_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (!isInternetAvailable()) {
                    alertDialog(PhoneNumberVerify.this, "Message", "Check Your Internet Connection !!");
                } else {
                    if (edt_phone_number.getText().toString().length() <= 12) {
                        MobileVarificationCode mobileVarificationCode = new MobileVarificationCode(PhoneNumberVerify.this);
                        mobileVarificationCode.execute();
                    } else {

                    }
                }
                return false;
            }
        });
        fab_verify_Phone = findViewById(R.id.fab_verify_Phone);
        fab_verify_Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()) {
                    alertDialog(PhoneNumberVerify.this, "Message", "Check Your Internet Connection !!");
                } else {
                    if (edt_phone_number.getText().toString().length() <= 12) {
                        MobileVarificationCode mobileVarificationCode = new MobileVarificationCode(PhoneNumberVerify.this);
                        mobileVarificationCode.execute();
                    } else {

                    }
                }
            }
        });
    }

    public class MobileVarificationCode extends AsyncTask<Void, Void, String> {
        private Activity context;
        ProgressDialog pd = null;

        public MobileVarificationCode(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(PhoneNumberVerify.this, "Sending", "Please wait few Second...");
        }

        @Override
        protected String doInBackground(Void... params) {
            sendVarificationCode();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (pd != null) {
                pd.dismiss();
            }

        }

    }

    private void sendVarificationCode() {
        String phone = edt_phone_number.getText().toString();

        if (phone.isEmpty()) {
            edt_phone_number.setError("Phone Number Is Required");
            edt_phone_number.requestFocus();
            return;
        }
        if (phone.length() < 11) {
            edt_phone_number.setError("Phone Number Is not Valid");
            edt_phone_number.requestFocus();
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneNumberVerify.this, e.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            phoneVerificationId = s;
            if (!phoneVerificationId.equals("")) {
                Intent goNew = new Intent(PhoneNumberVerify.this, PhoneVerifyCode.class);
                goNew.putExtra("mobile", edt_phone_number.getText().toString().trim());
                goNew.putExtra("phoneVerificationId", phoneVerificationId);
                startActivity(goNew);
                finish();
            }
        }
    };


    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) PhoneNumberVerify.this
                .getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
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

}
