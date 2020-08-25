package com.marsanpat.alba.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "message_table")
public class Message {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "message")
    private String message;

    //TODO INCLUDE MESSAGE TYPES: Error, info, etc.

    public Message(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
