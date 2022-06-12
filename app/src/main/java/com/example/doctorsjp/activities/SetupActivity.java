package com.example.doctorsjp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doctorsjp.R;
import com.example.doctorsjp.fragments.MapsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = SetupActivity.class.getSimpleName();

    private static final int CAMERA_CODE = 100;
    private static final int GALLERY_CODE = 200;

    EditText name , contact;
    TextView email;
    CircleImageView setup_imageview;
    FrameLayout edit_image;
    BottomSheetBehavior bottomSheetBehavior;
    RelativeLayout bottom_sheet_root;
    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    CoordinatorLayout root_layout;
    LinearLayout camera , gallery;
    File photofile;
    Uri final_file_uri;
    String currentpicturepath;
    Button save;
    ImageView button_change_address;
    final ArrayList<String> searchparams = new ArrayList<>();
    String[]permissions = {Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.CAMERA};
    FusedLocationProviderClient fusedLocationProviderClient;
    String filename , date;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ProgressDialog progressDialog;
    StorageReference storageReference;
    UploadTask uploadTask;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
        editor = sharedPreferences.edit();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SetupActivity.this);
        progressDialog = new ProgressDialog(this);


        root_layout = (CoordinatorLayout) findViewById(R.id.root_layout);

        name = (EditText) findViewById(R.id.name);
        contact = (EditText) findViewById(R.id.contact);
        email = (TextView) findViewById(R.id.email);

        save = (Button) findViewById(R.id.save);

        if (sharedPreferences.getBoolean("isPhoneLogin" , false)) {

            contact.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        } else if (sharedPreferences.getBoolean("isEmailLogin" , false)) {

            email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this , Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestPermissions(permissions, 100);

            } else {

                ActivityCompat.requestPermissions(SetupActivity.this, permissions, 100);
            }

            email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveToDatabase();
            }
        });

        setup_imageview = (CircleImageView) findViewById(R.id.setup_imageview);
        edit_image = (FrameLayout) findViewById(R.id.edit_image);

        bottom_sheet_root =  root_layout.findViewById(R.id.bottom_sheet_root);

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_root);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        camera = (LinearLayout) bottom_sheet_root.findViewById(R.id.camera);
        gallery = (LinearLayout) bottom_sheet_root.findViewById(R.id.gallery);



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();


            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent , "Select Image") , GALLERY_CODE);

            }
        });

        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_CODE:
                    Uri selected_image_uri = data.getData();
                    String imagepath = getImagePath(SetupActivity.this , selected_image_uri);
                    File file = new File(imagepath);
                    Toast.makeText(this, "Selected Image Path:" + imagepath, Toast.LENGTH_SHORT).show();
                    Glide.with(SetupActivity.this)
                            .load(file)
                            .placeholder(R.drawable.ic_user)
                            .into(setup_imageview);

                    progressDialog.setMessage(getString(R.string.save_pic_dialogue));
                    progressDialog.show();

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selected_image_uri);

                        date = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());
                        filename = date + ".jpg";

                        storageReference = storage.getReference().child("ProfilePic/" + filename);
                        uploadTask = storageReference.putStream(inputStream);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(SetupActivity.this,getString(R.string.upload_dialogue) , Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                }

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                progressDialog.setProgress((int) progress);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(SetupActivity.this, getString(R.string.upload_failed_dialogue), Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();

                            }
                        });

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()) {

                                    progressDialog.cancel();
                                    throw task.getException();
                                }

                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                Uri uri = task.getResult();
                                progressDialog.cancel();
                                setPhotoPath(uri.toString());

                            }
                        });

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;

                case CAMERA_CODE:

                    Glide.with(SetupActivity.this)
                            .load(currentpicturepath)
                            .placeholder(R.drawable.ic_user)
                            .into(setup_imageview);

                    date = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());
                    filename = date + ".jpg";

                    progressDialog.show();

                    storageReference = storage.getReference().child("ProfilePic/" + filename);
                    uploadTask = storageReference.putFile(Uri.fromFile(new File(currentpicturepath)));
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(SetupActivity.this,getString(R.string.upload_dialogue) , Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            progressDialog.setProgress((int) progress);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(SetupActivity.this, getString(R.string.upload_failed_dialogue), Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();

                        }
                    });

                    Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                            if (!task.isSuccessful()) {

                                progressDialog.cancel();
                                throw task.getException();
                            }

                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            Uri uri = task.getResult();
                            progressDialog.cancel();
                            setPhotoPath(uri.toString());

                        }
                    });

                    break;

            }
        }

    }

    public String getImagePath(Context context , Uri uri) {

        String imagepath = null;
        String[] filepathcolumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri , filepathcolumn , null , null , null);
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                int columnindex = cursor.getColumnIndex(filepathcolumn[0]);
                imagepath = cursor.getString(columnindex);

            }
            cursor.close();
        }

        if (imagepath == null) {

            return "";

        } else {

            return imagepath;

        }

    }





    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );


        currentpicturepath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.doctorsjp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_CODE);
            }
        }
    }

    public void saveToDatabase() {

        Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();

        progressDialog.setMessage("Saving Details.Please wait..");
        progressDialog.show();

        if (currentpicturepath != null && !currentpicturepath.equals("")) {

            Map map = new HashMap();
            map.put("name" , name.getText().toString());
            map.put("contact" , contact.getText().toString());
            map.put("email" , email.getText().toString());
            map.put("profilepic" , currentpicturepath);
            map.put("searchparams" , searchparams);

            SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name" , name.getText().toString());
            editor.putString("contact" , contact.getText().toString());
            editor.putString("email" , email.getText().toString());
            editor.putString("profilepic" , currentpicturepath);


            editor.commit();



            FirebaseFirestore.getInstance().collection("Patients")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(SetupActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();

                            Intent intent = new Intent(SetupActivity.this , MapsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SetupActivity.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();

                }
            });


        } else {


            Map map = new HashMap();
            map.put("name" , name.getText().toString());
            map.put("contact" , contact.getText().toString());
            map.put("email" , email.getText().toString());
            map.put("profilepic" , "");
            map.put("searchparams" , searchparams);


            SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name" , name.getText().toString());
            editor.putString("contact" , contact.getText().toString());
            editor.putString("email" , email.getText().toString());
            editor.putString("profilepic" , "");


            editor.commit();



            FirebaseFirestore.getInstance().collection("Patients")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(SetupActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();

                            Intent intent = new Intent(SetupActivity.this , MapsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SetupActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();

                }
            });


        }

    }

    public void setPhotoPath(String photoPath) {

        this.currentpicturepath = photoPath;
    }
}