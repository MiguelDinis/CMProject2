package com.example.ghostrunner.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ghostrunner.MainActivity;
import com.example.ghostrunner.R;
import com.example.ghostrunner.models.Trail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class DescriptionFragment extends Fragment implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static int RESULT_LOAD_IMAGE = 1;


    private Button addButton;
    private ImageView userPic;
    private boolean photoChanged;
    private String photoUrl;
    private ImageButton changePhoto;
    private ImageButton galleryPick;
    private String userId;
    private MainActivity main;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference userImageRef;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private String formattedDate;
    private TextView trailname;
    private TextView trailLocation;
    private TextView trailDescription;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_description, container, false);

        main =  ((MainActivity) this.requireActivity());
        addButton = (Button) root.findViewById(R.id.addBtn);

        changePhoto = root.findViewById(R.id.trailPhotoBut);
        galleryPick = root.findViewById(R.id.trailGalleryBut);
        userPic = root.findViewById(R.id.trailPic);
        trailname = root.findViewById(R.id.trailname);
        trailLocation = root.findViewById(R.id.address);
        trailDescription = root.findViewById(R.id.description);
        addButton.setOnClickListener(this);
        changePhoto.setOnClickListener(this);
        galleryPick.setOnClickListener(this);
        photoChanged = false;
        db = FirebaseFirestore.getInstance();
        userId = main.getUserId();
        docRef = db.collection("Users").document(userId);


        //Cloud storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        userImageRef = storageRef.child("trails/" +userId);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c);



        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addBtn:
                String name = trailname.getText().toString();
                String address = trailLocation.getText().toString();
                String description = trailDescription.getText().toString();
                if (photoChanged == true){
                    if(photoUrl == null) photoUrl = "https://contents.mediadecathlon.com/p1427463/640x0/27cr14/trail.jpg?k=3b52640a69d7a4dbb395121267e6ab91";
                    Trail tmpTrail = new Trail(userId, name,  address,  description, "1.5km", formattedDate,  photoUrl, new GeoPoint(40.048511, -8.890201),new GeoPoint(40.255185, -8.890201));
                    db.collection("Trails").document(userId).set(tmpTrail);
                    Intent goToHome = new Intent(getContext(),MainActivity.class);
                    startActivity(goToHome);

                }else{
                    Toast.makeText(getContext(), "Change the Trail Photo", Toast.LENGTH_LONG).show();
                }
                /*Activity act = getActivity();
                if (act instanceof MainActivity)
                    ((MainActivity) act).signOutActivated();*/
                break;
            case R.id.trailPhotoBut:
                if (checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                break;
            case R.id.trailGalleryBut:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                break;

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
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            photoChanged = true;
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            userPic.setImageBitmap(photo);
            uploadAndGetUrl(photo);

        }//Pick image from gallery
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                photoChanged = true;
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                userPic.setImageBitmap(selectedImage);
                uploadAndGetUrl(selectedImage);
            } catch (FileNotFoundException | IllegalStateException  e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void uploadAndGetUrl(Bitmap photo){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoBytes = baos.toByteArray();
        UploadTask uploadTask = userImageRef.putBytes(photoBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Failed to upload photo to cloud", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Photo Uploaded to Cloud", Toast.LENGTH_LONG).show();
                storageRef.child("trails/" +userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photoUrl = uri.toString();
                        docRef.update("urlPhoto",uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("ProfileFragment", "DocumentSnapshot successfully updated!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("ProfileFragment", "Error updating document", e);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });
    }
}

