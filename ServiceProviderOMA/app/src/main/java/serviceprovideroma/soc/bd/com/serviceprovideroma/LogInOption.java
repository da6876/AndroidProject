package serviceprovideroma.soc.bd.com.serviceprovideroma;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class LogInOption extends AppCompatActivity {
    SignInButton btn_google_sing_in;
    MaterialButton btn_phone_sing_in;
    private static final int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    TextView tv_alreadyAccount;
    boolean alreadyAccount=false;
    SharedPreferences sharedPreferences;
    String Reg_status="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_option);

        sharedPreferences=getSharedPreferences("SP_DATA", Context.MODE_PRIVATE);

        btn_google_sing_in=findViewById(R.id.btn_google_sing_in);
        tv_alreadyAccount=findViewById(R.id.tv_alreadyAccount);
        tv_alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetAvailable()) {
                    alertDialog(LogInOption.this, "Message", "No Internet Connection");
                } else {
                    startActivity(new Intent(LogInOption.this, LogInOption.class));
                    alreadyAccount = true;
                    finish();
                }
            }
        });
        btn_google_sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInternetAvailable()){
                    alertDialog(LogInOption.this,"Message","No Internet Connection");
                }else {
                    signIn();
                }
            }
        });
        btn_phone_sing_in=findViewById(R.id.btn_phone_sing_in);
        btn_phone_sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInOption.this,PhoneNumberVerify.class));
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String fullname=account.getDisplayName();
            String fastName=account.getGivenName();
            String lastName=account.getFamilyName();
            String emailName=account.getEmail();
            Intent intent=new Intent(getApplicationContext(),RegistrationPage.class);
            intent.putExtra("SingUpType","Google");
            intent.putExtra("fullname",fullname);
            intent.putExtra("fastName",fastName);
            intent.putExtra("lastName",lastName);
            intent.putExtra("emailName",emailName);
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Damm Error", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
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

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) LogInOption.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
