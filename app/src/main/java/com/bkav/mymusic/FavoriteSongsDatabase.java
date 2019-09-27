package com.bkav.mymusic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class FavoriteSongsDatabase extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "FavoriteSongs";
    private static final int DB_VESION = 1;
    private static final String DB_NAME = "favorite_songs";

    private static final String TABLE_FAVORITESONGS = "favoritesongs";
    private static final String ID = "_id";
    private static final String TITLE = "title";

    private static final String DB_SCHEMA = "create table " + TABLE_FAVORITESONGS + "(" + ID + "integer primary key autoincrement," + TITLE + "text not null);";

    private SQLiteDatabase database;

    public FavoriteSongsDatabase(Context context) {
        super(context, DB_NAME, null, DB_VESION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_SCHEMA);
    }

    public Cursor query(SQLiteQueryBuilder queryBuilder, String[] projection, String selection, String[] selectionArg, String orderBy) {

        if(queryBuilder==null){
            queryBuilder=new SQLiteQueryBuilder();
            queryBuilder.setTables(TABLE_FAVORITESONGS);
        }

        Cursor cursor = queryBuilder.query(database,projection,selection,selectionArg,null,null,orderBy);

        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
