package com.example.ghostrunner.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ghostrunner.MainActivity;
import com.example.ghostrunner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class trailsComparation extends Fragment {

    private static final String TAG = "TRAILSCOMPARATION";
    GraphView graph;
    private MainActivity main;
    String userName;
    String trailName;
    private FirebaseFirestore db;
    List<String> names;
    List<String> ids;
    List<String> times;
    BarGraphSeries<DataPoint> series;
    BarGraphSeries<DataPoint> series1;
    String myTime;
    String name;
    int counter;
    int counter2;
    Date newD;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.trails_comparations, container, false);
        db = FirebaseFirestore.getInstance();
        graph = (GraphView) root.findViewById(R.id.graph);
        main =  ((MainActivity) this.requireActivity());
        userName = main.getUserName();
        trailName =  getArguments().getString("trailName");
        myTime = getArguments().getString("time");

        String dt = myTime;
        dt = dt.replaceAll("\\s+", "");

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            newD = format.parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double dateNew = newD.getTime() / 1000;
        names = new ArrayList<>();
        ids = new ArrayList<>();
        times = new ArrayList<>();
        series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, dateNew)
        });
        series.setColor(Color.rgb(100,0,0));
        series.setSpacing(50);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        series.setValuesOnTopSize(50);
        series.setTitle(userName + "(me)");
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.addSeries(series);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(dateNew + 60);


// activate vertical scrolling
        graph.getViewport().setScrollableY(true);



        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);


        db.collection("Trails")
                .whereEqualTo("parentTrail", trailName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {

                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                       if (!ids.contains(document.get("id").toString())){
                                                           ids.add(document.get("id").toString());
                                                       }
                                                       times.add(document.get("duration").toString());


                                                   }
                                               }
                                               counter = 1;
                                               counter2 = 0;
                                                   for (String x : ids) {

                                                       db.collection("Users")
                                                               .whereEqualTo("id", x)
                                                               .get()
                                                               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                       if (task.isSuccessful()) {
                                                                           for (QueryDocumentSnapshot document : task.getResult()) {
                                                                               name = document.get("userName").toString();
                                                                           }

                                                                       }

                                                                           Log.i(TAG,String.valueOf(counter));
                                                                           Date date;
                                                                           String dtStart = times.get(counter2);
                                                                           dtStart = dtStart.replaceAll("\\s+", "");

                                                                           SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
                                                                           format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                                           try {
                                                                               date = format.parse(dtStart);
                                                                               double newDate = date.getTime() / 1000;
                                                                               //Log.i(TAG, String.valueOf(newDate));
                                                                               series1 = new BarGraphSeries<>(new DataPoint[]{
                                                                                       new DataPoint(counter, newDate),
                                                                               });
                                                                               graph.getViewport().setMaxX(ids.size() + 1);
                                                                               series1.setSpacing(50);
                                                                               series1.setDrawValuesOnTop(true);
                                                                               series1.setColor(Color.rgb((int) counter * 255 / 4, (int) Math.abs(newDate * 255 / 6), 100));
                                                                               series1.setValuesOnTopColor(Color.rgb((int) counter * 255 / 4, (int) Math.abs(newDate * 255 / 6), 100));
                                                                               series1.setValuesOnTopSize(50);
                                                                               series1.setTitle(name);
                                                                               graph.addSeries(series1);


                                                                           } catch (ParseException e) {
                                                                               e.printStackTrace();
                                                                           }
                                                                           if(counter < ids.size() +1)
                                                                               counter++;
                                                                           if(counter2 < ids.size())
                                                                               counter2++;

                                                                   }
                                                               });

                                                   }


                                           }
                                       });


        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " s";
                }
            }
        });





        return root;
    }


}
