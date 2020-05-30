package com.example.cmprojeto.ui.profile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cmprojeto.MainActivity;
import com.example.cmprojeto.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Button signOutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        signOutButton = (Button) root.findViewById(R.id.but_sign_out);
        signOutButton.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        Activity act = getActivity();
        if (act instanceof MainActivity)
            ((MainActivity) act).signOutActivated();
    }
}

