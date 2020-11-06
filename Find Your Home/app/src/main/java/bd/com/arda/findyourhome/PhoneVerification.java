package bd.com.arda.findyourhome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {
    FloatingActionButton fab_phone_verify;
    TextInputEditText tv_phone_number;
    public static String phoneVerificationId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verification);

        tv_phone_number = findViewById(R.id.tv_phone_number);
        tv_phone_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int lenth = tv_phone_number.getText().toString().length();
                if (!tv_phone_number.getText().toString().equals("")) {
                    if (lenth == 11) {
                        MobileVarificationCode mobileVarificationCode = new MobileVarificationCode(PhoneVerification.this);
                        mobileVarificationCode.execute();
                    } else {
                        Toast.makeText(PhoneVerification.this,"Phone Number Must be 11 Digit !!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PhoneVerification.this,"Enter The Phone Number !!",Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        fab_phone_verify = findViewById(R.id.fab_phone_verify);
        fab_phone_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lenth = tv_phone_number.getText().toString().length();
                if (!tv_phone_number.getText().toString().equals("")) {
                    if (lenth == 11) {
                        MobileVarificationCode mobileVarificationCode = new MobileVarificationCode(PhoneVerification.this);
                        mobileVarificationCode.execute();
                    } else {
                        Toast.makeText(PhoneVerification.this,"Phone Number Must be 11 Digit !!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PhoneVerification.this,"Enter The Phone Number !!",Toast.LENGTH_LONG).show();
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
            pd = ProgressDialog.show(PhoneVerification.this, "Sending", "Please wait few Second...");
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
        String phone = tv_phone_number.getText().toString();

        if (phone.isEmpty()) {
            tv_phone_number.setError("Phone Number Is Required");
            tv_phone_number.requestFocus();
            return;
        }
        if (phone.length() < 11) {
            tv_phone_number.setError("Phone Number Is not Valid");
            tv_phone_number.requestFocus();
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88"+phone,        // Phone number to verify
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
            Toast.makeText(PhoneVerification.this, e.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            phoneVerificationId = s;
            if (!phoneVerificationId.equals("")) {
                Intent goNew = new Intent(PhoneVerification.this, PhoneVerifyCode.class);
                goNew.putExtra("mobile", tv_phone_number.getText().toString().trim());
                goNew.putExtra("phoneVerificationId", phoneVerificationId);
                startActivity(goNew);
                finish();
            }
        }
    };
}
