package com.example.cmprojeto.ui.runners;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cmprojeto.R;

public class RunnersFragment extends Fragment {

    private RunnersViewModel runnersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        runnersViewModel =
                ViewModelProviders.of(this).get(RunnersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_runners, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        runnersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
