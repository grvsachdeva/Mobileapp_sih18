package com.example.namankhanna.sihmobileapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class EmployeeAttendanceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private static final int RC_PHOTO_PICKER = 23;
    public static GoogleApiClient mGoogleApiClient;
    public static final String TAG = EmployeeAttendanceActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 21 ;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileproviderSIH";
    private String mTempPhotoPath = null;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mAttendancePics;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_attendance);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mAttendancePics = mFirebaseStorage.getReference().child("pics");
        getCurrentLocation();
    }

    public boolean checkPermission()
    {
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        else
            return true;
    }

    void getCurrentLocation()
    {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{

            if(checkPermission())
            {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG,"onComplete : found location");
                            Location currentLocation = (Location) task.getResult();
                            Log.d(TAG,currentLocation.getLatitude() + "");
                            Log.d(TAG,currentLocation.getLongitude()+ "");
                        }else
                        {
                            Log.d(TAG,"onComplete : current location is null");
                            Toast.makeText(EmployeeAttendanceActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG,"onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG,"onConnectionSuspended");
    }

    @SuppressLint("NewApi")
    public void setupPermission() {
        if ((ContextCompat.checkSelfPermission(EmployeeAttendanceActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            String[] permissionNeeded = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionNeeded, PERMISSION_REQUEST_CODE);
        } else {
            launchCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    launchCamera();
                }
                else {
                    Toast.makeText(EmployeeAttendanceActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        }
    }

    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;

            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file

                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                //resolvePermission(takePictureIntent,MainActivity.this,photoURI);
                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d(TAG, "Uri : " + photoURI);
                // Launch the camera activity
                startActivityForResult(takePictureIntent, RC_PHOTO_PICKER);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            Uri selectedImageUri;
            selectedImageUri = Uri.fromFile(new File(mTempPhotoPath));

            Log.v(TAG,"Uri result : " + selectedImageUri);

            StorageReference reference = mAttendancePics.child(selectedImageUri.getLastPathSegment());
            reference.putFile(selectedImageUri).addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.v(TAG,downloadUrl.toString());
                }
            });
//            if(mTempPhotoPath!=null)
//            {
//                (new File(mTempPhotoPath)).delete();
//                mTempPhotoPath = null;
//            }

        }
    }


    public void clickCamera(View view) {
        setupPermission();
    }
}
