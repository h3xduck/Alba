package com.marsanpat.alba.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MessageRepository {

    private MessageDao messageDao;
    private LiveData<List<Message>> mAllMessages;

    public MessageRepository(Application application) {
        MessageRoomDatabase db = MessageRoomDatabase.getDatabase(application);
        messageDao = db.messageDao();
        mAllMessages = messageDao.getAlphabetizedWords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Message>> getAllMessages() {
        return mAllMessages;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Message message) {
        MessageRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageDao.insert(message);
        });
    }
}