package com.marsanpat.alba.Controller;

import android.util.Log;

import androidx.core.util.Pair;

import java.util.Arrays;
import java.util.List;

public class ProtocolParser {
    private static final List<String> KEYWORDS =
            Arrays.asList("INCLUDE", "ERROR", "INFO", "PING", "PONG");

    private boolean isValidProtocolMessage(String input){
        String[] parts = input.split(":", 2);
        Log.d("debug", "Separated as "+parts[0]+" and "+parts[1]);
        if(KEYWORDS.contains(parts[0])){
            return true;
        }
        return false;
    }

    private String getHeader (String input){
        String[] parts = input.split(":", 2);
        return parts[0];
    }

    private String getContent (String input){
        String[] parts = input.split(":", 2);
        return parts[1];
    }



    /**
     * Returns a pair of values:
     * String where some info is returned.
     * Integer with command code, indicating the repository what to do.
     * @param input: Message to parse.
     * @return
     * <String, 0> if it is a new message to include in DB (they always come in JSON format)
     * <String, 1> if it is an error sent by the server
     * <String, 2> if it is some info sent by the server, not needed to be included in the DB
     * <null, 3> if it is a PING
     * <null, 4> if it is a PONG
     * <null, -1> if error (parser could not parse).
     */
    public Pair<String, Integer> parse(String input){
        //TODO: This can be optimized. I'm dividing the string so many times
        if(isValidProtocolMessage(input)){
            String header = getHeader(input);
            switch (header){
                case "INCLUDE":
                    return new Pair<>(getContent(input), 0);
                case "ERROR":
                    return new Pair<>(getContent(input), 1);
                case "INFO":
                    return new Pair<>(getContent(input), 2);
                case "PING":
                    return new Pair<>(null, 3);
                case "PONG":
                    return new Pair<>(null, 4);
            }
        }
        return new Pair<>(null, -1);
    }

}
