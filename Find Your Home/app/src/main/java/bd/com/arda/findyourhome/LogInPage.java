package bd.com.arda.findyourhome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.MediaRouteButton;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogInPage extends AppCompatActivity {
    TextInputEditText edt_password, edt_phone_number;
    MaterialButton material_icon_button,btn_SingUp;

    public static String URLROOT = "http://103.91.54.60/apps/FYH/";
    String URLLogin = URLROOT+"FYM_USER_LOGIN.php";
    private ProgressDialog pd;
    SharedPreferences sharedpreferences;
    String STATUS_CODE="",USER_ACC_ID="",USER_NAME="",USER_EMAIL="",USER_PASSWORD="",LATTITUDE="",LOGLITUTDE="",ADDRESS=""
            ,USER_REFER_ID="",USER_POINT="",check_User_logIn="",UPDATE_BY="",UPDATE_DATE="",
            USER_PHONE="",CREATE_DATE="",CREATE_BY="",STATUS="",GENDER="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_page);

        sharedpreferences = getSharedPreferences("FYH_UserData", Context.MODE_PRIVATE);
        check_User_logIn= sharedpreferences.getString("USER_ACC_ID","0");
        if (!check_User_logIn.equals("0")){
            startActivity(new Intent(LogInPage.this,MainActivity.class));
        }

        edt_phone_number = findViewById(R.id.edt_phone_number);
        edt_password = findViewById(R.id.edt_password);
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                pd = ProgressDialog.show(LogInPage.this, "Checking Information",
                        "Please wait...");
                loginUser();
                return true;
            }
        });
        material_icon_button = findViewById(R.id.material_icon_button);
        btn_SingUp = findViewById(R.id.btn_SingUp);
        btn_SingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInPage.this,PhoneVerification.class));
            }
        });
        material_icon_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pd = ProgressDialog.show(LogInPage.this, "Checking Information","Please wait...");
                loginUser();
            }
        });
    }

    private void loginUser() {

        final String username = edt_phone_number.getText().toString().trim();
        final String password = edt_password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Toast.makeText(LogInPage.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_USER_NAME", username);
                params.put("P_USER_PASSWORD", password);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseData(String response) {

        try {
            JSONObject obj = new JSONObject(response);
            String responcse = obj.getString("responce");
            JSONObject obj2 = new JSONObject(responcse);
            String status_code = obj2.getString("status_code");
            String msg = obj2.getString("msg");
            String values = obj2.getString("values");
            if (status_code.equals("200")) {
                if (pd != null) {
                    pd.dismiss();
                }
                alertDialog(LogInPage.this,msg,values);
            } else {
                if (pd != null) {
                    pd.dismiss();
                }
                JSONArray jArray = new JSONArray(values.toString());
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject dataobj = jArray.getJSONObject(i);
                    STATUS_CODE = dataobj.getString("STATUS_CODE");
                    USER_ACC_ID = dataobj.getString("USER_ACC_ID");
                    USER_NAME = dataobj.getString("USER_NAME");
                    USER_EMAIL = dataobj.getString("USER_EMAIL");
                    USER_PASSWORD = dataobj.getString("USER_PASSWORD");
                    LATTITUDE = dataobj.getString("LATTITUDE");
                    LOGLITUTDE = dataobj.getString("LOGLITUTDE");
                    ADDRESS = dataobj.getString("ADDRESS");
                    USER_REFER_ID = dataobj.getString("USER_REFER_ID");
                    USER_POINT = dataobj.getString("USER_POINT");
                    USER_PHONE = dataobj.getString("USER_PHONE");
                    CREATE_DATE = dataobj.getString("CREATE_DATE");
                    CREATE_BY = dataobj.getString("CREATE_BY");
                    UPDATE_BY = dataobj.getString("UPDATE_BY");
                    UPDATE_DATE = dataobj.getString("UPDATE_DATE");
                    STATUS = dataobj.getString("STATUS");
                    GENDER = dataobj.getString("GENDER");
                }
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("STATUS_CODE", STATUS_CODE);
                editor.putString("USER_ACC_ID", USER_ACC_ID);
                editor.putString("USER_NAME", USER_NAME);
                editor.putString("USER_EMAIL", USER_EMAIL);
                editor.putString("USER_PASSWORD", USER_PASSWORD);
                editor.putString("LATTITUDE", LATTITUDE);
                editor.putString("LOGLITUTDE", LOGLITUTDE);
                editor.putString("ADDRESS", ADDRESS);
                editor.putString("USER_REFER_ID", USER_REFER_ID);
                editor.putString("USER_POINT", USER_POINT);
                editor.putString("USER_PHONE", USER_PHONE);
                editor.putString("CREATE_DATE", CREATE_DATE);
                editor.putString("CREATE_BY", CREATE_BY);
                editor.putString("UPDATE_BY", UPDATE_BY);
                editor.putString("UPDATE_DATE", UPDATE_DATE);
                editor.putString("STATUS", STATUS);
                editor.putString("GENDER", GENDER);
                editor.commit();
                startActivity(new Intent(LogInPage.this, MainActivity.class));
            }
        } catch (JSONException e) {
            if (pd != null) {
                pd.dismiss();
            }
            e.printStackTrace();
        }

    }

    public void alertDialog(android.content.Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub

                        if (pd != null) {
                            pd.dismiss();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }


}
