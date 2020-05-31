package com.example.cmprojeto.ui.runners;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RunnersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RunnersViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}