package com.bkav.mymusic;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SongViewModel extends AndroidViewModel {
    private SongRepository mSongRepository;
    private LiveData<List<Song>> mSong;
    private LiveData<List<Song>> mLoveSong;
    private LiveData<List<Song>> mSeachSong;

    public SongViewModel(@NonNull Application application) {
        super(application);
        mSongRepository=new SongRepository(application);
        mSong = mSongRepository.getAllSong();
        mLoveSong=mSongRepository.getLoveSong();
    }

    public LiveData<List<Song>> getmSong() {
        return mSong;
    }

    public LiveData<List<Song>> getLoveSong() {
        return mLoveSong;
    }

    public LiveData<List<Song>> getSeachSong(String seachSong) {
        mSeachSong=mSongRepository.getSeachSong(seachSong);
        return mSeachSong;
    }

    void update(Song Song) {
        mSongRepository.update(Song);
    }
}
