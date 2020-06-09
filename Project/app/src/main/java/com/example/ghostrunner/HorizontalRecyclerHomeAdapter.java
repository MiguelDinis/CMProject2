package com.example.ghostrunner;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghostrunner.models.Trail;
import com.example.ghostrunner.ui.home.trailsComparation;
import com.example.ghostrunner.ui.map.MapFragment;
import com.example.ghostrunner.ui.runners.FriendPage;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class HorizontalRecyclerHomeAdapter extends RecyclerView.Adapter<HorizontalRecyclerHomeAdapter.MessageViewHolder> {

    private ArrayList<Trail> imageModelArrayList = new ArrayList<>();
    private Context context;
    private List<Bitmap> mDataset;
    public HorizontalRecyclerHomeAdapter(ArrayList<Trail> horizontalList,List<Bitmap> myDataset, Context context) {
        this.imageModelArrayList = horizontalList;
        this.context = context;
        this.mDataset = myDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return  super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {
        final Trail model = imageModelArrayList.get(position);
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;

        messageViewHolder.imageView.setImageBitmap(mDataset.get(position));
        messageViewHolder.trailName.setText(model.getTrailName());
        messageViewHolder.duration.setText(model.getDuration());
        messageViewHolder.distance.setText(String.format("%.3f", Double.valueOf(model.getDistance()))+" km");
        messageViewHolder.speed.setText( String.format("%.3f", Double.valueOf(model.getSpeed()))+" m/s");
        messageViewHolder.date.setText(model.getDate());

        messageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Bundle args = new Bundle();
                args.putString("trailName", model.getTrailName());
                args.putString("time",model.getDuration());
                Fragment myFragment = new trailsComparation();
                myFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.trails, myFragment).addToBackStack(null).commit();
            }
        });
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView trailName;
        TextView duration;
        TextView distance;
        TextView speed;
        TextView date;


        private MessageViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            trailName = (TextView) view.findViewById(R.id.trailNamecard);
            duration = (TextView) view.findViewById(R.id.durationtxt);
            distance = (TextView) view.findViewById(R.id.distancetxt);
            speed = (TextView) view.findViewById(R.id.speedtxt);
            date = (TextView) view.findViewById(R.id.date);

        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new MessageViewHolder(itemView);
    }
}