package bd.com.arda.findyourhome.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


import bd.com.arda.findyourhome.LogInPage;
import bd.com.arda.findyourhome.PostTolet;
import bd.com.arda.findyourhome.R;

public class ToletDetailsFragment extends BottomSheetDialogFragment {
    String OpenType = "",CONTACT_PERSON_NM = "", CONTACT_PERSON_PHN = "", CONTACT_PERSON_EML = "", FLOORS = "",
            ADDRESS = "", AVAILABLE_FROM = "", BATHS = "", BEDS = "", LOGLITUTDE = "", LATTITUDE = "", PRICE = "",
            TOLET_DETAILS = "", TOLET_TYPE_ID = "", TOLET_NAME = "";

    TextView tv_tolet_name, tv_tolet_con_name, tv_avalable_Date, tv_tolet_details, tv_price, tv_Bath, tv_FLOORS, tv_BEDS, tv_address, tv_mobile, tv_email1;
    ImageButton editBtn_cancle,iv_location,iv_phone,ic_sms,ic_mail,editBtn_Edit;

    EditText edtKeyCode;
    String strkeyCode = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tolet_details, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            OpenType = bundle.getString("OpenType");
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
        }
        editBtn_Edit = v.findViewById(R.id.editBtn_Edit);
        editBtn_cancle = v.findViewById(R.id.editBtn_cancle);
        editBtn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        iv_location = v.findViewById(R.id.iv_location);
        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        iv_phone = v.findViewById(R.id.iv_phone);
        iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +CONTACT_PERSON_PHN ));
                startActivity(intent);
            }
        });
        ic_sms = v.findViewById(R.id.ic_sms);
        ic_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", CONTACT_PERSON_PHN, null)));
            }
        });
        ic_mail = v.findViewById(R.id.ic_mail);
        ic_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",CONTACT_PERSON_EML, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });


        tv_tolet_name = v.findViewById(R.id.tv_tolet_name);
        tv_tolet_con_name = v.findViewById(R.id.tv_tolet_con_name);
        tv_avalable_Date = v.findViewById(R.id.tv_avalable_Date);
        tv_tolet_details = v.findViewById(R.id.tv_tolet_details);
        tv_price = v.findViewById(R.id.tv_price);
        tv_Bath = v.findViewById(R.id.tv_Bath);
        tv_FLOORS = v.findViewById(R.id.tv_FLOORS);
        tv_BEDS = v.findViewById(R.id.tv_BEDS);
        tv_address = v.findViewById(R.id.tv_address);
        tv_mobile = v.findViewById(R.id.tv_mobile);
        tv_email1 = v.findViewById(R.id.tv_email1);
        if (CONTACT_PERSON_NM.equals("") || CONTACT_PERSON_NM.equals(null)|| CONTACT_PERSON_NM.equals("null")) {
            tv_tolet_con_name.setText("No Name Found");
        } else {
            tv_tolet_con_name.setText(CONTACT_PERSON_NM);
        }
        if (TOLET_NAME.equals("") || TOLET_NAME.equals(null)|| TOLET_NAME.equals("null")) {
            tv_tolet_name.setText("No Tolet Name");
        } else {
            tv_tolet_name.setText(TOLET_NAME);
        }
        if (AVAILABLE_FROM.equals("") || AVAILABLE_FROM.equals(null)|| AVAILABLE_FROM.equals("null")) {
            tv_avalable_Date.setText("No Date Found");
        } else {
            tv_avalable_Date.setText(AVAILABLE_FROM);
        }
        if (TOLET_DETAILS.equals("") || TOLET_DETAILS.equals(null)|| TOLET_DETAILS.equals("null")) {
            tv_tolet_details.setText("No Details Found");
        } else {
            tv_tolet_details.setText(TOLET_DETAILS);
        }
        if (PRICE.equals("") || PRICE.equals(null)|| PRICE.equals("null")) {
            tv_price.setText("00.000");
        } else {
            tv_price.setText(PRICE);
        }
        if (BATHS.equals("") || BATHS.equals(null)|| BATHS.equals("null")) {
            tv_Bath.setText("00");
        } else {
            tv_Bath.setText(BATHS);
        }
        if (FLOORS.equals("") || FLOORS.equals(null)|| FLOORS.equals("null")) {
            tv_FLOORS.setText("00");
        } else {
            tv_FLOORS.setText(FLOORS);
        }
        if (BEDS.equals("") || BEDS.equals(null)|| BEDS.equals("null")) {
            tv_BEDS.setText("00");
        } else {
            tv_BEDS.setText(BEDS);
        }
        if (ADDRESS.equals("") || ADDRESS.equals(null)|| ADDRESS.equals("null")) {
            tv_address.setText("No Address Found");
        } else {
            tv_address.setText(ADDRESS);
        }
        if (CONTACT_PERSON_PHN.equals("") || CONTACT_PERSON_PHN.equals(null)|| CONTACT_PERSON_PHN.equals("null")) {
            tv_mobile.setText("No Number Found");
        } else {
            tv_mobile.setText(CONTACT_PERSON_PHN);
        }
        if (CONTACT_PERSON_EML.equals("") || CONTACT_PERSON_EML.equals(null)|| CONTACT_PERSON_EML.equals("null")) {
            tv_email1.setText("No Email Found");
        } else {
            tv_email1.setText(CONTACT_PERSON_EML);
        }

        if (OpenType.equals("Single")){
            editBtn_Edit.setVisibility(View.VISIBLE);
        }else {
            editBtn_Edit.setVisibility(View.GONE);
        }
        editBtn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),PostTolet.class);
                intent.putExtra("OpenType","EditProfile");
                intent.putExtra("CONTACT_PERSON_NM", CONTACT_PERSON_NM);
                intent.putExtra("CONTACT_PERSON_PHN", CONTACT_PERSON_PHN);
                intent.putExtra("CONTACT_PERSON_EML", CONTACT_PERSON_EML);
                intent.putExtra("FLOORS", FLOORS);
                intent.putExtra("ADDRESS", ADDRESS);
                intent.putExtra("AVAILABLE_FROM",AVAILABLE_FROM);
                intent.putExtra("BATHS", BATHS);
                intent.putExtra("BEDS", BEDS);
                intent.putExtra("LOGLITUTDE", LOGLITUTDE);
                intent.putExtra("LATTITUDE", LATTITUDE);
                intent.putExtra("PRICE", PRICE);
                intent.putExtra("TOLET_DETAILS", TOLET_DETAILS);
                intent.putExtra("TOLET_TYPE_ID",TOLET_TYPE_ID);
                intent.putExtra("TOLET_NAME", TOLET_NAME);
                startActivity(intent);
            }
        });
        return v;
    }

}
