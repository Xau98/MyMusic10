package com.bkav.mymusic;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SongDao {

    @Insert
    void insert(Song Song);

    @Query("Select * from songTable ")
    Song[] getAnySong();

    @Query("Select * from songTable ")
    LiveData<List<Song>> getAllSong();

    @Query("Select * from songTable where Title like :word ")
    LiveData<List<Song>> searchSong(String word);

    @Query("Select * from songTable where love=1")
    LiveData<List<Song>> getloveSong();

    @Update
    void update(Song Song);

}
