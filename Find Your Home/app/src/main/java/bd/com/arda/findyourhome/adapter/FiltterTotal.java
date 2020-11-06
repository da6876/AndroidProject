package bd.com.arda.findyourhome.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bd.com.arda.findyourhome.LogInPage;
import bd.com.arda.findyourhome.PostTolet;
import bd.com.arda.findyourhome.R;
import bd.com.arda.findyourhome.model.ToletListModel;

import static bd.com.arda.findyourhome.LogInPage.URLROOT;

public class FiltterTotal extends BottomSheetDialogFragment {

    ArrayList<ToletListModel> listtofcustomer = new ArrayList<>();
    ArrayList<String> VEHICLE_ID = new ArrayList<>();
    ArrayList<String> LATTITUDE = new ArrayList<>();
    ArrayList<String> LOGLITUTDE = new ArrayList<>();

    MaterialSpinner tolet_type_SP, location_sp;
    EditText edt_price_Min, edt_price_max, edt_baths, edt_beds, edt_floors, edt_avilable_date;
    MaterialButton btn_get_date, btn_update_profile;
    ImageView iv_clear,iv_done,iv_cancle;

    String URL_filter_now = URLROOT + "FYH_GET_TOLET_FILTER.php";
    String URL_Loc_List = URLROOT + "FYH_GET_LOCATION_LIST.php";
    String URL_Tolet_type = URLROOT + "FYH_GET_TOLET_TYPE.php";
    ProgressDialog pd;

    ArrayList<String> TOLET_TYPE_ID_AR = new ArrayList<>();
    ArrayList<String> TOLET_TYPE_NAME_AR = new ArrayList<>();
    ArrayList<String> LOCATION_CODE_AR = new ArrayList<>();
    ArrayList<String> LOCATION_NAME_AR = new ArrayList<>();

    String str_USer_id = "", str_tolet_type_id = "0000000", str_location_code = "0000000", str_price_min = "", str_price_max = "",
            str_baths = "", str_beds = "", str_floors = "", str_avilable_date = "";
    private int mYear, mMonth, mDay;

