package srl.com.bd.srl;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        android.Manifest.permission.CALL_PHONE},1);
                return;
            }
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }  else if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            alertDialog1(this,"","Are Your sure exit!");
            //finish();
        }
        else {

            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //getSupportFragmentManager().beginTransaction().addToBackStack("home").replace(R.id.fragment_container,new HomeFragment()).commit();
        } else if (id == R.id.nav_gallery) {
           // getSupportFragmentManager().beginTransaction().addToBackStack("home").replace(R.id.fragment_container,new AboutUsFragment()).commit();
        }  else if (id == R.id.nav_share) {
            //getSupportFragmentManager().beginTransaction().addToBackStack("home").replace(R.id.fragment_container,new CommonSearchFragment()).commit();
        } else if (id == R.id.nav_share) {
            feedback();
        } else if (id == R.id.nav_slideshow) {
            /*Intent intent = new Intent(MainActivity.this,ContactUs.class);
            startActivity(intent);*/
        } else if (id == R.id.nav_tools) {
            /*getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment_container,
                    new LoginOption()).commit();*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void showAlert(Context context, String status, String msg){
        new AlertDialog.Builder(context).
                setCancelable(false).setTitle(status).setMessage(msg)
                .setPositiveButton("OK",null).show();
    }

    public static void showAlertTwo(Context context, String status, String msg){
        new AlertDialog.Builder(context).
                setCancelable(false).setTitle(status).setMessage(msg)
                .setPositiveButton("OK",null)
                .setNegativeButton("Exit",null).show();

    }

    public  void showAlertThree(Context context, String msg){
        new AlertDialog.Builder(context).
                setCancelable(false).setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

    }

    public void  alertDialog1(Context context, String title, String message) {new android.app.AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                }
            }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.finish();
                }
            }).show();
    }

    public void feedback(){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"nu.info@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "");
        email.putExtra(Intent.EXTRA_TEXT, "");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email Client :"));

    };
}
