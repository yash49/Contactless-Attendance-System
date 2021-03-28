package com.rte.contactless_attendance_admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogsItemAdapter extends RecyclerView.Adapter<LogsItemAdapter.ViewHolder> {

    Context context;
    HashMap<String,Object> logData;
    ArrayList<Long> timestamps = new ArrayList<>();
    TextView currentPresent;
    LogsItemAdapter(Context context, HashMap<String,Object> logData,TextView currentPresent){
        this.context = context;
        this.logData = logData;
        this.currentPresent = currentPresent;

        for(Object time: logData.keySet()){
            timestamps.add(Long.parseLong(time.toString()));
        }
        Collections.sort(timestamps, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return (int) (o2-o1);
            }
        });
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.log_details_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        currentPresent.setText(logData.get(timestamps.get(0)+"").toString().equals("0")?"ABSENT":"PRESENT");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(logData.get(timestamps.get(position)+"").toString().equals("0")){
            holder.status.setText("CHECK OUT");
            holder.status.setBackgroundResource(R.drawable.round_card_checkout);
            holder.logIc.setImageDrawable(context.getResources().getDrawable(R.drawable.exit_ic));
        }
        else{
            holder.status.setText("CHECK IN");
            holder.status.setBackgroundResource(R.drawable.round_card_checkin);
            holder.logIc.setImageDrawable(context.getResources().getDrawable(R.drawable.entry_ic));
        }
        Timestamp stamp = new Timestamp(timestamps.get(position)*1000);
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        holder.time.setText(gmtDateFormat.format(new Date(stamp.getTime())).toString());
    }

    @Override
    public int getItemCount() {
        return logData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time,status;
        ImageView logIc;
        public ViewHolder(View itemView) {
            super(itemView);
            this.time = itemView.findViewById(R.id.log_time);
            this.status = itemView.findViewById(R.id.log_type);
            this.logIc = itemView.findViewById(R.id.log_type_ic);
        }
    }
}
