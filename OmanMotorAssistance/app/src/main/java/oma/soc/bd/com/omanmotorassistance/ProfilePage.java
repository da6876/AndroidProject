package oma.soc.bd.com.omanmotorassistance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import oma.soc.bd.com.omanmotorassistance.adapter.ProfileUpdate;

public class ProfilePage extends AppCompatActivity {
    TextView tv_user_name,tv_email,tv_sur_name,tv_phone,tv_address,tv_password;
    String USER_INFO_ID,USER_FAST_NAME,USER_LAST_NAME,USER_PHONE,str_address,USER_EMAIL,
            LOCATION_CODE,USER_ADDRESS,USER_LATITUDE,USER_LONGITUDE,USER_PASSWORD;
    SharedPreferences sharedPreferences;
    FloatingActionButton fab_update_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        sharedPreferences = getSharedPreferences("OAA_DATA", Context.MODE_PRIVATE);
        USER_INFO_ID=sharedPreferences.getString("USER_INFO_ID","0");
        USER_FAST_NAME=sharedPreferences.getString("USER_FAST_NAME","0");
        USER_LAST_NAME=sharedPreferences.getString("USER_LAST_NAME","0");
        USER_PHONE=sharedPreferences.getString("USER_PHONE","0");
        USER_EMAIL=sharedPreferences.getString("USER_EMAIL","0");
        LOCATION_CODE=sharedPreferences.getString("LOCATION_CODE","0");
        USER_ADDRESS=sharedPreferences.getString("USER_ADDRESS","0");
        USER_LATITUDE=sharedPreferences.getString("USER_LATITUDE","0");
        USER_LONGITUDE=sharedPreferences.getString("USER_LONGITUDE","0");
        USER_PASSWORD=sharedPreferences.getString("USER_PASSWORD","0");
        //str_password=sharedPreferences.getString("LOGIN_STATUS","0");

        tv_user_name=findViewById(R.id.tv_user_name);
        tv_email=findViewById(R.id.tv_email);
        tv_sur_name=findViewById(R.id.tv_sur_name);
        tv_phone=findViewById(R.id.tv_phone);
        tv_address=findViewById(R.id.tv_address);
        tv_password=findViewById(R.id.tv_password);

        tv_user_name.setText(USER_FAST_NAME+" "+USER_LAST_NAME);
        tv_email.setText(USER_EMAIL);
        tv_sur_name.setText(USER_LAST_NAME);
        tv_phone.setText(USER_PHONE);
        tv_address.setText(USER_ADDRESS);

        fab_update_profile=findViewById(R.id.fab_update_profile);
        fab_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("USER_INFO_ID", USER_INFO_ID);
                bundle.putString("USER_FAST_NAME", USER_FAST_NAME);
                bundle.putString("USER_LAST_NAME", USER_LAST_NAME);
                bundle.putString("USER_PHONE", USER_PHONE);
                bundle.putString("USER_EMAIL", USER_EMAIL);
                bundle.putString("LOCATION_CODE", LOCATION_CODE);
                bundle.putString("USER_ADDRESS", USER_ADDRESS);
                bundle.putString("USER_LATITUDE", USER_LATITUDE);
                bundle.putString("USER_LONGITUDE", USER_LONGITUDE);
                bundle.putString("USER_PASSWORD", USER_PASSWORD);
                ProfileUpdate profileUpdate = new ProfileUpdate();
                profileUpdate.setArguments(bundle);
                profileUpdate.show(getSupportFragmentManager(), profileUpdate.getTag());
            }
        });


    }
}
