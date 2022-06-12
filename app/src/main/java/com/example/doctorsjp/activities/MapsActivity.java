package com.example.doctorsjp.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.doctorsjp.R;
import com.example.doctorsjp.fragments.CompleteFragment;
import com.example.doctorsjp.fragments.DoctsFragment;
import com.example.doctorsjp.fragments.MapsFragment;
import com.example.doctorsjp.fragments.ProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private BottomNavigationView bottomNavigationView;
    private static final String TAG = MapsActivity.class.getSimpleName();
    FusedLocationProviderClient fusedLocationProviderClient;
    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);

        bundle = getIntent().getExtras();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {


                    case R.id.home:

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, new MapsFragment())
                                .commit();

                        break;

                    case R.id.docts:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame , new DoctsFragment())
                                .commit();

                        break;

                    case R.id.payments:

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame , new CompleteFragment())
                                .commit();

                        break;

                    case R.id.profile:

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, new ProfileFragment())
                                .commit();
                        break;

                }
                return true;
            }
        });



        if (bundle != null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.frame , new DoctsFragment()).commit();


        } else {

            getSupportFragmentManager().beginTransaction().replace(R.id.frame , new MapsFragment()).commit();

        }




    }


    @Override
    protected void onStart() {
        super.onStart();

            checkGPS();

    }

    private void checkGPS() {

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            showDialog();


        }
    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setCancelable(false);
        builder.setMessage("App requires Location Services to be enabled.Do you want to enable it?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}