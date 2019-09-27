package com.bkav.mymusic;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FavoriteSongsProvider extends ContentProvider {
    private FavoriteSongsDatabase mFavoriteSongsDatabase;
    private static final String AUTHORITY = "com.bkav.mymusic";
    private static final Uri CONTENT_URI = Uri.parse("Content://"+AUTHORITY+"/favoritesongs");
    private static  UriMatcher sUriMatcher =new UriMatcher(UriMatcher.NO_MATCH) ;
//    static {
//        sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
//    }


    @Override
    public boolean onCreate() {
        mFavoriteSongsDatabase = new FavoriteSongsDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {


        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
