package com.example.namankhanna.sihmobileapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminAccountActivity extends AppCompatActivity {

    public Menu mMenu;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase database;
    ArrayList<Employee> employeeArrayList = new ArrayList<>();
    RecyclerView rvListEmployees;
    EmployeeAdapter employeeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account);

        rvListEmployees = findViewById(R.id.rvListEmployees);
        rvListEmployees.setLayoutManager(new LinearLayoutManager(this));
        employeeAdapter = new EmployeeAdapter(employeeArrayList,this);
        rvListEmployees.setAdapter(employeeAdapter);

        employeeAdapter.setOnEmployeeClickListener(new OnEmployeeClickListener() {
            @Override
            public void getEmployeeId(String uid) {
                Toast.makeText(AdminAccountActivity.this, "Id: "+uid, Toast.LENGTH_SHORT).show();
            }
        });


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference employeeRef = database.getReference().child("Employees");
        employeeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Employee employee = dataSnapshot.getValue(Employee.class);
                employeeArrayList.add(employee);
                employeeAdapter.updateEmployee(employeeArrayList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(AdminAccountActivity.this, MainActivity.class));
                    finish();
                }else
                {
                    Toast.makeText(AdminAccountActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.account_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout) {
            auth.signOut();
            Intent i = new Intent(AdminAccountActivity.this,MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mMenu.performIdentifierAction(R.id.action_logout,0);
        finish();
    }
}
