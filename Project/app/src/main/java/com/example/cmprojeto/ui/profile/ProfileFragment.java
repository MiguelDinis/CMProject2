package com.example.cmprojeto.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private Button signOutButton;
    private ImageView userPic;
    private String photoUrl;
    private String userName;
    private TextView userNameText;
    private ImageButton changePhoto;
    private String userId;
    private MainActivity main;
    private ImageView qrcodeView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        main =  ((MainActivity) this.requireActivity());
        signOutButton = (Button) root.findViewById(R.id.but_sign_out);
        changePhoto = root.findViewById(R.id.changePhotoBut);
        userPic = root.findViewById(R.id.userPic);
        userNameText = root.findViewById(R.id.userNameText);
        qrcodeView = root.findViewById(R.id.qrCodeView);
        signOutButton.setOnClickListener(this);
        changePhoto.setOnClickListener(this);
        photoUrl = main.getUrlPhoto();
        Picasso.get().load(photoUrl).into(userPic);
        userName = main.getUserName();
        userNameText.setText(userName);
        userId = main.getUserId();

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
}

