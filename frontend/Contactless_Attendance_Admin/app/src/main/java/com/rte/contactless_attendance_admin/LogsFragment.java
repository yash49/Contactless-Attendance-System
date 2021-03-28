package com.rte.contactless_attendance_admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class LogsFragment extends Fragment {
    RecyclerView logsContainer;
    EmployeeLogsAdapter employeeLogsAdapter;
    private DatabaseReference database;
    ArrayList<String> names = new ArrayList<>();
    Button logout;
    ArrayList<HashMap<String,Object>> logs = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.logs_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference();

        logsContainer = view.findViewById(R.id.logs_container);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),HomeActivity.class));
                getActivity().finish();

            }
        });
        
        employeeLogsAdapter = new EmployeeLogsAdapter(getActivity(),names,logs);
        logsContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
        logsContainer.setAdapter(employeeLogsAdapter);
        logsContainer.setHasFixedSize(true);

        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String id = snapshot1.getKey();
                    if(id.equals("GS2"))continue;
                    final HashMap<String,String> data = (HashMap<String, String>) snapshot1.getValue();
                    Log.e("MAP:",id);

                    final HashMap<String,Object> logForUser = new HashMap<>();
                    database.child("logs").child(id).orderByKey().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot logShot : snapshot.getChildren()){
                                String timeStamp = logShot.getKey();
                                String status = (String) logShot.getValue();
                                Log.e("TIMESTAMP:","=>"+timeStamp+"="+new Date(new Timestamp(Long.parseLong(timeStamp)).getTime()).toString());
                                logForUser.put(timeStamp,status);
                            }

                                if (!logs.contains(logForUser)) logs.add(logForUser);
                                if (!names.contains(data.get("name"))) names.add(data.get("name"));
                                employeeLogsAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
