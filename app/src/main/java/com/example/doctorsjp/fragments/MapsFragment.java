package com.example.doctorsjp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.example.doctorsjp.GetValues;
import com.example.doctorsjp.R;
import com.example.doctorsjp.activities.ChooseDateTimeActivity;
import com.example.doctorsjp.activities.MapsActivity;
import com.example.doctorsjp.adapters.ViewPagerAdapter;
import com.example.doctorsjp.services.SearchDocService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback , GetValues {

    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Context c;
    private static final String TAG = MapsFragment.class.getSimpleName();
    TextView full_address;
    GetValues getValues;
    static Double latitude;
    static Double longitude;
    static String fulladdress;
    Button search_docs;
    ProgressBar search_doc_progress;
    BroadcastReceiver broadcastReceiver;
    int MAX = 30000;
    BottomNavigationView bottomNavigationView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        return inflater.inflate(R.layout.fragment_maps, container, false);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(MapsFragment.this);
        }

        full_address = (TextView) view.findViewById(R.id.full_address);
        search_docs = (Button) view.findViewById(R.id.search_docs);
        search_docs.setBackgroundColor(getResources().getColor(R.color.grey));
        search_docs.setClickable(false);

        search_doc_progress = (ProgressBar) view.findViewById(R.id.search_doc_progress);
        search_doc_progress.setMax(MAX);

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals("com.example.CUSTOM_DOCS")) {

                    if (intent.getExtras().containsKey("progress")) {

                        Log.d(TAG, "onReceive: " + intent.getIntExtra("progress" , 0));

                        search_doc_progress.setProgress(intent.getIntExtra("progress" , 0));


                    } else {

                        search_doc_progress.setProgress(0);

                    }


                }

            }
        };



        search_docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewPagerDialogFragment viewPagerDialogFragment = new ViewPagerDialogFragment();
                viewPagerDialogFragment.show(getChildFragmentManager() , "ViewPagerDialogFragment");

            }
        });



    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude , longitude);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16));

                    if (Geocoder.isPresent()) {

                        Geocoder geocoder = new Geocoder(getActivity());


                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude , longitude , 5);
                            String fulladdress = addresses.get(0).getAddressLine(0);
                            full_address.setText(fulladdress);


                            final List<String> searchparams = new ArrayList<>();

                            String[] splitted = fulladdress.trim().split(", ");



                            for (String s : splitted) {

                                if (s.contains(addresses.get(0).getPostalCode())) {

                                    int index = s.indexOf(addresses.get(0).getPostalCode());
                                    int length = s.length();
                                    searchparams.add(s.substring(0 , index-1).toLowerCase());
                                    searchparams.add(s.substring(index , length-1));
                                    continue;
                                }

                                searchparams.add(s.toLowerCase());

                            }

                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("profiledetails" , MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("fulladdress" , fulladdress);
                            editor.commit();

                            Map map = new HashMap();
                            map.put("latitude" , latitude);
                            map.put("longitude" , longitude);
                            map.put("fulladdress" , fulladdress);
                            map.put("searchparams" , searchparams);

                            FirebaseFirestore.getInstance().collection("Patients")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(map , SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            search_docs.setClickable(true);
                                            search_docs.setBackgroundColor(getResources().getColor(R.color.ash_variant));

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG, "onFailure: " + e.getMessage());

                                }
                            });

                        } catch (IOException e) {
                            Log.d(TAG, "onSuccess: " + e.getMessage());
                        }


                    }


                }
            });


            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {

                    LatLng latLng = mMap.getCameraPosition().target;
                    latitude = Double.valueOf(String.format("%.6f" , latLng.latitude));
                    longitude = Double.valueOf(String.format("%.6f" , latLng.longitude));


                    Log.d(TAG, "Latitude: " + latitude + "\n" + "Longitude:" + longitude);


                   if (Geocoder.isPresent()) {

                       if (latitude > 1.0 && longitude > 1.0) {

                           Geocoder geocoder = new Geocoder(getActivity());

                           try {
                               List<Address> addresses = geocoder.getFromLocation(latitude , longitude , 5);
                               fulladdress = addresses.get(0).getAddressLine(0);
                               full_address.setText(fulladdress);


                           } catch (IOException e) {
                               Log.d(TAG, "onSuccess: " + e.getMessage());
                           }

                       }




                    }


                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        c.registerReceiver(broadcastReceiver , new IntentFilter("com.example.CUSTOM_DOCS"));


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        c = context;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        c.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void getlatlng(double latitude, double longitude) {

    }

    @Override
    public void sendDateTime(String date, String time, int convertedtime) {



    }
}