package com.rte.contactless_attendance_admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {
    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
    Context context;
    public MembersAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        super();
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.member_item, parent, false);
        MemberViewHolder viewHolder = new MemberViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        HashMap<String,String> item = data.get(position);
        holder.email.setText(item.get("email"));
        holder.name.setText(item.get("name"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView email;
        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.member_name);
            email = itemView.findViewById(R.id.member_email);
        }
    }
}
