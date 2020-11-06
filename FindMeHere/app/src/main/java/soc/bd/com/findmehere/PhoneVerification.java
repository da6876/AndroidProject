package soc.bd.com.findmehere;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {
    EditText edt_PhoneNO;
    Button btn_Sing_up;
    public static String phoneVerificationId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verification);

        edt_PhoneNO=findViewById(R.id.edt_PhoneNO);
        btn_Sing_up=findViewById(R.id.btn_Sing_up);
        btn_Sing_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobileVarificationCode mobileVarificationCode=new MobileVarificationCode(PhoneVerification.this);
                mobileVarificationCode.execute();
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
        protected void onPostExecute(String result){
            if (pd != null) {
                pd.dismiss();
            }

        }

    }

    private void sendVarificationCode(){
        String phone = edt_PhoneNO.getText().toString();

        if(phone.isEmpty()){
            edt_PhoneNO.setError("Phone Number Is Required");
            edt_PhoneNO.requestFocus();
            return;
        }
        if (phone.length()<  11){
            edt_PhoneNO.setError("Phone Number Is not Valid");
            edt_PhoneNO.requestFocus();
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerification.this,e.toString(), Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            phoneVerificationId = s;
            if(!phoneVerificationId.equals("")){
                Intent goNew = new Intent(PhoneVerification.this,PhoneCodeVerify.class);
                goNew.putExtra("mobile",edt_PhoneNO.getText().toString().trim());
                goNew.putExtra("phoneVerificationId",phoneVerificationId);
                startActivity(goNew);
                finish();
            }
        }
    };
}
