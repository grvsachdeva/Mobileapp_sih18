package com.example.namankhanna.sihmobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmployeeSignUpActivity extends AppCompatActivity {

    EditText etName , etDepartment , etPhoneNo , etEmail , etPassword;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_sign_up);

        etName = findViewById(R.id.etSignUpName);
        etDepartment = findViewById(R.id.etSignUpDepartment);
        etPhoneNo = findViewById(R.id.etSignUpPhoneNo);
        etEmail = findViewById(R.id.etSignUpEmail);
        etPassword = findViewById(R.id.etSignUpPassword);
        dialog = new ProgressDialog(this);

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

    private void writeIntoDatabase() {

        Log.v("TAG","writeIntoDatabase");
        DatabaseReference employeeRef = database.getReference().child("Employees");
        DatabaseReference employeeId = employeeRef.child(auth.getCurrentUser().getUid());

        Employee employee = new Employee(
                etName.getText().toString(),
                etDepartment.getText().toString(),
                etPhoneNo.getText().toString(),
                true,
                System.currentTimeMillis()
        );

        employeeId.setValue(employee);
    }
}