    SharedPreferences sharedPreferences;

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.filtter_tolet, container, false);

        sharedPreferences = getActivity().getSharedPreferences("FYH_UserData", Context.MODE_PRIVATE);
        str_USer_id=sharedPreferences.getString("USER_ACC_ID","");

        tolet_type_SP = v.findViewById(R.id.tolet_type_SP);
        location_sp = v.findViewById(R.id.location_sp);
        edt_price_Min = v.findViewById(R.id.edt_price_Min);
        edt_price_max = v.findViewById(R.id.edt_price_max);
        edt_baths = v.findViewById(R.id.edt_baths);
        edt_beds = v.findViewById(R.id.edt_beds);
        edt_floors = v.findViewById(R.id.edt_floors);
        edt_avilable_date = v.findViewById(R.id.edt_avilable_date);
        iv_clear = v.findViewById(R.id.iv_clear);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        iv_done = v.findViewById(R.id.iv_done);
        iv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataCheck();
            }
        });
        iv_cancle = v.findViewById(R.id.iv_cancle);
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_get_date = v.findViewById(R.id.btn_get_date);
        btn_get_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edt_avilable_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        GetToletType();
        GetlocationList();
        pd = ProgressDialog.show(getContext(), "Getting Data",
                "Please wait...");
        return v;
    }

    private void GetToletType() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Tolet_type,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                                ArrayAdapter adapterComplain = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, TOLET_TYPE_NAME_AR);
                                adapterComplain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                tolet_type_SP.setAdapter(adapterComplain);
                                tolet_type_SP.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                        str_tolet_type_id = TOLET_TYPE_ID_AR.get(position);
                                    }
                                });

                            }
                        } catch (JSONException e) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            e.printStackTrace();
                        }
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
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void GetlocationList() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Loc_List,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                                    LOCATION_CODE_AR.clear();
                                }
                                if (jArray.length() > 1) {
                                    LOCATION_CODE_AR.add("0000000");
                                    LOCATION_NAME_AR.add("Select Location");
                                }

                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    LOCATION_CODE_AR.add(json_data.getString("LOCATION_CODE"));
                                    LOCATION_NAME_AR.add(json_data.getString("LOCATION_NAME"));

                                }
                                ArrayAdapter adapterComplain = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, LOCATION_NAME_AR);
                                adapterComplain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                location_sp.setAdapter(adapterComplain);
                                location_sp.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                        str_location_code = LOCATION_CODE_AR.get(position);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            e.printStackTrace();
                        }
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
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void dataCheck() {
        str_price_min = edt_price_Min.getText().toString().trim();
        str_price_max = edt_price_max.getText().toString().trim();
        str_baths = edt_baths.getText().toString().trim();
        str_beds = edt_beds.getText().toString().trim();
        str_floors = edt_floors.getText().toString().trim();
        str_avilable_date = edt_avilable_date.getText().toString().trim();

        if (!str_tolet_type_id.equals("0000000")) {
            if (!str_location_code.equals("0000000")) {
                if (!str_avilable_date.equals("")) {
                    updateProfileData();
                    pd = ProgressDialog.show(getContext(), "Checking Information",
                            "Please wait...");
                } else {
                    Toast.makeText(getContext(), "Select Avilable Date ", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Enter Floor", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Please Add Your Address", Toast.LENGTH_LONG).show();
        }

    }

    private void updateProfileData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_filter_now,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jArray = new JSONArray(response);
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject json_data1 = jArray.getJSONObject(i);
                                VEHICLE_ID.add(json_data1.getString("ADDRESS"));
                                LATTITUDE.add(json_data1.getString("LATTITUDE"));
                                LOGLITUTDE.add(json_data1.getString("LOGLITUTDE"));
                            }
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject json_data = jArray.getJSONObject(i);
                                ToletListModel toletListModel = new ToletListModel(
                                        json_data.getString("TOLET_INFO_ID"),
                                        json_data.getString("USER_ACC_ID"),
                                        json_data.getString("TOLET_NAME"),
                                        json_data.getString("TOLET_DETAILS"),
                                        json_data.getString("ADDRESS"),
                                        json_data.getString("LATTITUDE"),
                                        json_data.getString("LOGLITUTDE"),
                                        json_data.getString("PRICE"),
                                        json_data.getString("BATHS"),
                                        json_data.getString("BEDS"),
                                        json_data.getString("FLOORS"),
                                        json_data.getString("AVAILABLE_FROM"),
                                        json_data.getString("CONTACT_PERSON_NM"),
                                        json_data.getString("CONTACT_PERSON_PHN"),
                                        json_data.getString("CONTACT_PERSON_EML"),
                                        json_data.getString("TOLET_TYPE_ID"),
                                        json_data.getString("TOLET_TYPE_NAME"),
                                        json_data.getString("PRODUCT_IMAGE"),
                                        json_data.getString("CREATE_DATA"),
                                        json_data.getString("CREATE_BY")
                                );
                                listtofcustomer.add(toletListModel);
                            }
                            if (pd != null) {
                                pd.dismiss();
                            }
                        } catch (JSONException e) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            e.printStackTrace();
                        }
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

                params.put("P_USER_ACC_ID", str_USer_id);
                params.put("P_LOCATION_CODE", str_location_code);
                if (str_beds.equals("")){
                    str_beds="100";
                    params.put("P_BEDS", str_beds);
                }else {
                    params.put("P_BEDS", str_beds);
                }
                if (str_baths.equals("")){
                    str_baths="100";
                    params.put("P_BATHS", str_baths);
                }else {
                    params.put("P_BATHS", str_baths);
                }
                if (str_price_min.equals("")){
                    str_price_min="0";
                    params.put("P_MIN", str_price_min);
                }else {
                    params.put("P_MIN", str_price_min);
                }
                if (str_price_max.equals("")){
                    str_price_max="100000";
                    params.put("P_MAX", str_price_max);
                }else {
                    params.put("P_MAX", str_price_max);
                }

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    public void alertDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dataCheck();
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
