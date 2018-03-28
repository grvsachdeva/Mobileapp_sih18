package com.example.namankhanna.sihmobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {

    EditText etEmail , etPassword;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    ProgressDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etEmail = findViewById(R.id.etAdminEmail);
        etPassword = findViewById(R.id.etAdminPassword);
        dialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null) {
                    Intent i = new Intent(AdminLoginActivity.this,AdminAccountActivity.class);
                    startActivity(i);
                }
            }
        };

        int logoutFlag = getIntent().getIntExtra("LOGOUT_FLAG",-1);
        if(logoutFlag != -1) {
            auth.signOut();
        }

        (findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInAdmin();
            }
        });
    }

    private void signInAdmin() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            dialog.setMessage("Signing In");
            dialog.show();

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        Toast.makeText(AdminLoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });

        }
        else {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
