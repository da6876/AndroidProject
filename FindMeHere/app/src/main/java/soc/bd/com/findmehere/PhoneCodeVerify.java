package soc.bd.com.findmehere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneCodeVerify extends AppCompatActivity {
    EditText edt_Verify_code;
    Button btn_Verify_Code;
    private FirebaseAuth mAuth;
    String strmobile="",verificationId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_code_verify);


        mAuth = FirebaseAuth.getInstance();Bundle getMobile=getIntent().getExtras();
        if(getMobile!=null){
            strmobile=getMobile.getString("mobile");
            verificationId=getMobile.getString("phoneVerificationId");
        }

        edt_Verify_code=findViewById(R.id.edt_Verify_code);
        btn_Verify_Code=findViewById(R.id.btn_Verify_Code);
        btn_Verify_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edt_Verify_code.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {

                    edt_Verify_code.setError("Enter code...");
                    edt_Verify_code.requestFocus();
                }
                verifyCode(code);
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
                            Intent intent = new Intent(PhoneCodeVerify.this, RegistrationPage.class);
                            intent.putExtra("SingUpType","Phone");
                            intent.putExtra("PhoneNo",strmobile);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(PhoneCodeVerify.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
