package bd.com.arda.findyourhome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.arda.findyourhome.adapter.ProfileEditFragment;
import bd.com.arda.findyourhome.adapter.ToletDetailsFragment;

public class Profilepage extends AppCompatActivity {
    TextView tv_Name,tv_referNumber,tv_point,tv_gender,tv_email,tv_phone,tv_address;
    MaterialButton material_icon_button;
    String URLline = "http://103.91.54.60/apps/FYH/FYH_USER_DATA.php";
    private ProgressDialog pd;
    SharedPreferences sharedpreferences;
    String USER_ACC_ID="",USER_NAME="",USER_EMAIL="",USER_PASSWORD="",LATTITUDE="",LOGLITUTDE="",ADDRESS=""
            ,USER_REFER_ID="",USER_POINT="",
            USER_PHONE="",GENDER="";
    Button openBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);


        tv_Name=findViewById(R.id.tv_Name);
        tv_referNumber=findViewById(R.id.tv_referNumber);
        tv_point=findViewById(R.id.tv_point);
        tv_gender=findViewById(R.id.tv_gender);
        tv_email=findViewById(R.id.tv_email);
        tv_phone=findViewById(R.id.tv_phone);
        tv_address=findViewById(R.id.tv_address);

        sharedpreferences = getSharedPreferences("FYH_UserData", Context.MODE_PRIVATE);
        USER_ACC_ID= sharedpreferences.getString("USER_ACC_ID","0");
        USER_NAME= sharedpreferences.getString("USER_NAME","0");
        USER_EMAIL= sharedpreferences.getString("USER_EMAIL","0");
        USER_PASSWORD= sharedpreferences.getString("USER_PASSWORD","0");
        LATTITUDE= sharedpreferences.getString("LATTITUDE","0");
        LOGLITUTDE= sharedpreferences.getString("LOGLITUTDE","0");
        ADDRESS= sharedpreferences.getString("ADDRESS","0");
        USER_REFER_ID= sharedpreferences.getString("USER_REFER_ID","0");

        if (!USER_REFER_ID.equals(null))
            tv_referNumber.setText(USER_REFER_ID);
        else
            tv_referNumber.setText("");
        USER_POINT= sharedpreferences.getString("USER_POINT","0");
        if (!USER_POINT.equals("null"))
            tv_point.setText(USER_POINT);
        else
            tv_point.setText("00");
        USER_PHONE= sharedpreferences.getString("USER_PHONE","0");
        if (!USER_PHONE.equals(null))
            tv_phone.setText(USER_PHONE);
        else
            tv_phone.setText("");
        GENDER= sharedpreferences.getString("GENDER","0");
        if (!GENDER.equals(null))
            tv_gender.setText(GENDER);
        else
            tv_gender.setText("");

        tv_Name.setText(USER_NAME);
        tv_email.setText(USER_EMAIL);
        tv_address.setText(ADDRESS);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Bundle bundle = new Bundle();
            bundle.putString("USER_ACC_ID",USER_ACC_ID);
            bundle.putString("USER_NAME",USER_NAME);
            bundle.putString("USER_EMAIL", USER_EMAIL);
            bundle.putString("USER_PHONE", USER_PHONE);
            bundle.putString("ADDRESS", ADDRESS);
            bundle.putString("USER_PASSWORD", USER_PASSWORD);
            bundle.putString("LATTITUDE", LATTITUDE);
            bundle.putString("LOGLITUTDE", LOGLITUTDE);
            bundle.putString("GENDER", GENDER);
            ProfileEditFragment toletDetailsFragment = new ProfileEditFragment();
            toletDetailsFragment.setArguments(bundle);
            toletDetailsFragment.show(getSupportFragmentManager(), toletDetailsFragment.getTag());
        }

        return super.onOptionsItemSelected(item);
    }

}
