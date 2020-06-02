package com.example.ghostrunner;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghostrunner.ui.map.MapFragment;

import java.util.ArrayList;
import java.util.List;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.MessageViewHolder> {

    private ArrayList<ImageModel> imageModelArrayList = new ArrayList<>();
    private Context context;
    private List<Bitmap> mDataset;
    public HorizontalRecyclerViewAdapter(ArrayList<ImageModel> horizontalList,List<Bitmap> myDataset, Context context) {
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
        final ImageModel model = imageModelArrayList.get(position);
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;

        messageViewHolder.imageView.setImageBitmap(mDataset.get(position));
        messageViewHolder.trailName.setText(model.getTrailName());
        //messageViewHolder.duration.setText(model.getDuration());
        messageViewHolder.distance.setText(model.getDistance());
        //messageViewHolder.speed.setText(model.getSpeed());
        messageViewHolder.date.setText(model.getDate());

        messageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MapFragment.idPressed(model.getCoordsStart(), model.getCoordsEnd());
                //Toast.makeText(context, model.getImageName()+" - "+position, Toast.LENGTH_SHORT).show();
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
            //duration = (TextView) view.findViewById(R.id.durationtxt);
            distance = (TextView) view.findViewById(R.id.distancetxt);
            //speed = (TextView) view.findViewById(R.id.speedtxt);
            date = (TextView) view.findViewById(R.id.date);

        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new MessageViewHolder(itemView);
    }
}
