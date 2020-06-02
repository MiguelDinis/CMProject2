package com.example.ghostrunner.ui.runners;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghostrunner.FriendsAdapter;
import com.example.ghostrunner.MainActivity;
import com.example.ghostrunner.R;
import com.example.ghostrunner.models.Trail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendPage extends Fragment implements View.OnClickListener {


    private static final String TAG = "FRIENDPAGE";
    private MainActivity main;
    private ImageView friendPhoto;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private String photoUrl;
    private TextView weight;
    private TextView height;
    private TextView friendName;
    private friendTrailsAdapter friendTrailsAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<Trail> friendTrailsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_friend, container, false);

        main =  ((MainActivity) this.requireActivity());
        friendPhoto = root.findViewById(R.id.friendPhoto);
        weight = root.findViewById(R.id.weight);
        height = root.findViewById(R.id.height);
        friendName = root.findViewById(R.id.friendName);
        db = FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView) root.findViewById(R.id.friendrecycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        String value = getArguments().getString("id");
        friendTrailsList = new ArrayList<Trail>();


        userRef = db.collection("Users").document(value);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                photoUrl = documentSnapshot.get("mPhotoUrl").toString();
                Picasso.get().load(photoUrl).into(friendPhoto);
                weight.setText(documentSnapshot.get("userWeight").toString() + "kg");
                height.setText(documentSnapshot.get("userHeight").toString() + "cm");
                friendName.setText(documentSnapshot.get("userName").toString());

            }
        });

        db.collection("Trails")
                .whereEqualTo("id",value )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Trail trail = document.toObject(Trail.class);
                                friendTrailsList.add(trail);
                                friendTrailsAdapter = new friendTrailsAdapter(friendTrailsList);
                                recyclerView.setAdapter(friendTrailsAdapter);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return root;
    }


    @Override
    public void onClick(View v) {

    }
}

