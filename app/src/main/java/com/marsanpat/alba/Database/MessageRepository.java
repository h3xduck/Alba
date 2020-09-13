package com.marsanpat.alba.Database;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.marsanpat.alba.Controller.MessageController;
import com.marsanpat.alba.Controller.ProtocolParser;
import com.marsanpat.alba.Utils.JSONManager;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

public class MessageRepository {

    private MessageDao messageDao;
    private LiveData<List<Message>> mAllMessages;
    private LiveData<Message> newServerMessage;

    private static MessageRepository messageRepository;
    private boolean communicatingWithServer = false;
    private String buildUpControllerMessage =""; //Constructs the whole message received from the controller by String concatenation.

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
                    Log.v("debug", "Repository received: "+messageReceived);
                    ProtocolParser protocolParser = new ProtocolParser();
                    //The parser decides what to do with the new message.
                    Pair<String, Integer> resultBuffer = protocolParser.parse(messageReceived);
                    List<Integer> codesAbsentFromDropping = Arrays.asList(100, 1, -1, 3, 4); //This codes are not dropped when there is no STARTCONN
                    if(!codesAbsentFromDropping.contains(resultBuffer.second)&&!communicatingWithServer){
                        Log.e("debug", "DROPPED PACKAGE: Server did not send STARTCONN first");
                        return;
                    }
                    switch (resultBuffer.second){
                        case -1:
                            Log.e("debug", "The server sent an invalid packet.");
                            break;
                        case 0:
                            Log.i("debug", "Server sent new data for the DB.");
                            Log.d("debug", "Server sent part of a message: "+resultBuffer.first);
                            buildUpControllerMessage += resultBuffer.first;

                            break;
                        case 1:
                            //TODO ERRORS MUST HAVE OWN TABLE
                            Log.e("debug", "Server sent an error.");
                            insert(new Message(resultBuffer.first));
                            break;
                        case 2:
                            Log.i("debug", "Server sent some info data");
                            buildUpControllerMessage+=resultBuffer.first;
                            break;
                        case 3:
                            Log.i("debug", "Server sent PING");
                            MessageController.getInstance().enqueueMessage("PONG::Hello server");
                            break;
                        case 4:
                            Log.i("debug", "Server sent PONG");
                            break;
                        case 100:
                            Log.i("debug", "STARTCONN received");
                            communicatingWithServer = true;
                            break;
                        case 200:
                            Log.i("debug", "ENDCOMM received");
                            Log.i("debug", "Introducing final message");
                            communicatingWithServer = false;
                            try{
                                String[] decodedJSON = new JSONManager().extractJSON(buildUpControllerMessage);
                                insert(new Message(decodedJSON[1]));
                                Log.i("debug", "SUCCESS");
                                break;
                            }catch(JSONException ex) {
                                Log.e("debug", "JSON error: " + ex.getMessage());
                                Log.e("debug", "Discarded message: "+buildUpControllerMessage);
                                ex.printStackTrace();
                             }finally {
                                buildUpControllerMessage="";
                            }

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