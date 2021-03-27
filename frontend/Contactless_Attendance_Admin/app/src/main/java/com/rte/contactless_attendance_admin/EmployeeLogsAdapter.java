package com.rte.contactless_attendance_admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EmployeeLogsAdapter extends RecyclerView.Adapter<EmployeeLogsAdapter.ViewHolder> {
    ArrayList<String> names; ArrayList<HashMap<String,Object>> logData;
    Context context;
    EmployeeLogsAdapter(Context context, ArrayList<String> names, ArrayList<HashMap<String,Object>> logData){
        this.context = context;
        Log.e("ADAPTER",logData.size()+"");
        this.names = names;
        this.logData = logData;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.log_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.employeeName.setText(names.get(position));
        if(position < logData.size()){
            HashMap<String,Object> d = logData.get(position);
            LogsItemAdapter logAdapter = new LogsItemAdapter(context,d, holder.status);
            holder.logView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,true));
            holder.logView.setAdapter(logAdapter);
        }


    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView logView;
        TextView employeeName,status;
        public ViewHolder(View itemView) {
            super(itemView);
            this.logView = itemView.findViewById(R.id.employee_log_view);
            this.employeeName = itemView.findViewById(R.id.employee_name);
            this.status = itemView.findViewById(R.id.employee_status);
        }
    }
}
