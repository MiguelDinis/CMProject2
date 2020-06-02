package com.example.ghostrunner;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghostrunner.ui.map.DescriptionFragment;
import com.example.ghostrunner.ui.map.MapFragment;
import com.example.ghostrunner.ui.runners.FriendPage;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {
    private List<Bitmap> mDataset;
    private List<String> ids;
    private List<String> names;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public  ImageView imageView;;
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.runnerPhoto);
            textView = v.findViewById(R.id.friendNameCard);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendsAdapter(List<Bitmap> myDataset, List<String> friendsIds, List<String> MyNames) {
        mDataset = myDataset;
        ids = friendsIds;
        names = MyNames;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_runner, parent, false);
        return new FriendsAdapter.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.imageView.setImageBitmap(mDataset.get(position));
        holder.textView.setText(names.get(position));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Bundle args = new Bundle();
                args.putString("id", ids.get(position));
                Fragment myFragment = new FriendPage();
                myFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.runnersFragment, myFragment).addToBackStack(null).commit();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
