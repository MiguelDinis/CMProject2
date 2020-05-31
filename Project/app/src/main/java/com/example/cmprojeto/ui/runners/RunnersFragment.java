package com.example.cmprojeto.ui.runners;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmprojeto.FriendsAdapter;
import com.example.cmprojeto.MainActivity;
import com.example.cmprojeto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;


public class RunnersFragment extends Fragment {

    private TextView qrcodeText;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FriendsAdapter friendsAdapter;
    private FirebaseFirestore db;
    private MainActivity main;
    private String userId;
    private List<String> list;
    private List<Bitmap> friendsUrls;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_runners, container, false);
        qrcodeText = root.findViewById(R.id.qrcodeText);
        qrcodeText.setText("Friends List");
        recyclerView = (RecyclerView) root.findViewById(R.id.FriendsList);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        main =  ((MainActivity) this.requireActivity());
        userId = main.getUserId();
        list = new ArrayList<>();
        friendsUrls = new ArrayList<>();
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                list = (List<String>) documentSnapshot.get("friends");
                getFriendPhotoList(list);

            }

        });



        FloatingActionButton myFab = (FloatingActionButton) root.findViewById(R.id.addRunner);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startQRScanner();
            }
        });
        return root;
    }

    //Qrcode reader operations
    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.forSupportFragment(RunnersFragment.this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result =   IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(),"Cancelled",Toast.LENGTH_LONG).show();
            } else {
                updateFriendsList(result.getContents());
                getFriendPhotoList(list);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void getFriendsList(){
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                list = (List<String>) documentSnapshot.get("friends");
            }
        });
    }

    private void getFriendPhotoList(List<String> friendsList){
        friendsUrls = new ArrayList<>();
        for(String x : friendsList){
            DocumentReference docRef = db.collection("Users").document(x);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Picasso.get().load(documentSnapshot.get("mPhotoUrl").toString()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            friendsUrls.add(bitmap);
                            friendsAdapter = new FriendsAdapter(friendsUrls);
                            recyclerView.setAdapter(friendsAdapter);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        }
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
                }
            });
        }


    }

    private void updateFriendsList(String newFriend){
        list.add(newFriend);
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.update("friends", list)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("RunnersFragment", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("RunnersFragment", "Error updating document", e);
                    }
                });
    }

}
