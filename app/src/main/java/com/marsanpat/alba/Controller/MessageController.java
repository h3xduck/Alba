package com.marsanpat.alba.Controller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marsanpat.alba.Database.Message;
import com.marsanpat.alba.Utils.JSONManager;
import com.marsanpat.alba.ui.logs.LogFragment;

import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MessageController {
    private static MessageController messageController;

    private final String HOSTNAME = "192.168.1.46";
    private final int PORT = 5001;

    public static boolean clientActive = false;
    public static MutableLiveData<Boolean> liveClientState = new MutableLiveData<>();

    private static int PROTOCOL_STANDARD_MESSAGE_LENGTH = 1024;

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
                    Thread.sleep(2000);//Artificial initial delay, just for testing purposes
                    long keepAliveTimer = 0; //TCP does not allow us to know if the server closed the connection, this emulates keep-alive functionality
                    clientActive = true; //Connection successful
                    liveClientState.postValue(true);
                    Log.d("debug","Server connection started");
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    while (clientActive) { //Reads from stream in 10sec intervals. Stops when something halts the client externally.
                        char[] response = new char[PROTOCOL_STANDARD_MESSAGE_LENGTH];
                        int charsRead = -1;
                        if(reader.ready()){
                            charsRead = reader.read(response, 0 , PROTOCOL_STANDARD_MESSAGE_LENGTH);
                        }
                        if(charsRead!=-1){
                            Log.v("debug", "Received from server: " + Arrays.toString(response));
                            Log.d("debug", "Read "+charsRead+" bytes in total");
                            //We received something from the server (might just be a PING), but enough to check connection is open
                            keepAliveTimer = System.currentTimeMillis();
                            messageList.postValue(new Message(new String(response)));
                        }else{
                            Log.d("debug", "Connection idle");
                        }
                        Thread.sleep(500);

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
        final long maxMillisWithoutNotice = 5000;
        final long delayWaitForPings = 3000;
        long currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis-keepAliveTimer>maxMillisWithoutNotice+delayWaitForPings){
            //We gave a bit of a delay for the server to send the Pong. If it's not here yet, we just disconnect.
            return true;
        }else if(currentTimeMillis-keepAliveTimer>maxMillisWithoutNotice){
            //We send a PING to the server. If it answers, the timer is reset.
            sendPING(socket);
            return false;
        }
        return false;
    }

    private void sendPING(Socket socket){
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        byte[] fillerArray = new byte[PROTOCOL_STANDARD_MESSAGE_LENGTH];
        Arrays.fill(fillerArray, (byte) 0);
        Log.d("debug", "Sending PING to the Server");
        // write the message we want to send
        writer.write("PING::Hello server".concat(ProtocolParser.PROTOCOL_SEPARATOR).concat(Arrays.toString(fillerArray)), 0 , PROTOCOL_STANDARD_MESSAGE_LENGTH);
        writer.flush(); // send the message
        } catch (Exception e) {
            Log.e("debug", "Error writing in socket stream: "+e.toString());
        }
    }
}
