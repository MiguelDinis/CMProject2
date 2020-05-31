package com.example.cmprojeto.ui.profile;

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


import com.example.cmprojeto.MainActivity;
import com.example.cmprojeto.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static int RESULT_LOAD_IMAGE = 1;


    private Button signOutButton;
    private ImageView userPic;
    private String photoUrl;
    private String userName;
    private TextView userNameText;
    private ImageButton changePhoto;
    private ImageButton galleryPick;
    private String userId;
    private MainActivity main;
    private ImageView qrcodeView;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference userImageRef;
    private FirebaseFirestore db;
    private DocumentReference docRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        main =  ((MainActivity) this.requireActivity());
        signOutButton = (Button) root.findViewById(R.id.but_sign_out);
        changePhoto = root.findViewById(R.id.changePhotoBut);
        galleryPick = root.findViewById(R.id.galleryBut);
        userPic = root.findViewById(R.id.userPic);
        userNameText = root.findViewById(R.id.userNameText);
        qrcodeView = root.findViewById(R.id.qrCodeView);
        signOutButton.setOnClickListener(this);
        changePhoto.setOnClickListener(this);
        galleryPick.setOnClickListener(this);
        db = FirebaseFirestore.getInstance();
        userId = main.getUserId();
        docRef = db.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                photoUrl = documentSnapshot.get("mPhotoUrl").toString();
                Picasso.get().load(photoUrl).into(userPic);
            }
        });

        userName = main.getUserName();
        userNameText.setText(userName);



        //Cloud storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        userImageRef = storageRef.child("users/" +userId);


        initQRCode(userId);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.but_sign_out:
                Activity act = getActivity();
                if (act instanceof MainActivity)
                    ((MainActivity) act).signOutActivated();
                break;
            case R.id.changePhotoBut:
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
            case R.id.galleryBut:
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
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            userPic.setImageBitmap(photo);
            uploadAndGetUrl(photo);

        }//Pick image from gallery
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                Uri imageUri = data.getData();
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

    private void initQRCode(String textToSend) {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToSend.toString(), BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcodeView.setImageBitmap(bitmap);
            qrcodeView.setVisibility(View.VISIBLE);

        } catch (WriterException e) {
            e.printStackTrace();
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
                storageRef.child("users/" +userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        docRef.update("mPhotoUrl",uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

