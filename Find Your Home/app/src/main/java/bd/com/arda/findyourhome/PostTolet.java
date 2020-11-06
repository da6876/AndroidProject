package bd.com.arda.findyourhome;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bd.com.arda.findyourhome.model.GetUri;

import static bd.com.arda.findyourhome.LogInPage.URLROOT;

public class PostTolet extends AppCompatActivity {
    MaterialSpinner spinner;
    TextInputEditText edt_tolet_name, edt_tolet_details, edt_address, edt_price, edt_baths,
            edt_beds, edt_floors, edt_avilable_date, edt_contact_person_name, edt_contact_phone_number,
            edt_contact_person_email, edt_latitude, edt_longtitude;
    MaterialButton btn_get_address, btn_get_date, btn_next;
    String str_tolet_name, str_tolet_details, str_address, str_price, str_baths,
            str_beds, str_floors, str_avilable_date, str_contact_person_name, str_contact_phone_number,
            str_contact_person_email, str_latitude, str_longtitude, str_tolet_type_id;
    String OpenType = "", CONTACT_PERSON_NM = "", CONTACT_PERSON_PHN = "", CONTACT_PERSON_EML = "", FLOORS = "",
            ADDRESS = "", AVAILABLE_FROM = "", BATHS = "", BEDS = "", LOGLITUTDE = "", LATTITUDE = "", PRICE = "",
            TOLET_DETAILS = "", TOLET_TYPE_ID = "", TOLET_NAME = "";
    ArrayList<String> TOLET_TYPE_ID_AR = new ArrayList<>();
    ArrayList<String> TOLET_TYPE_NAME_AR = new ArrayList<>();
    String URL_GET_TOTLET_TYPE = URLROOT+"FYH_GET_TOLET_TYPE.php";

