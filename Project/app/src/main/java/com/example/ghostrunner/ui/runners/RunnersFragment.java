package com.example.ghostrunner.ui.runners;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.ghostrunner.FriendsAdapter;
import com.example.ghostrunner.MainActivity;
import com.example.ghostrunner.R;
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

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class RunnersFragment extends Fragment {

    private TextView qrcodeText;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FriendsAdapter friendsAdapter;
    private FirebaseFirestore db;
    private MainActivity main;
    private String userId;
    private List<String> list;
    private List<String> friendList;
    private List<Bitmap> friendsUrls;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


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
        friendList = new ArrayList<>();
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
        if (checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }else{
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.forSupportFragment(RunnersFragment.this).initiateScan();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.forSupportFragment(RunnersFragment.this).initiateScan();
            }
            else
            {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
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
                    if (documentSnapshot.get("mPhotoUrl") != null){
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
                    });}
                }
            });
        }


    }

    //Update the friends to the two persons.
    private void updateFriendsList(final String newFriend){
        list.add(newFriend);
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.update("friends", list)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("RunnersFragment", "DocumentSnapshot successfully updated!");
                        DocumentReference friendRef = db.collection("Users").document(newFriend);
                        friendRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                friendList = (List<String>) documentSnapshot.get("friends");
                                friendList.add(userId);
                                DocumentReference friendRef2 = db.collection("Users").document(newFriend);
                                friendRef2.update("friends", friendList)
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
                        });
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
