package com.example.namankhanna.sihmobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalInfoActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseAuth auth;
    ImageView ivProfileImage;
    TextView tvName , tvDeparment , tvPhoneNo , tvJobStatus , tvTotalAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        ivProfileImage = findViewById(R.id.ivEmployeeInfoImage);
        tvName = findViewById(R.id.tvEmployeeInfoName);
        tvDeparment = findViewById(R.id.tvEmployeeInfoDepartment);
        tvPhoneNo = findViewById(R.id.tvEmployeeInfoPhoneNo);
        tvJobStatus = findViewById(R.id.tvEmployeeInfoJobStatus);
        tvTotalAttendance = findViewById(R.id.tvEmployeeInfoTotal);

        DatabaseReference attendanceRef = database.getReference().child("Employees").child(auth.getCurrentUser().getUid()).child("attendance");
        attendanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setTotalAttendance(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference employeeRef = database.getReference().child("Employees").child(auth.getCurrentUser().getUid());
        employeeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Employee employee = dataSnapshot.getValue(Employee.class);
                setTheViews(employee);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setTotalAttendance(long childrenCount) {
        tvTotalAttendance.setText(String.valueOf(childrenCount));
    }

    private void setTheViews(Employee myEmployee) {
        Glide.with(this).load(myEmployee.getPhotoUri()).centerCrop().into(ivProfileImage);
        tvName.setText(myEmployee.getName());
        tvDeparment.setText(myEmployee.getDepartment_name());
        tvPhoneNo.setText(myEmployee.getPhone_no());
        if(myEmployee.getActive()) {
            tvJobStatus.setText("Active");
        }
        else {
            tvJobStatus.setText("Inactive");
        }
    }
}
