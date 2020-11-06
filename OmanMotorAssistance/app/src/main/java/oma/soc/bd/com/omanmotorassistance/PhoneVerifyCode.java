package oma.soc.bd.com.omanmotorassistance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneVerifyCode extends AppCompatActivity {
    TextView tv_number_set;
    TextInputEditText tv_verify_code;
    FloatingActionButton fab_code_verify;
    String strmobile="";
    private FirebaseAuth mAuth;
    private String verificationId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verify_code);
        tv_verify_code=findViewById(R.id.tv_verify_code);
        tv_verify_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (!isInternetAvailable()) {
                    alertDialog(PhoneVerifyCode.this, "Message", "Check Your Internet Connection !!");
                } else {
                    String code = tv_verify_code.getText().toString().trim();
                    if (code.isEmpty() || code.length() < 6) {

                        tv_verify_code.setError("Enter code...");
                        tv_verify_code.requestFocus();
                    }
                    verifyCode(code);
                }
                return false;
            }
        });
        fab_code_verify=findViewById(R.id.fab_code_verify);
        mAuth = FirebaseAuth.getInstance();

        Bundle getMobile=getIntent().getExtras();
        if(getMobile!=null){
            strmobile=getMobile.getString("mobile");
            verificationId=getMobile.getString("phoneVerificationId");
        }
        fab_code_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()) {
                    alertDialog(PhoneVerifyCode.this, "Message", "Check Your Internet Connection !!");
                } else {
                    String code = tv_verify_code.getText().toString().trim();
                    if (code.isEmpty() || code.length() < 6) {

                        tv_verify_code.setError("Enter code...");
                        tv_verify_code.requestFocus();
                    }
                    verifyCode(code);
                }
            }
        });


    }
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(PhoneVerifyCode.this, RegistrationPage.class);
                            intent.putExtra("SingUpType","Phone");
                            intent.putExtra("PhoneNo",strmobile);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(PhoneVerifyCode.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void  alertDialog(android.content.Context context, String title, String message) {new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            }).show();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) PhoneVerifyCode.this
                .getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
}