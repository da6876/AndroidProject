package oma.soc.bd.com.omanmotorassistance;

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
    SharedPreferences sharedPreferences;
    String Reg_status = "", str_Login_status = "";
    TextView tv_already_acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_option);

        sharedPreferences = getSharedPreferences("OAA_DATA", Context.MODE_PRIVATE);
        Reg_status = sharedPreferences.getString("Reg_status", "0");
        str_Login_status = sharedPreferences.getString("LOGIN_STATUS", "0");

        tv_already_acc = findViewById(R.id.tv_already_acc);
        tv_already_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInOption.this, LogInPage.class));
            }
        });
        btn_google_sing_in = findViewById(R.id.btn_google_sing_in);
        btn_google_sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()) {
                    alertDialog(LogInOption.this, "Message", "Check Your Internet Connection !!");
                } else {
                    signIn();
                }
            }
        });
        btn_phone_sing_in = findViewById(R.id.btn_phone_sing_in);
        btn_phone_sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInOption.this, PhoneNumberVerify.class));
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

            String fullname = account.getDisplayName();
            String fastName = account.getGivenName();
            String lastName = account.getFamilyName();
            String emailName = account.getEmail();
            Intent intent = new Intent(getApplicationContext(), RegistrationPage.class);
            intent.putExtra("SingUpType", "Google");
            intent.putExtra("fullname", fullname);
            intent.putExtra("fastName", fastName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("emailName", emailName);
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Damm Error", "signInResult:failed code=" + e.getStatusCode());
            alertDialog1(LogInOption.this, "Failed", "Sorry Could Not Verify Google Account");
            //updateUI(null);
        }
    }

    public void alertDialog1(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
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
