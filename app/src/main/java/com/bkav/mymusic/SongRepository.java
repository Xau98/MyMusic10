package com.bkav.mymusic;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SongRepository {
    private SongDao songDao;
    private LiveData<List<Song>> AllSong;
    private LiveData<List<Song>> LoveSong;
    private LiveData<List<Song>> SeachSong;

    SongRepository(){

    }
    public SongRepository(Application application) {
        SongRoom sr= SongRoom.getDatabase(application);
        this.songDao = sr.songDao();
        AllSong=songDao.getAllSong();
        LoveSong=songDao.getloveSong();

    }

    public LiveData<List<Song>> getAllSong() {
        return AllSong;
    }

    public LiveData<List<Song>> getLoveSong() {
        return LoveSong;
    }

    public LiveData<List<Song>> getSeachSong(String seachSong) {
        SeachSong = songDao.searchSong(String.format("%%%s%%", seachSong));
        return SeachSong;
    }

    void update(Song Song) {
        new update(songDao).execute(Song);
    }

    private static class update extends AsyncTask<Song, Void, Void> {
        SongDao songDao;

        public update(SongDao songDao) {
            this.songDao = songDao;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            Song song = songs[0];
            songDao.update(song);
            return null;
        }
    }

}


