package com.example.doctorsjp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doctorsjp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoctMapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMyLocationButtonClickListener
, View.OnClickListener {

    private static final String TAG = DoctMapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    TextView docts_full_address;
    Intent intent;

    Double latitude , longitude;

    String id;
    String name;
    String fulladdress;
    String profileimage;
    String date;
    String time;
    String status;
    String timestamp;
    int convertedtime;

    CircleImageView doct_maps_profile_image;
    TextView doct_maps_name , doct_maps_full_address , doct_maps_accept , doct_maps_decline , doct_maps_date , doct_maps_time;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doct_maps);

        intent = getIntent();

        id = intent.getStringExtra("id");
        latitude = intent.getDoubleExtra("latitude" , 0.0);
        longitude = intent.getDoubleExtra("longitude" , 0.0);
        name = intent.getStringExtra("name");
        fulladdress = intent.getStringExtra("fulladdress");
        profileimage = intent.getStringExtra("profileimage");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        convertedtime = intent.getIntExtra("convertedtime" , 0);
        timestamp = intent.getStringExtra("timestamp");
        status = intent.getStringExtra("status");
        name = intent.getStringExtra("name");

        doct_maps_profile_image = (CircleImageView) findViewById(R.id.doct_map_profile_image);
        doct_maps_name = (TextView) findViewById(R.id.doct_map_name);
        doct_maps_full_address = (TextView) findViewById(R.id.doct_map_full_address);
        doct_maps_decline = (Button) findViewById(R.id.doct_map_decline);
        doct_maps_date = (TextView) findViewById(R.id.doct_map_date);
        doct_maps_time = (TextView) findViewById(R.id.doct_map_time);


        String timest = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            timest = new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        doct_maps_time.setText(timest);

        doct_maps_name.setText(name);
        doct_maps_full_address.setText(fulladdress);
        doct_maps_date.setText(date);

        Glide.with(DoctMapsActivity.this)
                .load(profileimage)
                .placeholder(R.drawable.ic_user)
                .into(doct_maps_profile_image);

        doct_maps_decline.setOnClickListener(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.doct_map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.doct_map_decline:

                FirebaseFirestore.getInstance().collection("Patients")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Doctors")
                        .document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDelDoc: "+  e.getMessage());

                    }
                });

                FirebaseFirestore.getInstance().collection("Doctors")
                        .document(id)
                        .collection("Patients")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDelPat: " + e.getMessage());

                    }
                });

                break;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);


        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16));
    }

    @Override
    public boolean onMyLocationButtonClick() {

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16));

        return true;
    }
}