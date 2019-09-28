package com.bkav.mymusic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class FavoriteSongsDatabase  {
//    private static final String DEBUG_TAG = "FavoriteSongs";extends SQLiteOpenHelper
//    private static final int DB_VESION = 1;
//    private static final String DB_SONGS = "db_songs";
//
//    private static final String TABLE_LISTSONGS = "listsongs";
//    private static final String ID_LIST= "id";
//      static final String DATA   = "data";
//      static final String TITLE  = "title";
//      static final String ALBUM  = "album";
//      static final String ARTIST = "artist";
//      static final String DURATION = "duration";
//      static final String CREATE_TABLE_LISTSONGS =
//            "create table " +TABLE_LISTSONGS+"("+
//                    ID_LIST + "integer primary key autoincrement," +
//                    DATA +"text ,"+
//                    TITLE + "text ," +
//                    ALBUM + "text ," +
//                    ARTIST + "text ," +
//                    DURATION + "integer " +
//                    ");";
//
//    private static final String TABLE_FAVORITESONGS = "favoritesongs";
//    private static final String ID_FAVORITE = "id";
//    private static final String FAVORITE = "favorite";
//    private static final String COUNT = "count";
//    private static final String CREATE_TABLE_FAVORITESONGS =
//            "create table " + TABLE_FAVORITESONGS + "(" +
//            ID_FAVORITE + "integer primary key autoincrement," +
//            FAVORITE + "integer default -1, "+
//            COUNT+"integer default 0  );";
//
//    private SQLiteDatabase database;
//
//    public FavoriteSongsDatabase(Context context) {
//        super(context, DB_SONGS, null, DB_VESION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL(CREATE_TABLE_FAVORITESONGS);
//        sqLiteDatabase.execSQL(CREATE_TABLE_LISTSONGS);
//    }
//
//    public Cursor query(SQLiteQueryBuilder queryBuilder, String[] projection, String selection, String[] selectionArg, String orderBy) {
//
//        if(queryBuilder==null){
//            queryBuilder=new SQLiteQueryBuilder();
//            queryBuilder.setTables(TABLE_FAVORITESONGS);
//        }
//
//        Cursor cursor = queryBuilder.query(database,projection,selection,selectionArg,null,null,orderBy);
//        return cursor;
//    }
//
//    public long insert(ContentValues values){
//
//        try {
//            long insertDB= database.insert(CREATE_TABLE_LISTSONGS,null, values);
//            return insertDB;
//        }catch (Exception ex){
//            Log.e("Error insert", ex+"");
//        }
//        return 0;
//    }
//
//    public  int delete(String selection , String [] selectionArg){
//    try {
//        return database.delete(CREATE_TABLE_LISTSONGS,selection,selectionArg);
//    }catch (Exception ex){
//        Log.e("Error delete", ""+ex);
//    }
//    return 0;
//    }
//
//    public int update(ContentValues values , String selection, String []selectionArg){
//        try{
//            return  database.update(CREATE_TABLE_LISTSONGS,values, selection,selectionArg );
//        }catch (Exception ex){
//            Log.e("Error update", ""+ex);
//        }
//        return 0;
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITESONGS);
//        onCreate(database);
//    }
}
