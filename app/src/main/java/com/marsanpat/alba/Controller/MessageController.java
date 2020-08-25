package com.marsanpat.alba.Controller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marsanpat.alba.Database.Message;
import com.marsanpat.alba.Utils.JSONManager;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

public class MessageController {
    private static MessageController messageController;

    private final String HOSTNAME = "192.168.1.46";
    private final int PORT = 5001;

    public static boolean clientActive = false;

    //SINGLETON PATTERN
    public  static MessageController getInstance() {
        if (messageController==null) {
            messageController = new MessageController();
        }
        return messageController;
    }
    private MessageController(){}

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
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try (Socket socket = new Socket(HOSTNAME, PORT)) {
                    Thread.sleep(3000);//Artificial initial delay, just for testing purposes
                    clientActive = true; //Connection successful
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    while (clientActive) { //Reads from stream in 10sec intervals. Stops when something halts the client externally.
                        String response = reader.lines().collect(Collectors.joining());
                        if(response!=""){
                            Log.d("debug", "Received from server: " + response);
                        }else{
                            Log.d("debug", "Connection idle");
                        }
                        if (!response.equals("")) {
                            messageList.postValue(new Message(response));
                        } else {
                            //Log.d("debug", "Ignored, empty string");
                        }
                        Thread.sleep(10000);

                        //This code checks if the server closed the connection.
                        //This is the only way to do it in the TCP protocol
                        if(!isServerConnected(socket)){
                            Log.d("debug", "Connection closed by server");
                            clientActive = false;
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

    public boolean isServerConnected(Socket socket){
        try{
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.write("");
            return true;
        }catch(IOException ex){
            return false;
        }
    }

}
