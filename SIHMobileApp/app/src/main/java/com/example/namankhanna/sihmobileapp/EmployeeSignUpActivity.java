package com.example.namankhanna.sihmobileapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;


public class EmployeeSignUpActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 23;
    EditText etName , etDepartment , etPhoneNo , etEmail , etPassword;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    private String mTempPhotoPath = null;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mProfilePics;
    ImageView ivProfileImage;
    public static final int PERMISSION_REQUEST_CODE = 50;
    public static final String TAG = EmployeeSignUpActivity.class.getSimpleName();
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileproviderSIH";

    ProgressDialog uploadDialog;

    String downloadUriForImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_sign_up);

        etName = findViewById(R.id.etSignUpName);
        etDepartment = findViewById(R.id.etSignUpDepartment);
        etPhoneNo = findViewById(R.id.etSignUpPhoneNo);
        etEmail = findViewById(R.id.etSignUpEmail);
        etPassword = findViewById(R.id.etSignUpPassword);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePics = mFirebaseStorage.getReference().child("profile_pics");

        ivProfileImage = findViewById(R.id.ivProfileImage);

        dialog = new ProgressDialog(this);
        uploadDialog = new ProgressDialog(this);
        uploadDialog.setMessage("Uploading Image");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        (findViewById(R.id.btnRegister)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEmployee();
            }
        });
    }



    private void createEmployee() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            dialog.setMessage("Registering Employee");
            dialog.show();

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Intent i = new Intent(EmployeeSignUpActivity.this,EmployeeAccountActivity.class);
                        writeIntoDatabase();
                        startActivity(i);
                        finish();
                        dialog.dismiss();
                    }else
                    {
                        Toast.makeText(EmployeeSignUpActivity.this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        else {

            Toast.makeText(this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show();

        }
    }

    @SuppressLint("NewApi")
    public void setupPermission() {
        if ((ContextCompat.checkSelfPermission(EmployeeSignUpActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            String[] permissionNeeded = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionNeeded, PERMISSION_REQUEST_CODE);
        } else {
            launchCamera();
        }

    }

    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;

            try {
                photoFile = BitmapUtils.createTempImageFile(EmployeeSignUpActivity.this);
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Toast.makeText(EmployeeSignUpActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            uploadDialog.show();
            final Uri selectedImageUri;
            selectedImageUri = Uri.fromFile(new File(mTempPhotoPath));
            Glide.with(ivProfileImage.getContext()).load(selectedImageUri).into(ivProfileImage);
            Log.v(TAG,"Uri result : " + selectedImageUri);

            StorageReference reference = mProfilePics.child(selectedImageUri.getLastPathSegment());
            reference.putFile(selectedImageUri).addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    downloadUriForImage = downloadUrl.toString();
                    uploadDialog.dismiss();
                    Glide.with(ivProfileImage.getContext()).load(selectedImageUri).into(ivProfileImage);
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

    private void writeIntoDatabase() {

        Log.v("TAG","writeIntoDatabase");
        DatabaseReference employeeRef = database.getReference().child("Employees");
        DatabaseReference employeeId = employeeRef.child(auth.getCurrentUser().getUid());



        Employee employee = new Employee(
                etName.getText().toString(),
                etDepartment.getText().toString(),
                etPhoneNo.getText().toString(),
                auth.getCurrentUser().getUid(),
                true,
                System.currentTimeMillis(),
                "",
                downloadUriForImage
        );

        employeeId.setValue(employee);
    }

    public void clickProfilePicture(View view) {
        setupPermission();
    }
}
