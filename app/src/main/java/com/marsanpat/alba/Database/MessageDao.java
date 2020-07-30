package com.marsanpat.alba.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message word);

    @Query("DELETE FROM message_table")
    void deleteAll();

    @Query("SELECT * from message_table ORDER BY message ASC")
    LiveData<List<Message>> getAlphabetizedWords();
}