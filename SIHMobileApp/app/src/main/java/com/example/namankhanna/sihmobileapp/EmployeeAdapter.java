package com.example.namankhanna.sihmobileapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    ArrayList<Employee> employeeArrayList;
    Context context;

    private OnEmployeeClickListener oecl;

    public void setOnEmployeeClickListener(OnEmployeeClickListener oecl) {
        this.oecl = oecl;
    }

    public EmployeeAdapter(ArrayList<Employee> employeeArrayList, Context context) {
        this.employeeArrayList = employeeArrayList;
        this.context = context;
    }

    public void updateEmployee(ArrayList<Employee> employeeArrayList) {
        this.employeeArrayList = employeeArrayList;
        notifyDataSetChanged();
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.list_employee,parent,false);
        return new EmployeeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        final Employee employee = employeeArrayList.get(position);
        holder.tvName.setText(employee.getName());
        holder.tvDepartment.setText(employee.getDepartment_name());
        holder.btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mayank will implement the notification feature
            }
        });
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oecl != null) {
                    oecl.getEmployeeId(employee.getUserId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeArrayList.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView tvName , tvDepartment;
        Button btnNotify;
        View rootView;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEmployeeName);
            tvDepartment = itemView.findViewById(R.id.tvEmployeeDepartment);
            btnNotify = itemView.findViewById(R.id.btnNotify);
            rootView = itemView;
        }
    }
}
