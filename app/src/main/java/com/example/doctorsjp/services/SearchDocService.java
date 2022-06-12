package com.example.doctorsjp.services;

import static com.example.doctorsjp.DoctorsJP.CHANNEL_ID;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.doctorsjp.R;
import com.example.doctorsjp.activities.MapsActivity;
import com.example.doctorsjp.fragments.MapsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SearchDocService extends Service {

    FusedLocationProviderClient fusedLocationProviderClient;

    private int MAX_SECS = 30000;

    public static final String TAG = SearchDocService.class.getSimpleName();
    CountDownTimer countDownTimer;
    DocumentSnapshot documentSnapshot;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        double latitude = intent.getDoubleExtra("latitude" , 0);
        double longitude = intent.getDoubleExtra("longitude" , 0);
        final String date = intent.getStringExtra("date");
        final String time = intent.getStringExtra("time");
        final int convertedtime = intent.getIntExtra("convertedtime" , 0);
        final String fulladdress = intent.getStringExtra("fulladdress");

        Log.d(TAG, "onStartCommand: " + latitude + "\n" + longitude);

     countDownTimer = new CountDownTimer(MAX_SECS , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                int remaining =(int) (MAX_SECS - millisUntilFinished);
                Intent intent1 = new Intent("com.example.CUSTOM_DOCS");
                intent1.putExtra("progress" , remaining);
                sendBroadcast(intent1);

            }

            @Override
            public void onFinish() {

                stopTimer();

                if (documentSnapshot!=null) {

                    FirebaseFirestore.getInstance().collection("Doctors")
                            .document(documentSnapshot.getId())
                            .collection("Patients")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    
                }

            }
        };

        countDownTimer.start();

        if (Geocoder.isPresent()) {

            Geocoder geocoder = new Geocoder(SearchDocService.this);
            String[] addresslist = new String[10];
            List<String> final_list = new ArrayList<>();

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude , longitude , 1);
                if (addresses.size() > 0) {


                    String s = addresses.get(0).getAddressLine(0);
                    String[] splitted = s.split(", ");


                    for (int i = 0 ; i<splitted.length ; i++) {

                        if(i == 10) {

                            break;
                        }

                        if (splitted[i].contains(addresses.get(0).getPostalCode())) {

                            int index = splitted[i].indexOf(addresses.get(0).getPostalCode());
                            int length = splitted[i].length();

                            addresslist[i] = splitted[i].substring(0 , index-1).toLowerCase();
                            addresslist[i+1] = splitted[i].substring(index , length-1);
                            i+=2;


                            continue;

                        }

                        addresslist[i] = splitted[i];

                    }

                    for(String j : addresslist) {

                       final_list.add(j);
                    }
                    Toast.makeText(this, "Size:" + addresslist.length, Toast.LENGTH_SHORT).show();

                    FirebaseFirestore.getInstance().collection("Doctors")
                            .whereEqualTo("isenabled" , true)
                            .whereArrayContainsAny("searchparams" , final_list)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    if (!queryDocumentSnapshots.isEmpty()) {

                                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                                        int nos = documentSnapshots.size();
                                        if (nos ==1) {

                                            documentSnapshot = documentSnapshots.get(0);


                                        } else {

                                            Random random = new Random();
                                            int n = random.nextInt(nos);
                                            documentSnapshot = documentSnapshots.get(n);

                                        }

                                        SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        String currentdate = new SimpleDateFormat("dd/mm/yyyy").format(new Date());


                                        Map map = new HashMap();
                                        map.put("id" , FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        map.put("name" , sharedPreferences.getString("name" , ""));
                                        map.put("email" , sharedPreferences.getString("email" , ""));
                                        map.put("contact" , sharedPreferences.getString("contact" , ""));
                                        map.put("fulladdress" , fulladdress);
                                        map.put("date" , date);
                                        map.put("time" , time);
                                        map.put("convertedtime" , convertedtime);
                                        map.put("profilepic" , sharedPreferences.getString("profilepic" , ""));
                                        map.put("latitude" , latitude);
                                        map.put("longitude" , longitude);
                                        map.put("timestamp" , String.valueOf(System.currentTimeMillis()));
                                        map.put("status" , "pending");

                                        FirebaseFirestore.getInstance().collection("Doctors")
                                                .document(documentSnapshot.getId())
                                                .collection("Patients")
                                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .set(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Log.d(TAG, "onFailureDocsCall" + e.getMessage());

                                            }
                                        });

                                        FirebaseFirestore.getInstance().collection("Patients")
                                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .collection("Doctors")
                                                .document(documentSnapshot.getId())
                                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                                        if (error != null) {

                                                            return;
                                                        }

                                                        if (value.exists()) {

                                                            if (value.getString("status").equalsIgnoreCase("accepted")) {

                                                                stopTimer();

                                                                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                                MediaPlayer mediaPlayer = MediaPlayer.create(SearchDocService.this , uri);
                                                                mediaPlayer.start();

                                                                Notification notification = new NotificationCompat.Builder(SearchDocService.this , CHANNEL_ID)
                                                                        .setContentTitle(documentSnapshot.getString("name") + " is your Doctor")
                                                                        .setContentText("Tap to view")
                                                                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                                                                        .setAutoCancel(false)
                                                                        .build();


                                                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                notificationManager.notify(5 , notification);


                                                            } else if (value.getString("status").equalsIgnoreCase("completed")) {

                                                                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                                MediaPlayer mediaPlayer = MediaPlayer.create(SearchDocService.this , uri);
                                                                mediaPlayer.start();

                                                                Notification notification = new NotificationCompat.Builder(SearchDocService.this , CHANNEL_ID)
                                                                        .setContentTitle(getString(R.string.checkup_complete_dialogue))
                                                                        .setContentText("Tap to view")
                                                                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                                                                        .setAutoCancel(false)
                                                                        .build();


                                                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                notificationManager.notify(6 , notification);

                                                                value.getReference().delete();
                                                                stopSelf();


                                                            }


                                                        }


                                                    }
                                                });

                                    }







                                }
                            });



                } else {

                    Toast.makeText(this, "No Doctors found.Search again", Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();

                    Intent intent1 = new Intent("com.example.CUSTOM_DOCS");
                    intent1.putExtra("stop" , "stop");
                    sendBroadcast(intent1);

                    stopSelf();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
        return START_NOT_STICKY;
    }

    public void stopTimer() {

        countDownTimer.cancel();

        Intent intent1 = new Intent("com.example.CUSTOM_DOCS");
        intent1.putExtra("stop" , "stop");
        sendBroadcast(intent1);

        //stopSelf();


    }









}



