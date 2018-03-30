package com.example.namankhanna.sihmobileapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EmployeeAccountActivity extends AppCompatActivity {

    public Menu mMenu;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;

    public static final String USER_UID = "Current_User_ID";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEmployeeInfoReference;
    private DatabaseReference mAttendanceReference;
    private FirebaseUser mCurrentUser;
    public ValueEventListener mValueEventListener;
    public ChildEventListener mChildEventListener;
    public ArrayList<Attendance> attendanceArrayList;
    public static final String TAG = EmployeeAccountActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_account);

        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEmployeeInfoReference = mFirebaseDatabase.getReference().child("Employees").child(mCurrentUser.getUid());
        mAttendanceReference = mEmployeeInfoReference.child("attendance");
        attendanceArrayList = new ArrayList<>();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(EmployeeAccountActivity.this, MainActivity.class));
                    finish();
                }else
                {
                    attachValueEventListener();
                    attachChildEventListener();
                    Toast.makeText(EmployeeAccountActivity.this, "Welcome to your account", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    public void markAttendance()
    {
        String userUID = mCurrentUser.getUid();
        Intent i = new Intent(EmployeeAccountActivity.this,EmployeeAttendanceActivity.class);
        i.putExtra("Current_User_ID",userUID);
        startActivity(i);
    }

    public void attachChildEventListener()
    {
        if(mChildEventListener == null)
        {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Attendance attendance = dataSnapshot.getValue(Attendance.class);
                    if(attendance!=null)
                    {
                        attendanceArrayList.add(attendance);
                        Log.v(TAG,attendance.toString());
                    }else {
                        Log.v(TAG,"No Value");
                    }

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
            };
            mAttendanceReference.addChildEventListener(mChildEventListener);
        }
    }

    public void attachValueEventListener()
    {
        if(mValueEventListener==null)
        {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Employee employee = dataSnapshot.getValue(Employee.class);
                    Log.v(TAG,employee.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mEmployeeInfoReference.addValueEventListener(mValueEventListener);
        }
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
            Intent i = new Intent(EmployeeAccountActivity.this,MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mMenu.performIdentifierAction(R.id.action_logout,0);
        finish();
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

    public void startAttendance(View view) {
        markAttendance();
    }

}
