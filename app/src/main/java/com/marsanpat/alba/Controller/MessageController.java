package com.marsanpat.alba.Controller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marsanpat.alba.Database.Message;
import com.marsanpat.alba.Utils.JSONManager;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

public class MessageController {
    private static MessageController messageController;

    private final String HOSTNAME = "192.168.1.46";
    private final int PORT = 5001;

    public static boolean clientActive = false;
    public static MutableLiveData<Boolean> liveClientState = new MutableLiveData<>();

    //SINGLETON PATTERN
    public  static MessageController getInstance() {
        if (messageController==null) {
            messageController = new MessageController();
        }
        return messageController;
    }
    private MessageController(){
        liveClientState.setValue(false);
    }

    public static MutableLiveData<Message> messageList = new MutableLiveData<Message>(new Message(""));

    /**
     * Starts client connection with remote server.
     * @return
     * 0 if success
     * -1 if connection was already established
     * -2 if error
     */
    public int startClient(){
        if(clientActive){
            return -1; //Client is already connected to the server, don't start another connection thread.
            //It's a crappy mutex
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try (Socket socket = new Socket(HOSTNAME, PORT)) {
                    Thread.sleep(3000);//Artificial initial delay, just for testing purposes
                    long keepAliveTimer = 0; //TCP does not allow us to know if the server closed the connection, this emulates keep-alive functionality
                    clientActive = true; //Connection successful
                    liveClientState.postValue(true);
                    Log.d("debug","Server connection started");
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    while (clientActive) { //Reads from stream in 10sec intervals. Stops when something halts the client externally.
                        String response = reader.lines().collect(Collectors.joining());
                        if(!response.equals("")){
                            Log.d("debug", "Received from server: " + response);
                            //We received something from the server (might just be a PING), but enough to check connection is open
                            keepAliveTimer = System.currentTimeMillis();
                        }else{
                            Log.d("debug", "Connection idle");
                        }
                        if (!response.equals("")) {
                            messageList.postValue(new Message(response));
                        } else {
                            //Log.d("debug", "Ignored, empty string");
                        }
                        Thread.sleep(10000);

                        //Checking if the server closed the connection.
                        if(connectionTimedOut(keepAliveTimer, socket)){
                            Log.d("debug", "Connection timed out. Server closed it?");
                            clientActive = false;
                            liveClientState.postValue(false);
                        }
                    }


                } catch (UnknownHostException ex) {
                    Log.d("debug", "Server not found: " + ex.getMessage());
                } catch (IOException ex) {
                    Log.d("debug", "I/O error: " + ex.getMessage());
                }catch (Exception e) {
                    Log.d("debug", "FATAL error: " + e.getMessage());
                }finally {
                    clientActive = false; //Client stopped.
                    liveClientState.postValue(false);
                    Log.d("debug","Client stopped");
                }
            }
        });
        thread.start();
        if(clientActive){
            return 0;
        }else{
            return -2;
        }
    }

    //TODO: RETURN A LIST
    public LiveData<Message> getNewMessages(){
        LiveData<Message> result = messageList;
        //TODO check this up: messageList = null;
        return result;
    }

    public boolean isClientActive(){
        return clientActive;
    }

    public LiveData<Boolean> getLiveClientState(){
        return liveClientState;
    }

    private boolean connectionTimedOut(long keepAliveTimer, Socket socket){
        final long maxMillisWithoutNotice = 25000;
        long currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis-keepAliveTimer>maxMillisWithoutNotice){
            sendPING(socket);
            return true;
        }
        return false;
    }

    private void sendPING(Socket socket){
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        Log.d("debug", "Sending PING to the Server");
        // write the message we want to send
        dataOutputStream.writeUTF("PING:Hello server\n");
        dataOutputStream.flush(); // send the message
        } catch (IOException e) {
            Log.e("debug", "Error writing in socket stream");
            e.printStackTrace();
        }
    }
}
