package com.example.ghostrunner.ui.home;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghostrunner.FriendsAdapter;
import com.example.ghostrunner.HorizontalRecyclerHomeAdapter;
import com.example.ghostrunner.HorizontalRecyclerViewAdapter;
import com.example.ghostrunner.MainActivity;
import com.example.ghostrunner.R;
import com.example.ghostrunner.models.Trail;
import com.example.ghostrunner.ui.MyTrailsRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOMEFRAGMENT";
    private FirebaseFirestore db;
    private List<Bitmap> trails;
    private MainActivity main;
    private String userId;
    private MyTrailsRecyclerAdapter trailsAdapter;
    private RecyclerView trailsRecycler;
    private LinearLayoutManager horizontalLayoutManager;
    private ArrayList<Trail> imageModelArrayList;
    private RecyclerView mHorizontalRecyclerView;
    private HorizontalRecyclerHomeAdapter horizontalAdapter;
    private String todayDate;
    private String distanceTotal;
    private String timeTotal;
    private String speedTotal;
    private int traildisplaycont;
    private SimpleDateFormat timeFormat;
    private TextView distance;
    private TextView duration;
    private TextView speed;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        imageModelArrayList = new ArrayList<>();
        timeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        distance = (TextView) root.findViewById(R.id.distance);
        duration = (TextView) root.findViewById(R.id.timetotal);
        speed = (TextView) root.findViewById(R.id.maxspeed);
        traildisplaycont = 0;
        distanceTotal = "0.0";
        timeTotal = "0:0:0:0";
        speedTotal = "0.0";
        db = FirebaseFirestore.getInstance();
        main =  ((MainActivity) this.requireActivity());
        userId = main.getUserId();
        trailsRecycler = (RecyclerView) root.findViewById(R.id.trailsView);
        horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trailsRecycler.setLayoutManager(horizontalLayoutManager);
        trails = new ArrayList<>();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        todayDate = df.format(c);

        //Return list of trails created by user
        db.collection("Trails")
                .whereEqualTo("id", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    Picasso.get().load(document.get("urlPhoto").toString()).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            Log.i(TAG,bitmap.toString());
                                            trails.add(bitmap);
                                            for (Bitmap bit : trails){
                                                Trail traill = new Trail(document.get("id").toString(),document.get("trailName").toString(),
                                                        document.get("address").toString(),document.get("description").toString(),document.get("duration").toString(),
                                                        document.get("distance").toString(),document.get("speed").toString(),
                                                        document.get("date").toString(),document.get("urlPhoto").toString(),(GeoPoint) document.get("coordStart"),
                                                        (GeoPoint) document.get("coordEnd"), (List<GeoPoint>) document.get("trailPoints"), (String) document.get("parentId"));


                                                imageModelArrayList.add(traill);
                                                if(todayDate.equals(document.get("date").toString())){
                                                    traildisplaycont++;

                                                    distanceTotal = Double.valueOf(distanceTotal)+Double.valueOf(document.get("distance").toString())+"";
                                                    Date date1 = null, date2 = null;
                                                    try {
                                                        date1 = timeFormat.parse(timeTotal);
                                                        date2 = timeFormat.parse(document.get("duration").toString());
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    long sum;
                                                    if((date1 != null) && (date2 != null)) {
                                                        sum = date1.getTime() + date2.getTime();
                                                        timeTotal = timeFormat.format(new Date(sum));
                                                        double mean =  Double.valueOf(speedTotal)+Double.valueOf(document.get("speed").toString())/traildisplaycont;
                                                        speedTotal = mean+"";

                                                        distance.setText(String.format("%.3f", Double.valueOf(distanceTotal))+" km");
                                                        duration.setText(timeTotal);
                                                        speed.setText(String.format("%.3f", Double.valueOf(speedTotal))+" ms");
                                                    }




                                                }
                                            }
                                            mHorizontalRecyclerView = (RecyclerView) root.findViewById(R.id.trailsView);
                                            horizontalAdapter = new HorizontalRecyclerHomeAdapter(imageModelArrayList,trails, getContext());
                                            horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                            mHorizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
                                            mHorizontalRecyclerView.setAdapter((RecyclerView.Adapter) horizontalAdapter);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        }
                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    });

                                }





                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        return root;
    }
}
