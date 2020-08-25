package com.marsanpat.alba.Database;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.marsanpat.alba.Controller.MessageController;

import java.util.Date;
import java.util.List;

public class MessageRepository {

    private MessageDao messageDao;
    private LiveData<List<Message>> mAllMessages;
    private LiveData<Message> newServerMessage;

    public MessageRepository(Application application) {
        MessageRoomDatabase db = MessageRoomDatabase.getDatabase(application);

        MessageController controller = new MessageController();
        controller.startServer();

        messageDao = db.messageDao();

        newServerMessage = controller.getNewMessages();
        newServerMessage.observeForever(new Observer<Message>() {
            @Override
            public void onChanged(@Nullable final Message message) {
                if(!newServerMessage.getValue().getMessage().equals("")){
                    Log.d("debug", "new message to be inserted is: "+newServerMessage.getValue().getMessage());
                    insert(newServerMessage.getValue());
                }

            }
        });




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