package bd.com.arda.findyourhome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.arda.findyourhome.model.GetUri;

public class TakeImageTolet extends AppCompatActivity {
    ProgressDialog pd;

    ImageView ivProfilePicture;
    ImageButton camera, folder;
    File temp;
    private int SELECT_PICTURE = 1;
    final int CAMERA_DATA = 0;
    int flag = 0;
    GetUri getUri;
    Dialog openDialog;
    String encodedImage;
    FloatingActionButton fab_post_tolet;
    String str_tolet_name="", str_tolet_details="", str_address="", str_price="", str_baths="",
            str_beds="", str_floors="", str_avilable_date="", str_contact_person_name="", str_contact_phone_number="",
            str_contact_person_email="", str_latitude="", str_longtitude="", str_tolet_type_id="",USER_ACC_ID="";
    String URL_POST_TOLET = "http://103.91.54.60/apps/FYH/FYH_USER_POST_TOLET.php";

    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_image_tolet);

        sharedpreferences = getSharedPreferences("FYH_UserData", Context.MODE_PRIVATE);
        USER_ACC_ID= sharedpreferences.getString("USER_ACC_ID","0");
        fab_post_tolet=findViewById(R.id.fab_post_tolet);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            str_tolet_name=bundle.getString("str_tolet_name");
            str_address=bundle.getString("str_address");
            str_tolet_type_id=bundle.getString("str_tolet_type_id");
            str_address=bundle.getString("str_address");
            str_tolet_details=bundle.getString("str_tolet_details");
            str_latitude=bundle.getString("str_latitude");
            str_longtitude=bundle.getString("str_longtitude");
            str_price=bundle.getString("str_price");
            str_baths=bundle.getString("str_baths");
            str_beds=bundle.getString("str_beds");
            str_floors=bundle.getString("str_floors");
            str_avilable_date=bundle.getString("str_avilable_date");
            str_contact_person_name=bundle.getString("str_contact_person_name");
            str_contact_phone_number=bundle.getString("str_contact_phone_number");
            str_contact_person_email=bundle.getString("str_contact_person_email");
        }
        getUri = new GetUri();
        ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);


        fab_post_tolet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivProfilePicture.getDrawable() == null) {
                    flag = 0;
                    alertDialog(TakeImageTolet.this, "Error", "Please Capture Profile Picture");
                } else {
                    flag = 1;
                }
                if (flag == 1) {
                    try {
                        Bitmap bitmap = null;
                        bitmap = ((BitmapDrawable) ivProfilePicture.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] arr = baos.toByteArray();
                        encodedImage = Base64.encodeToString(arr, Base64.DEFAULT);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    if (isInternetAvailable()) {
                        postTolet();
                        pd = ProgressDialog.show(TakeImageTolet.this, "Posting New Tolet", "Please wait few Second...");

                    } else {
                        alertDialog(TakeImageTolet.this, "Error", "Please Check Your Internet Connection");
                    }
                }

            }
        });

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog = new Dialog(TakeImageTolet.this);
                openDialog.setContentView(R.layout.custom_dialog_reg_image);
                camera = (ImageButton) openDialog.findViewById(R.id.camera);
                folder = (ImageButton) openDialog.findViewById(R.id.folder);
                openDialog.setTitle("Choose an Option");

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File f = createImageFile();
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri mMakePhotoUri = Uri.fromFile(f);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, mMakePhotoUri);
                        startActivityForResult(i, CAMERA_DATA);
                        openDialog.dismiss();
                    }
                });

                folder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_PICTURE);
                        openDialog.dismiss();
                    }
                });
                openDialog.show();
            }
        });

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(TakeImageTolet.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TakeImageTolet.this, new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

    }


    private  void postTolet(){
        StringRequest strRequest = new StringRequest(Request.Method.POST, URL_POST_TOLET,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        parcePostTolet(response);
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("P_USER_ACC_ID",USER_ACC_ID);
                params.put("P_TOLET_NAME",str_tolet_name);
                params.put("P_TOLET_DETAILS",str_tolet_details);
                params.put("P_ADDRESS",str_address);
                params.put("P_LATTITUDE",str_latitude);
                params.put("P_LOGLITUTDE",str_longtitude);
                params.put("P_PRICE",str_price);
                params.put("P_BATHS",str_baths);
                params.put("P_BEDS",str_beds);
                params.put("P_FLOORS",str_floors);
                params.put("P_AVAILABLE_FROM",str_avilable_date);
                params.put("P_CONTACT_PERSON_NM",str_contact_person_name);
                params.put("P_CONTACT_PERSON_PHN",str_contact_phone_number);
                params.put("P_CONTACT_PERSON_EML",str_contact_person_email);
                params.put("P_TOLET_TYPE_ID",str_tolet_type_id);
                params.put("P_CREATE_DATA",str_contact_person_name);
                params.put("P_PRODUCT_IMAGE",encodedImage);
                params.put("P_CREATE_BY",str_avilable_date);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(strRequest);
    }

    private void parcePostTolet(String response) {
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(response);
            String resposnce=jsonObject.getString("responce");
            JSONObject jsonObject1=new JSONObject(resposnce);
            String status_code=jsonObject1.getString("status_code");
            String msg=jsonObject1.getString("msg");
            String values=jsonObject1.getString("values");
            if (status_code.equals("200")){
                if (pd != null) {
                    pd.dismiss();
                }
                alertDialog(TakeImageTolet.this,msg,values);
            }else{
                if (pd != null) {
                    pd.dismiss();
                }
                alertDialog(TakeImageTolet.this,msg,values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (pd != null) {
                pd.dismiss();
            }
        }

    }




    @SuppressWarnings("unused")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK && requestCode == CAMERA_DATA) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            Matrix mat = new Matrix();
            mat.postRotate((270));
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            if (temp != null) {
                Bitmap bmp = decodeFile(temp, 100, 50);
                // -------------------encode into string--------------------
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    int i = baos.toByteArray().length;
                    encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
                    // -------------------encode into string--------------------
                    ivProfilePicture.setImageBitmap(bmp);
                } catch (Exception e) {
                    Toast.makeText(TakeImageTolet.this,
                            e.toString(),
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(TakeImageTolet.this,
                        "Image can't be retrieve, Please try again",
                        Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri dataUri = data.getData();
            Bitmap bitmap;
            try {

                bitmap = decodeUri(dataUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] b = baos.toByteArray();
                int i = baos.toByteArray().length;
                encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
                ivProfilePicture.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o);
        final int REQUIRED_SIZE = 140;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o2);
    }

    private Bitmap decodeFile(File f, int imageSize, int imagehgt) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            final int REQUIRED_SIZE = imageSize;
            final int REQUIRED_SIZE2 = imagehgt;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE2)
                scale *= 2;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private File createImageFile() {
        // TODO Auto-generated method stub
        temp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("DDmmyyyy_hhmmss");
        String currentDateandTime = sdf.format(new Date());
        String path = Environment.getExternalStorageDirectory()
                + "/CameraApps/images/";
        File tem = new File(path);
        if (tem.exists()) {
            tem.mkdir();
        } else
            tem.mkdirs();
        temp = new File(path, currentDateandTime + ".jpg");
        getUri.setUri(temp);
        return temp;
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

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) TakeImageTolet.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
