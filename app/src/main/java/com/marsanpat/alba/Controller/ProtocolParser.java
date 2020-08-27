package com.marsanpat.alba.Controller;

import android.util.Log;

import androidx.core.util.Pair;

import java.util.Arrays;
import java.util.List;

public class ProtocolParser {
    private static final List<String> KEYWORDS =
            Arrays.asList("INCLUDE", "ERROR", "INFO", "PING", "PONG", "STARTCONN", "ENDCONN");
    private static final String HEADER_SEPARATOR = "::"; //Indicates where the header ends.
    private static final String PROTOCOL_SEPARATOR = "\n##ALBA##\n"; //Indicates the end of the message received (the rest is filled with 0s).

    private boolean isValidProtocolMessage(String input){
        try {
            String[] parts = input.split(HEADER_SEPARATOR, 2);
            Log.d("debug", "Separated as " + parts[0] + " and " + parts[1]);
            if (KEYWORDS.contains(parts[0])) {
                String[] parts2 = parts[1].split(PROTOCOL_SEPARATOR, 2);
                Log.d("debug", "Then separated as " + parts2[0] + " and " + parts2[1]);
                if(parts2[1]!=null){
                    return true;
                }
            }
            return false;
        }catch (Exception ex){
            return false;
        }
    }

    private String getHeader (String input){
        String[] parts = input.split(HEADER_SEPARATOR, 2);
        return parts[0];
    }

    private String getContent (String input){
        String[] parts = input.split(HEADER_SEPARATOR, 2);
        String[] parts2 = parts[1].split(PROTOCOL_SEPARATOR, 2);
        return parts2[0];
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
     * <null, -1> if error (parser could not parse)
     * <null, 100> if communication starts
     * <null, 200> if communication ends
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
                case "STARTCONN":
                    return new Pair<>(null, 100);
                case "ENDCONN":
                    return new Pair<>(null, 200);
            }
        }
        return new Pair<>(null, -1);
    }

}