    private static final int PLACE_PICKER_REQUEST = 1;
    String ssaddress = "", area = "", postcode = "", fullAddress = "";
    Geocoder geocoder;
    List<Address> addresses;
    private int mYear, mMonth, mDay;
    ProgressDialog pd;
    ImageView ivProfilePicture;
    GetUri getUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_tolet);

        edt_tolet_name = findViewById(R.id.edt_tolet_name);
        edt_tolet_details = findViewById(R.id.edt_tolet_name);
        edt_address = findViewById(R.id.edt_address);
        edt_price = findViewById(R.id.edt_price);
        edt_baths = findViewById(R.id.edt_baths);
        edt_beds = findViewById(R.id.edt_beds);
        edt_floors = findViewById(R.id.edt_floors);
        edt_avilable_date = findViewById(R.id.edt_avilable_date);
        edt_contact_person_name = findViewById(R.id.edt_contact_person_name);
        edt_contact_phone_number = findViewById(R.id.edt_contact_phone_number);
        edt_contact_person_email = findViewById(R.id.edt_contact_person_email);
        edt_latitude = findViewById(R.id.edt_latitude);
        edt_longtitude = findViewById(R.id.edt_longtitude);
        btn_get_address = findViewById(R.id.btn_get_address);
        btn_get_date = findViewById(R.id.btn_get_date);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            OpenType = bundle.getString("OpenType");
            if (OpenType.equals("EditProfile")) {
                CONTACT_PERSON_NM = bundle.getString("CONTACT_PERSON_NM");
                CONTACT_PERSON_PHN = bundle.getString("CONTACT_PERSON_PHN");
                CONTACT_PERSON_EML = bundle.getString("CONTACT_PERSON_EML");
                FLOORS = bundle.getString("FLOORS");
                ADDRESS = bundle.getString("ADDRESS");
                BATHS = bundle.getString("BATHS");
                AVAILABLE_FROM = bundle.getString("AVAILABLE_FROM");
                BEDS = bundle.getString("BEDS");
                LOGLITUTDE = bundle.getString("LOGLITUTDE");
                LATTITUDE = bundle.getString("LATTITUDE");
                PRICE = bundle.getString("PRICE");
                TOLET_DETAILS = bundle.getString("TOLET_DETAILS");
                TOLET_TYPE_ID = bundle.getString("TOLET_TYPE_ID");
                TOLET_NAME = bundle.getString("TOLET_NAME");
            }else{
                edt_tolet_name.setText(TOLET_NAME);
                edt_address.setText(ADDRESS);
                edt_tolet_details.setText(TOLET_DETAILS);
                edt_price.setText(PRICE);
                edt_baths.setText(BATHS);
                edt_beds.setText(BEDS);
                edt_floors.setText(FLOORS);
                edt_avilable_date.setText(AVAILABLE_FROM);
                edt_contact_person_name.setText(CONTACT_PERSON_NM);
                edt_contact_phone_number.setText(CONTACT_PERSON_PHN);
                edt_contact_person_email.setText(CONTACT_PERSON_EML);
                edt_latitude.setText(LATTITUDE);
                edt_longtitude.setText(LOGLITUTDE);
            }
        }
        getUri = new GetUri();
        ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);


        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(PostTolet.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostTolet.this, new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        btn_get_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(PostTolet.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edt_avilable_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                str_tolet_type_id = TOLET_TYPE_ID_AR.get(position);
            }
        });
        geocoder = new Geocoder(this, Locale.getDefault());
        GetToletType();


        btn_get_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleMap();
            }
        });
    }

    private void checkData(){
        str_tolet_name = edt_tolet_name.getText().toString().trim();
        str_address = edt_address.getText().toString().trim();
        str_tolet_details = edt_tolet_details.getText().toString().trim();
        str_price = edt_price.getText().toString().trim();
        str_baths = edt_baths.getText().toString().trim();
        str_beds = edt_beds.getText().toString().trim();
        str_floors = edt_floors.getText().toString().trim();
        str_avilable_date = edt_avilable_date.getText().toString().trim();
        str_contact_person_name = edt_contact_person_name.getText().toString().trim();
        str_contact_phone_number = edt_contact_phone_number.getText().toString().trim();
        str_contact_person_email = edt_contact_person_email.getText().toString().trim();
        str_latitude = edt_latitude.getText().toString().trim();
        str_longtitude = edt_longtitude.getText().toString().trim();

        if (!str_tolet_name.equals("")) {
            if (!str_address.equals("")) {
                if (!str_price.equals("")) {
                    if (!str_baths.equals("")) {
                        if (!str_beds.equals("")) {
                            if (!str_avilable_date.equals("")) {
                                if (!str_contact_person_name.equals("")) {
                                    if (!str_contact_phone_number.equals("")) {
                                        if (!str_tolet_type_id.equals("0000000")) {
                                            Intent intent = new Intent(PostTolet.this, TakeImageTolet.class);
                                            intent.putExtra("str_tolet_name", str_tolet_name);
                                            intent.putExtra("str_address", str_address);
                                            intent.putExtra("str_tolet_details", str_tolet_details);
                                            intent.putExtra("str_tolet_type_id", str_tolet_type_id);
                                            intent.putExtra("str_address", str_address);
                                            intent.putExtra("str_latitude", str_latitude);
                                            intent.putExtra("str_longtitude", str_longtitude);
                                            intent.putExtra("str_price", str_price);
                                            intent.putExtra("str_baths", str_baths);
                                            intent.putExtra("str_beds", str_beds);
                                            intent.putExtra("str_floors", str_floors);
                                            intent.putExtra("str_avilable_date", str_avilable_date);
                                            intent.putExtra("str_contact_person_name", str_contact_person_name);
                                            intent.putExtra("str_contact_phone_number", str_contact_phone_number);
                                            intent.putExtra("str_contact_person_email", str_contact_person_email);
                                            startActivity(intent);
                                        } else {
                                            Snackbar.make(getWindow().getDecorView().getRootView(), "Select Tolet Type !", Snackbar.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Snackbar.make(getWindow().getDecorView().getRootView(), "Enter Connect Person Number !", Snackbar.LENGTH_LONG).show();
                                    }

                                } else {
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Enter Connect Name !", Snackbar.LENGTH_LONG).show();
                                }

                            } else {
                                Snackbar.make(getWindow().getDecorView().getRootView(), "Select The Date For Available From !", Snackbar.LENGTH_LONG).show();
                            }

                        } else {
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Enter Bed Room Number !", Snackbar.LENGTH_LONG).show();
                        }

                    } else {
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Enter Bath Room Number !", Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Enter Price !", Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(), "Enter Address !", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Enter Tolet Name !", Snackbar.LENGTH_LONG).show();
        }
    }

    private void goToGoogleMap() {

        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(intentBuilder.build(PostTolet.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException es) {
            es.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                try {
                    String latitude = String.valueOf(place.getLatLng().latitude);
                    String longitude = String.valueOf(place.getLatLng().longitude);
                    final CharSequence address = place.getAddress();

                    if (address.equals("")) {
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
                    } else {
                        edt_address.setText(address);
                        edt_latitude.setText(latitude);
                        edt_longtitude.setText(longitude);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void GetToletType() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_TOTLET_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseDataForGetToletType(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Toast.makeText(PostTolet.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseDataForGetToletType(String response) {

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
                // alertDialog(PostTolet.this,msg,values);
            } else {
                if (pd != null) {
                    pd.dismiss();
                }
                JSONArray jArray = new JSONArray(values.toString());

                if (jArray.length() > 0) {
                    TOLET_TYPE_ID_AR.clear();
                }
                if (jArray.length() > 1) {
                    TOLET_TYPE_ID_AR.add("0000000");
                    TOLET_TYPE_NAME_AR.add("Select Tolet Type ");
                }

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    TOLET_TYPE_ID_AR.add(json_data.getString("TOLET_TYPE_ID"));
                    TOLET_TYPE_NAME_AR.add(json_data.getString("TOLET_TYPE_NAME"));

                }
                ArrayAdapter adapterComplain = new ArrayAdapter(PostTolet.this, android.R.layout.simple_spinner_item, TOLET_TYPE_NAME_AR);
                adapterComplain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterComplain);

            }
        } catch (JSONException e) {
            if (pd != null) {
                pd.dismiss();
            }
            e.printStackTrace();
        }

    }


    public void alertDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub

                        dialog.dismiss();
                    }
                }).show();
    }

}
