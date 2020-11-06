package bd.com.arda.findyourhome.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bd.com.arda.findyourhome.LogInPage;
import bd.com.arda.findyourhome.MainActivity;
import bd.com.arda.findyourhome.R;
import bd.com.arda.findyourhome.TakeAddress;

public class ProfileEditFragment extends BottomSheetDialogFragment {
    String ADDRESS = "", USER_ACC_ID = "", USER_NAME = "", USER_EMAIL = "", LOGLITUTDE = "", LATTITUDE = "", USER_PASSWORD = "",
            GENDER = "", USER_PHONE = "";

    TextView edt_name, edt_email, edt_phone,edt_password, edt_address, edt_latitude, edt_longtitude;
    RadioGroup radioSexGroup;
    RadioButton radioMale, radioFemale, radioOthers;
    MaterialButton btn_update_profile,btn_get_address;
    String ssaddress = "", area = "", postcode = "", fullAddress = "";
    String URLline = "http://103.91.54.60/apps/FYH/FYH_UPDATE_USER_PROFILE.php";
    private ProgressDialog pd;
    View v;
    SharedPreferences sharedPreferences;
    Geocoder geocoder;
    List<Address> addresses;
    private static final int PLACE_PICKER_REQUEST = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.profile_efit, container, false);

        sharedPreferences = getActivity().getSharedPreferences("FYH_UserData", Context.MODE_PRIVATE);
        btn_update_profile = v.findViewById(R.id.btn_update_profile);
        btn_get_address = v.findViewById(R.id.btn_get_address);
        btn_get_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleMap();
            }
        });
        edt_name = v.findViewById(R.id.edt_name);
        edt_phone = v.findViewById(R.id.edt_phone);
        edt_email = v.findViewById(R.id.edt_email);
        edt_password = v.findViewById(R.id.edt_password);
        edt_address = v.findViewById(R.id.edt_address);
        edt_latitude = v.findViewById(R.id.edt_latitude);
        edt_longtitude = v.findViewById(R.id.edt_longtitude);
        radioSexGroup = v.findViewById(R.id.radioSex);
        radioMale = v.findViewById(R.id.radioMale);
        radioFemale = v.findViewById(R.id.radioFemale);
        radioOthers = v.findViewById(R.id.radioOthers);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            USER_ACC_ID = bundle.getString("USER_ACC_ID");
            USER_NAME = bundle.getString("USER_NAME");
            USER_EMAIL = bundle.getString("USER_EMAIL");
            USER_PHONE = bundle.getString("USER_PHONE");
            ADDRESS = bundle.getString("ADDRESS");
            USER_PASSWORD = bundle.getString("USER_PASSWORD");
            LATTITUDE = bundle.getString("LATTITUDE");
            LOGLITUTDE = bundle.getString("LOGLITUTDE");
            GENDER = bundle.getString("GENDER");
        }
        if (USER_NAME.equals("") || USER_NAME.equals(null) || USER_NAME.equals("null")) {
            edt_name.setText("");
        } else {
            edt_name.setText(USER_NAME);
        }
        if (USER_EMAIL.equals("") || USER_EMAIL.equals(null) || USER_EMAIL.equals("null")) {
            edt_email.setText("");
        } else {
            edt_email.setText(USER_EMAIL);
        }
        if (USER_PHONE.equals("") || USER_PHONE.equals(null) || USER_PHONE.equals("null")) {
            edt_phone.setText("");
        } else {
            edt_phone.setText(USER_PHONE);
        }
        if (ADDRESS.equals("") || ADDRESS.equals(null) || ADDRESS.equals("null")) {
            edt_address.setText("");
        } else {
            edt_address.setText(ADDRESS);
        }
        if (LATTITUDE.equals("") || LATTITUDE.equals(null) || LATTITUDE.equals("null")) {
            edt_latitude.setText("");
        } else {
            edt_latitude.setText(LATTITUDE);
        }
        if (LOGLITUTDE.equals("") || LOGLITUTDE.equals(null) || LOGLITUTDE.equals("null")) {
            edt_longtitude.setText("");
        } else {
            edt_longtitude.setText(LOGLITUTDE);
        }
        if (USER_PASSWORD.equals("") || USER_PASSWORD.equals(null) || USER_PASSWORD.equals("null")) {
            edt_password.setText("");
        } else {
            edt_password.setText(USER_PASSWORD);
        }



        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioMale.isChecked()) {
                    GENDER = "Male";
                    dataCheck();
                } else if (radioFemale.isChecked()) {
                    GENDER = "Female";
                    dataCheck();
                } else if (radioOthers.isChecked()) {
                    GENDER = "Others";
                    dataCheck();
                } else {
                    Toast.makeText(getContext(),"Please Select Gender",Toast.LENGTH_LONG).show();
                }


            }

        });

        return v;
    }


    private void dataCheck() {
        if (!edt_name.getText().toString().equals("")){
            if (!edt_phone.getText().toString().equals("")){
                if (!edt_email.getText().toString().equals("")){
                    if (!edt_address.getText().toString().equals("")){
                        if (!edt_latitude.getText().toString().equals("")){
                            if (!edt_longtitude.getText().toString().equals("")){
                                updateProfileData();
                                pd = ProgressDialog.show(getContext(), "Checking Information",
                                        "Please wait...");
                            }else {
                                Toast.makeText(getContext(),"Could Not Find The Location ",Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(getContext(),"Could Not Find The Location",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getContext(),"Please Add Your Address",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Please Add Email",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getContext(),"Please Add Phone",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getContext(),"Please Enter Your Name",Toast.LENGTH_LONG).show();
        }

    }

    private void updateProfileData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
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
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_USER_ACC_ID", USER_ACC_ID);
                params.put("P_USER_NAME", edt_name.getText().toString());
                params.put("P_USER_EMAIL", edt_email.getText().toString());
                params.put("P_USER_PASSWORD", edt_password.getText().toString());
                params.put("P_LATTITUDE", edt_latitude.getText().toString());
                params.put("P_LOGLITUTDE", edt_longtitude.getText().toString());
                params.put("P_ADDRESS", edt_address.getText().toString());
                params.put("P_USER_PHONE", edt_phone.getText().toString());
                params.put("P_GENDER", GENDER);
                params.put("P_UPDATE_BY", USER_NAME);
                params.put("P_STATUS", "A");

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                alertDialog(getContext(),msg,values);
            } else {
                if (pd != null) {
                    pd.dismiss();
                }
                alertDialog(getContext(),msg,values);

            }
        } catch (JSONException e) {
            if (pd != null) {
                pd.dismiss();
            }
            e.printStackTrace();
            Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG);
        }

    }

    private void goToGoogleMap() {

        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(intentBuilder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException es) {
            es.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getContext());
                StringBuilder stringBuilder = new StringBuilder();
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                final CharSequence address = place.getAddress();

                if (address.equals(""))
                    try {
                        addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                        ssaddress = addresses.get(0).getAddressLine(0);
                        area = addresses.get(0).getLocality();
                        postcode = addresses.get(0).getPostalCode();
                        fullAddress = ssaddress + "," + area + "," + postcode;
                        edt_address.setText(fullAddress);
                        edt_latitude.setText(latitude);
                        edt_longtitude.setText(longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                else
                    edt_address.setText(address);
                edt_latitude.setText(latitude);
                edt_longtitude.setText(longitude);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void alertDialog(android.content.Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(getActivity(), LogInPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dismiss();
                        dialog.dismiss();
                    }
                }).show();
    }
}
