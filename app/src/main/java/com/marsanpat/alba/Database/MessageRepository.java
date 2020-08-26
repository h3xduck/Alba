package com.marsanpat.alba.Database;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.marsanpat.alba.Controller.MessageController;
import com.marsanpat.alba.Controller.ProtocolParser;
import com.marsanpat.alba.Utils.JSONManager;

import org.json.JSONException;

import java.util.List;

public class MessageRepository {

    private MessageDao messageDao;
    private LiveData<List<Message>> mAllMessages;
    private LiveData<Message> newServerMessage;

    private static MessageRepository messageRepository;

    //SINGLETON PATTERN
    public  static MessageRepository getInstance(Application application) {
        if (messageRepository==null) {
            messageRepository = new MessageRepository(application);
        }
        return messageRepository;
    }

    private MessageRepository(Application application) {
        MessageRoomDatabase db = MessageRoomDatabase.getDatabase(application);

        messageDao = db.messageDao();

        newServerMessage = MessageController.getInstance().getNewMessages();
        newServerMessage.observeForever(new Observer<Message>() {
            @Override
            public void onChanged(@Nullable final Message message) {
                if(!newServerMessage.getValue().getMessage().equals("")){
                    String messageReceived = newServerMessage.getValue().getMessage();
                    Log.d("debug", "Repository received: "+messageReceived);
                    ProtocolParser protocolParser = new ProtocolParser();
                    //The parser decides what to do with the new message.
                    Pair<String, Integer> resultBuffer = protocolParser.parse(messageReceived);
                    switch (resultBuffer.second){
                        case -1:
                            Log.e("debug", "The server sent an invalid packet.");
                            break;
                        case 0:
                            Log.d("debug", "Server sent new data for the DB.");
                            try {
                                String[] decodedJSON = new JSONManager().extractJSON(resultBuffer.first);
                                Log.d("debug", "Final message to introduce is: "+decodedJSON[1]);
                                insert(new Message(decodedJSON[1]));
                            }catch(JSONException ex) {
                                Log.e("debug", "JSON error: " + ex.getMessage());
                                ex.printStackTrace();
                            }

                            break;
                        case 1:
                            //TODO ERRORS MUST HAVE OWN TABLE
                            Log.d("debug", "Server sent an error.");
                            insert(new Message(resultBuffer.first));
                            break;
                        case 2:
                            Log.d("debug", "Server sent some info data");
                            insert(new Message(resultBuffer.first));
                            break;
                        case 3:
                        case 4:
                            //TODO create PING-PONG(s) logic
                            Log.d("debug", "Server sent ping/pong");
                            break;

                    }

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

    /**
     * Returns connection state with server
     * @return
     * true if connected
     * false if disconnected
     */
    public LiveData<Boolean> getClientConnectionState(){
        return MessageController.getInstance().getLiveClientState();
    }
}