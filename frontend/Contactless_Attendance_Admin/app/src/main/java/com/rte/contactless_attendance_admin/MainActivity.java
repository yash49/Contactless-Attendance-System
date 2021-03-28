package com.rte.contactless_attendance_admin;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.firebase.database.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
// TO-DO
    RecyclerView logsContainer;
    EmployeeLogsAdapter employeeLogsAdapter;
    private DatabaseReference database;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> idSet = new ArrayList<>();
    ArrayList<HashMap<String,Object>> logs = new ArrayList<>();
    BottomNavigationBar bottomNavigationBar;
    FragmentTransaction fragTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setActiveColor("#FFFFFF").
                setBarBackgroundColor(R.color.colorPrimaryDark);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.cam_24dp, "Live Logs"))
                .addItem(new BottomNavigationItem(R.drawable.group_24dp, "Members"))
                .addItem(new BottomNavigationItem(R.drawable.stats_24dp, "Stats"))
                .setFirstSelectedPosition(0)
                .initialise();
        fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.fragContainer, new LogsFragment());fragTrans.commit();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                if(position == 0){
                    fragTrans = getSupportFragmentManager().beginTransaction();
                    fragTrans.replace(R.id.fragContainer, new LogsFragment());fragTrans.commit();
                }
                else if(position == 1){
                    fragTrans = getSupportFragmentManager().beginTransaction();
                    fragTrans.replace(R.id.fragContainer, new MembersFragment());fragTrans.commit();
                }
                else{
                    fragTrans = getSupportFragmentManager().beginTransaction();
                    fragTrans.replace(R.id.fragContainer, new StatsFragment());fragTrans.commit();
                }
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });

        /*database = FirebaseDatabase.getInstance().getReference();

        logsContainer = findViewById(R.id.logs_container);


        employeeLogsAdapter = new EmployeeLogsAdapter(MainActivity.this,names,logs);
        logsContainer.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        logsContainer.setAdapter(employeeLogsAdapter);
        logsContainer.setHasFixedSize(true);
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("HERE","HERE");
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String id = snapshot1.getKey();
                    final String name = (String) snapshot1.getValue();

                    //============================= FOR LOGS================================================================
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

                                if(!name.equals("GS2")) {
                                    if (!logs.contains(logForUser)) logs.add(logForUser);
                                    if (!names.contains(name)) names.add(name);
                                    employeeLogsAdapter.notifyDataSetChanged();
                                }
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
*/


//=============================================================================================

    }
}
