package com.bkav.mymusic;


public class Song {
    private int id;

    private  String title;

    private  String file;

    private String artist;

    private int duration;


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



    public Song(int id, String title, String file, String artist, int duration  ) {
        this.title=title;
        this.id=id;
        this.file=file;
        this.artist = artist;
        this.duration= duration;
    }
}


