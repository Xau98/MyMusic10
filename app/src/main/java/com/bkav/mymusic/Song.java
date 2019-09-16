package com.bkav.mymusic;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songTable")
public class Song {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")

    private long id;

    @ColumnInfo(name = "title")
    private  String title;

    @ColumnInfo(name = "file")
    private  String file;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "duration")
    private int duration;

    @ColumnInfo(name = "love")
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


