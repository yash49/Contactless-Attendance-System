package com.rte.contactless_attendance_admin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StatsFragment extends Fragment {
    BarChart chart;
    private DatabaseReference database;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Double> totalTime = new ArrayList<>();
    List<BarEntry> entries = new ArrayList<BarEntry>();
    IndexAxisValueFormatter formatter = new IndexAxisValueFormatter();
    XAxis xAxis;
    boolean set;
    double sum = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stats_fragment, parent, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chart = view.findViewById(R.id.barchart);
        database = FirebaseDatabase.getInstance().getReference();

        xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0f);
        //xAxis.setXOffset(200f);
        //xAxis.setCenterAxisLabels(true);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setDrawLabels(false);
        Description desc = new Description();
        desc.setText("Members vs. Total time spent (minutes)");
        desc.setYOffset(-28);
        chart.setDescription(desc);

        final int startColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark);
        final int shadowColor = Color.parseColor("#888888");

        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    final String id = snapshot1.getKey();
                    if(id.equals("GS2"))continue;
                    final HashMap<String,String> data = (HashMap<String, String>) snapshot1.getValue();

                    database.child("logs").child(id).orderByKey().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            double lastT = 0;
                            for(DataSnapshot logShot : snapshot.getChildren()){
                                String timeStamp = logShot.getKey();
                                String status = (String) logShot.getValue();
                                if(status.equals("1")) lastT = Double.parseDouble(timeStamp);
                                if(status.equals("0")){
                                    sum += (Double.parseDouble(timeStamp)-lastT);
                                }
                            }
                            if (!names.contains(data.get("name"))) {
                                names.add(data.get("name"));
                                totalTime.add(sum);
                                entries.add(new BarEntry(names.size()-1, (float) (sum/60)));
                                sum = 0;
                                final BarDataSet barDataSet = new BarDataSet(entries,"Members");
                                final BarData newData = new BarData(barDataSet);

                                barDataSet.setColor(startColor);
                                barDataSet.setBarShadowColor(shadowColor);
                                xAxis.setValueFormatter(new IndexAxisValueFormatter(names));

                                chart.clear();
                                chart.setData(newData);
                                chart.notifyDataSetChanged();
                                chart.invalidate();

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
        //chart.invalidate();

    }

}
