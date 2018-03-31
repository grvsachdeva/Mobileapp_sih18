package com.example.namankhanna.sihmobileapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EmployeeAttendanceActivity extends AppCompatActivity{

    private static final int RC_PHOTO_PICKER = 23;
    public static final String TAG = EmployeeAttendanceActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 21 ;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileproviderSIH";
    private String mTempPhotoPath = null;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mAttendancePics;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAttendanceReference;
    LocationManager mLocationManager;

    Location currentLocation;
    TextView tvAttendanceDate;
    TextView tvAttendanceTime;
    TextView tvAttendanceLocation;
    EditText etAttendanceRemarks;
    Attendance attendance;
    ImageView ivEmployeeImage;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_attendance);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        attendance = new Attendance();
        tvAttendanceDate = findViewById(R.id.tvAttendanceDate);
        tvAttendanceTime = findViewById(R.id.tvAttendanceTime);
        tvAttendanceLocation = findViewById(R.id.tvAttendanceLocation);
        ivEmployeeImage = findViewById(R.id.ivEmployeeImage);
        etAttendanceRemarks = findViewById(R.id.etAttendanceRemarks);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image");
        attendanceMarking();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mAttendancePics = mFirebaseStorage.getReference().child("pics");
        String user_uid = (getIntent()).getStringExtra(EmployeeAccountActivity.USER_UID);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAttendanceReference = mFirebaseDatabase.getReference().child("Employees").child(user_uid).child("attendance");
    }

    @SuppressLint("NewApi")
    public void setupLocationPermission() {
        if ((ContextCompat.checkSelfPermission(EmployeeAttendanceActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(EmployeeAttendanceActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            String[] permissionNeeded = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissionNeeded, PERMISSION_REQUEST_CODE + 1);
        } else {
            getCurrentLocation();
        }

    }

    private Location getLastBestLocation() {
        @SuppressLint("MissingPermission") Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        @SuppressLint("MissingPermission") Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    void getCurrentLocation()
    {
        Location location = getLastBestLocation();
        Log.v(TAG,location.toString());
        currentLocation = new Location(location);
        Geocoder geocoder = new Geocoder(EmployeeAttendanceActivity.this,Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            Log.v(TAG + "Hello",add);
            attendance.setLocation(add);
            tvAttendanceLocation.setText(add);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            case PERMISSION_REQUEST_CODE + 1 : {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getCurrentLocation();
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
                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d(TAG, "Uri : " + photoURI);
                // Launch the camera activity
                startActivityForResult(takePictureIntent, RC_PHOTO_PICKER);
            }
        }
    }

    public void attendanceMarking()
    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(c);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String time_in = timeFormat.format(c);
        String time_out = "";
        attendance.setDate(formattedDate);
        attendance.setTime_in(time_in);
        attendance.setTime_out(time_out);
        tvAttendanceTime.setText(time_in);
        tvAttendanceDate.setText(formattedDate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            dialog.show();
            Uri selectedImageUri;
            selectedImageUri = Uri.fromFile(new File(mTempPhotoPath));

            Log.v(TAG,"Uri result : " + selectedImageUri);

            StorageReference reference = mAttendancePics.child(selectedImageUri.getLastPathSegment());
            reference.putFile(selectedImageUri).addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    attendance.setImage(downloadUrl.toString());
                    dialog.dismiss();
                    Toast.makeText(EmployeeAttendanceActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    Glide.with(ivEmployeeImage.getContext()).load(downloadUrl).into(ivEmployeeImage);
                    Log.v(TAG,downloadUrl.toString());
                }
            });

            if(mTempPhotoPath!=null)
            {
                (new File(mTempPhotoPath)).delete();
                mTempPhotoPath = null;
            }

        }
    }


    public void clickCamera(View view) {
        setupPermission();
    }

    public void getLocation(View view) {
        setupLocationPermission();
    }

    public void viewLocation(View view) {
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();
        Uri geoLocation = Uri.parse("geo:" + latitude + "," + longitude);
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll= " + latitude + ", " +  longitude);

        Intent intent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    public void checkInAttendance(View view) {
        String remarks = etAttendanceRemarks.getText().toString();
        if(currentLocation == null)
        {
            Toast.makeText(this, "Please select the location", Toast.LENGTH_SHORT).show();
            return;
        }
        if(attendance.image.equals("") || attendance.image == null)
        {

            Toast.makeText(this, "Please click an image", Toast.LENGTH_SHORT).show();
            return;

        }
        attendance.setRemarks(remarks);


        mAttendanceReference.push().setValue(attendance).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EmployeeAttendanceActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EmployeeAttendanceActivity.this,EmployeeAccountActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
