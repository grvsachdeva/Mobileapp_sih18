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

public class MainActivity extends AppCompatActivity {

    EditText etEmail , etPassword;
    FirebaseAuth auth;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmployeeEmail);
        etPassword = findViewById(R.id.etEmployeePassword);
        dialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        int logoutFlag = getIntent().getIntExtra("LOGOUT_FLAG",-1);
        if(logoutFlag != -1) {
            auth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        }


        (findViewById(R.id.btnEmployeeLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInEmployee();
            }
        });

        (findViewById(R.id.btnEmployeeSignUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,EmployeeSignUpActivity.class);
                startActivity(i);
            }
        });

        (findViewById(R.id.btnAdminLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,AdminLoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void signInEmployee() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        etEmail.setText("");
        etPassword.setText("");

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            dialog.setMessage("Signing In");
            dialog.show();

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent i = new Intent(MainActivity.this,EmployeeAccountActivity.class);
                        startActivity(i);
                        finish();
                    }
                    dialog.dismiss();
                }
            });

        }
        else {

            Toast.makeText(this, "Fields cannot be Empty", Toast.LENGTH_SHORT).show();
        }
    }

}
