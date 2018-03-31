package com.example.namankhanna.sihmobileapp;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.Key;
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
    Attendance attendanceToChange=null;
    String keyTochange=null;
    public static final String TAG = EmployeeAccountActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_account);

        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"The token is : " + token);

        (new FaceRecognition.DownloadAsync()).execute();
        attendanceArrayList = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEmployeeInfoReference = mFirebaseDatabase.getReference().child("Employees").child(mCurrentUser.getUid());
        mEmployeeInfoReference.child("fcm_token").setValue(token);
        mAttendanceReference = mEmployeeInfoReference.child("attendance");

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
                    //Toast.makeText(EmployeeAccountActivity.this, "Welcome to your account", Toast.LENGTH_SHORT).show();
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
                    //Log.d(TAG, "onChildAdded:"+attendance.toString());
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
        attachChildEventListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    public void showHistory(View view) {
        Intent i = new Intent(EmployeeAccountActivity.this,HistoryActivity.class);
        startActivity(i);
    }

    public void showPersonalInfo(View view) {
        Toast.makeText(this, "Function yet to be implemented", Toast.LENGTH_SHORT).show();
    }

    public void checkIn(View view) {
        markAttendance();
    }

    public void checkOut(View view) {
        final DatabaseReference attendanceRef = mFirebaseDatabase.getReference().child("Employees").child(auth.getCurrentUser().getUid()).child("attendance");
        attendanceRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                Attendance attendance = dataSnapshot.getValue(Attendance.class);
                if(attendance.getTime_out().equals("")) {
                    Calendar currentTime = Calendar.getInstance();
                    int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int minutes = currentTime.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            EmployeeAccountActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            String timeOut = i+":"+i1;
                            attendanceRef.child(dataSnapshot.getKey()).child("time_out").setValue(timeOut);
                        }
                    },hour,minutes,true);
                    timePickerDialog.setTitle("Select Time");
                    timePickerDialog.show();
                }
                else {
                    Toast.makeText(EmployeeAccountActivity.this, "Check In Necessary", Toast.LENGTH_SHORT).show();
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
        });

    }
}
