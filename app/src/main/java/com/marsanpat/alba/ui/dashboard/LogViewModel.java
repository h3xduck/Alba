package com.marsanpat.alba.ui.dashboard;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.marsanpat.alba.Database.Message;
import com.marsanpat.alba.Database.MessageRepository;

import java.util.List;

public class LogViewModel extends ViewModel {

    private MessageRepository mRepository;

    private LiveData<List<Message>> allMessages;

    public LogViewModel(Application application) {

        mRepository = new MessageRepository(application);
        allMessages = mRepository.getAllMessages();
    }


    LiveData<List<Message>> getAllMessages() { return allMessages; }

    public void insert(Message message) { mRepository.insert(message); }
}