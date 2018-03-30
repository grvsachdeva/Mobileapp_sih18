package com.example.namankhanna.sihmobileapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    ArrayList<Attendance> attendanceArrayList;
    Context context;

    public AttendanceAdapter(ArrayList<Attendance> attendanceArrayList, Context context) {
        this.attendanceArrayList = attendanceArrayList;
        this.context = context;
    }

    public void updateAttendance(ArrayList<Attendance> attendanceArrayList) {
        this.attendanceArrayList = attendanceArrayList;
        notifyDataSetChanged();
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.list_attendance,parent,false);
        return new AttendanceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {
        Attendance attendance = attendanceArrayList.get(position);
        holder.tvDate.setText(attendance.getDate());
        holder.tvCheckInTime.setText(attendance.getTime_in());
        holder.tvCheckOutTime.setText(attendance.getTime_out());
        holder.tvLocation.setText(attendance.getLocation());
        holder.tvRemarks.setText(attendance.getRemarks());
    }

    @Override
    public int getItemCount() {
        return attendanceArrayList.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate , tvCheckInTime , tvCheckOutTime , tvLocation , tvRemarks;

        public AttendanceViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCheckInTime = itemView.findViewById(R.id.tvCheckInTime);
            tvCheckOutTime = itemView.findViewById(R.id.tvCheckOutTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
        }
    }
}
