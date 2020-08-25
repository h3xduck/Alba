package com.marsanpat.alba.Utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONManager {


    public String[] extractJSON(String jsonString)throws JSONException {
        String[] stringArray = new String[3];

        JSONObject jsonObject  = new JSONObject(jsonString);

        Log.d("debug", "Starting decoding of JSON into strings");
        stringArray[0] = jsonObject.getString("Date");
        stringArray[1] = jsonObject.getString("Content");
        stringArray[2] = jsonObject.getString("User");

        return stringArray;

    }
}
