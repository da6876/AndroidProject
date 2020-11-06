package soc.bd.com.findmehere;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static soc.bd.com.findmehere.LoginPage.rootUrl;

public class RegistrationPage extends AppCompatActivity {
    EditText edt_Name,edt_PhoneNO,edt_Email,edt_Password,edt_Con_Password;
    Button btn_Sing_up;
    String str_singUp_type="",str_full_name="",str_fast_name="",str_email_address="",
            str_phone_number="";
    private ProgressDialog pd;
    String URLline = rootUrl+"ADD_FMH_USER_INFO.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);



        edt_Name=findViewById(R.id.edt_Name);
        edt_PhoneNO=findViewById(R.id.edt_PhoneNO);
        edt_Email=findViewById(R.id.edt_Email);
        edt_Password=findViewById(R.id.edt_Password);
        edt_Con_Password=findViewById(R.id.edt_Con_Password);
        btn_Sing_up=findViewById(R.id.btn_Sing_up);
        btn_Sing_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            str_singUp_type=bundle.getString("SingUpType");
            if (str_singUp_type.equals("Google")){
                str_full_name=bundle.getString("fullname");
                str_email_address=bundle.getString("emailName");

                edt_Name.setText(str_full_name);
                edt_Email.setText(str_email_address);
            }else{
                str_phone_number=bundle.getString("PhoneNo");
                edt_PhoneNO.setText(str_phone_number);
            }
        }
    }

    private void checkInfo() {
        if (!edt_Name.getText().toString().equals("")){
            if (!edt_PhoneNO.getText().toString().equals("")){
                if (!edt_Email.getText().toString().equals("")){
                    if (!edt_Password.getText().toString().equals("")){
                        if (!edt_Con_Password.getText().toString().equals("")){
                            if (edt_Con_Password.getText().toString().equals(edt_Password.getText().toString())){
                                singNow();
                            }else{
                                showDialog("Message","Password Don't Match !");
                            }
                        }else{
                            showDialog("Message","Enter Confirm Password !");
                        }
                    }else{
                        showDialog("Message","Enter Your Password !");
                    }
                }else{
                    showDialog("Message","Enter Your Email Address !");
                }
            }else{
                showDialog("Message","Enter Your Phone Number !");
            }
        }else{
            showDialog("Message","Enter Your Full Name !");
        }
    }


    private void singNow() {
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URLline, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null) {
                    pd.dismiss();
                }
                parceData(response);
                //  Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pd != null) {
                    pd.dismiss();
                }
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                showFailedDialog(RegistrationPage.this,"Failed",error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("P_USER_NAME",edt_Name.getText().toString().trim());
                params.put("P_USER_PASSWORD",edt_Password.getText().toString().trim());
                params.put("P_USER_PHONE",edt_PhoneNO.getText().toString().trim());
                params.put("P_USER_EMAIL",edt_Email.getText().toString().trim());
                params.put("P_USER_REFER_NO",str_phone_number);
                params.put("P_USER_STATUS","R");
                params.put("P_CREATE_BY",edt_Name.getText().toString().trim());
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void parceData(String response) {
        try {
            JSONObject jsonObject= new JSONObject(response.toString());
            String responces= jsonObject.getString("responce");
            JSONObject jsonObject1=new JSONObject(responces.toString());
            String status_code=jsonObject1.getString("status_code");
            String msg=jsonObject1.getString("msg");
            String values=jsonObject1.getString("values");
            if (status_code.equals("500")){
                showSuccessDialog(RegistrationPage.this,msg,values);
            }else {
                showFailedDialog(RegistrationPage.this,msg,values);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showFailedDialog(Context context, String title, String msg){
        new AlertDialog.Builder(context).setIcon(R.drawable.ic_menu_send).setTitle(title).setMessage(msg).
                setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
    private void showSuccessDialog(Context context, String title, String msg){
        new AlertDialog.Builder(context).setIcon(R.drawable.ic_menu_send).setTitle(title).setMessage(msg).
                setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent goNew = new Intent(RegistrationPage.this, LoginPage.class);
                        goNew.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(goNew);
                        finish();
                        dialogInterface.dismiss();
                    }
                }).show();
    }


    private void showDialog(String Title,String Msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(Title);
        builder.setMessage(Msg);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
