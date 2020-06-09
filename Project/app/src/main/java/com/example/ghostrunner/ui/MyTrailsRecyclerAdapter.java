package com.example.ghostrunner.ui;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghostrunner.R;

import java.util.List;


public class MyTrailsRecyclerAdapter extends RecyclerView.Adapter<MyTrailsRecyclerAdapter.MyViewHolder> {
    private List<Bitmap> mDataset;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView trailName;
        public TextView duration;
        public TextView distance;
        public TextView speed;
        public TextView date;
        public MyViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.trailPhoto);
            trailName = (TextView) v.findViewById(R.id.trailNamecard2);
            duration = (TextView) v.findViewById(R.id.durationtxt2);
            distance = (TextView) v.findViewById(R.id.distancetxt2);
            speed = (TextView) v.findViewById(R.id.speedtxt2);
            date = (TextView) v.findViewById(R.id.date2);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyTrailsRecyclerAdapter(List<Bitmap> myDataset) {
        mDataset = myDataset;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MyTrailsRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailshome, parent, false);
        return new MyTrailsRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTrailsRecyclerAdapter.MyViewHolder holder, int position) {
        holder.imageView.setImageBitmap(mDataset.get(position));


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
