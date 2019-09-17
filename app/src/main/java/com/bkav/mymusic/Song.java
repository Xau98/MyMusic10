package com.bkav.mymusic;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class Song {
    private long id;
    private  String title;

    private  String file;

    private String artist;

    private int duration;

    private  int love;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    public String getFile() {
        return file;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public int getLove() {
        return love;
    }


    public Song(long id, String title, String file, String artist, int duration
            , int love) {
        this.title=title;
        this.id=id;
        this.file=file;
        this.artist = artist;
        this.duration= duration;
        this.love=love;
    }
}


