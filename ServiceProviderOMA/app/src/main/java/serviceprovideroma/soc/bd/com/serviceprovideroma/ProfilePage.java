package serviceprovideroma.soc.bd.com.serviceprovideroma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import serviceprovideroma.soc.bd.com.serviceprovideroma.adapter.ProfileUpdate;

public class ProfilePage extends AppCompatActivity {
    TextView tv_user_name,tv_email,tv_sur_name,tv_phone,tv_address,tv_password;
    String USER_INFO_ID_SP="",USER_FAST_NAME_SP="",USER_LAST_NAME_SP="",USER_PASSWORD="",USER_PHONE_SP="",
            USER_EMAIL_SP="",USER_ADDRESS_SP="",SEV_PRO_NAME="",SEV_DELAIL="";
    SharedPreferences sharedPreferences;
    FloatingActionButton fab_update_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        sharedPreferences = getSharedPreferences("SP_DATA", Context.MODE_PRIVATE);
        USER_INFO_ID_SP=sharedPreferences.getString("USER_INFO_ID_SP","");
        USER_FAST_NAME_SP=sharedPreferences.getString("USER_FAST_NAME_SP","");
        USER_LAST_NAME_SP=sharedPreferences.getString("USER_LAST_NAME_SP","");
        USER_PASSWORD=sharedPreferences.getString("USER_PASSWORD","");
        USER_PHONE_SP=sharedPreferences.getString("USER_PHONE_SP","");
        USER_EMAIL_SP=sharedPreferences.getString("USER_EMAIL_SP","");
        USER_ADDRESS_SP=sharedPreferences.getString("USER_ADDRESS_SP","");
        SEV_PRO_NAME=sharedPreferences.getString("SEV_PRO_NAME","");
        SEV_DELAIL=sharedPreferences.getString("SEV_DELAIL","");
        //str_password=sharedPreferences.getString("LOGIN_STATUS","0");

        fab_update_profile=findViewById(R.id.fab_update_profile);
        fab_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("USER_INFO_ID_SP", USER_INFO_ID_SP);
                bundle.putString("USER_FAST_NAME_SP", USER_FAST_NAME_SP);
                bundle.putString("USER_LAST_NAME_SP", USER_LAST_NAME_SP);
                bundle.putString("USER_PASSWORD", USER_PASSWORD);
                bundle.putString("USER_PHONE_SP", USER_PHONE_SP);
                bundle.putString("USER_EMAIL_SP", USER_EMAIL_SP);
                bundle.putString("USER_ADDRESS_SP", USER_ADDRESS_SP);
                bundle.putString("SEV_PRO_NAME", SEV_PRO_NAME);
                bundle.putString("SEV_DELAIL", SEV_DELAIL);
                ProfileUpdate profileUpdate = new ProfileUpdate();
                profileUpdate.setArguments(bundle);
                profileUpdate.show(getSupportFragmentManager(), profileUpdate.getTag());
            }
        });
        tv_user_name=findViewById(R.id.tv_user_name);
        tv_email=findViewById(R.id.tv_email);
        tv_sur_name=findViewById(R.id.tv_sur_name);
        tv_phone=findViewById(R.id.tv_phone);
        tv_address=findViewById(R.id.tv_address);
        tv_password=findViewById(R.id.tv_password);

        tv_user_name.setText(USER_FAST_NAME_SP+" "+USER_LAST_NAME_SP);
        tv_email.setText(USER_EMAIL_SP);
        tv_sur_name.setText(USER_LAST_NAME_SP);
        tv_phone.setText(USER_PHONE_SP);
        tv_address.setText(USER_ADDRESS_SP);
        tv_password.setText(USER_PASSWORD);


    }
}
