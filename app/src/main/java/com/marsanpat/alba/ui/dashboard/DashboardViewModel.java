package com.marsanpat.alba.ui.dashboard;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marsanpat.alba.Database.Word;
import com.marsanpat.alba.Database.WordRepository;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private WordRepository mRepository;

    private LiveData<List<Word>> mAllWords;

    public DashboardViewModel (Application application) {

        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
    }


    LiveData<List<Word>> getAllWords() { return mAllWords; }

    public void insert(Word word) { mRepository.insert(word); }
}