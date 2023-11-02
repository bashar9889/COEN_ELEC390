package com.example.coenelec390.ui.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemViewModel extends ViewModel {//TODO: To remove

    private final MutableLiveData<String> mText;

    public ItemViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is item fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}