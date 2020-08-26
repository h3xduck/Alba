package com.marsanpat.alba.ui.home;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marsanpat.alba.Database.MessageRepository;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private LiveData<Boolean> connectionState;
    private MessageRepository mRepository;

    public HomeViewModel(Application application) {
        mRepository = MessageRepository.getInstance(application);
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        connectionState = mRepository.getClientConnectionState();
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<Boolean> getConnectionState(){
        return connectionState;
    }
}