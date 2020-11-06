package bd.com.arda.findyourhome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationPage extends AppCompatActivity {
    String str_SingInType = "", str_Phone = "", strFast_Name = "", str_Last_Name = "", str_Email = "", str_full_Name = "";
    TextInputEditText edt_Name, edt_phone, edt_email, edt_password, edt_gender;
    MaterialButton btn_next;
    RadioGroup radioSexGroup;
    RadioButton radioMale, radioFemale, radioOthers;
    String URLline = "http://103.91.54.60/apps/FYH/FYM_USER_LOGIN.php", gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);

        edt_Name = findViewById(R.id.edt_Name);
        edt_phone = findViewById(R.id.edt_phone);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        radioSexGroup = findViewById(R.id.radioSex);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        radioOthers = findViewById(R.id.radioOthers);
        btn_next = findViewById(R.id.btn_next);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            str_SingInType = bundle.getString("SingInType");
            if (str_SingInType.equals("Google")) {
                str_Email = bundle.getString("Email");
                strFast_Name = bundle.getString("Fastname");
                str_Last_Name = bundle.getString("Lastname");
                str_full_Name = bundle.getString("Fullname");
                edt_email.setText(str_Email);
                edt_Name.setText(str_full_Name);
            } else {
                str_Phone = bundle.getString("PhoneNo");
                edt_phone.setText(str_Phone);
            }
        }
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioButtonID = radioSexGroup.getCheckedRadioButtonId();

                if (!edt_phone.getText().toString().trim().equals("")) {
                    if (!edt_email.getText().toString().trim().equals("")) {
                        if (!edt_password.getText().toString().equals("")) {
                            if (!edt_Name.getText().toString().equals("")) {
                                if (radioMale.isChecked()) {
                                    gender = "Male";
                                } else if (radioFemale.isChecked()) {
                                    gender = "Female";
                                } else if (radioOthers.isChecked()) {
                                    gender = "Others";
                                }


                                Intent intent = new Intent(RegistrationPage.this, TakeAddress.class);
                                intent.putExtra("Phone", edt_phone.getText().toString().trim());
                                intent.putExtra("Email", edt_email.getText().toString().trim());
                                intent.putExtra("Password", edt_password.getText().toString().trim());
                                intent.putExtra("Name", edt_Name.getText().toString().trim());
                                intent.putExtra("Gender", gender);
                                startActivity(intent);

                            } else {
                                Snackbar.make(v, "Enter The Name.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } else {
                            Snackbar.make(v, "Enter The Password.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(v, "Enter The Courrrect Email Address.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(v, "Enter The Phone Number.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


    }


}
