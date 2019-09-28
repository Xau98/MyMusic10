package com.bkav.mymusic;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FavoriteSongsProvider extends ContentProvider {
    private FavoriteSongsDatabase mFavoriteSongsDatabase;
    private static final String AUTHORITY = "com.bkav.mymusic";
    private static final String CONTENT_PATH = "listsongs";   //looix
    static final Uri CONTENT_URI = Uri.parse("Content://" + AUTHORITY + "/" + CONTENT_PATH);
    private static UriMatcher sUriMatcher;
    private static final int URI_ALL_ITEM_CODE = 1;
    private static final int URI_ONE_ITEM_CODE = 2;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, CONTENT_PATH, URI_ALL_ITEM_CODE);
        sUriMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", URI_ONE_ITEM_CODE);
    }


    @Override
    public boolean onCreate() {
        mFavoriteSongsDatabase = new FavoriteSongsDatabase(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String seclection, String[] seclectionArg, String orderBy) {
        switch (sUriMatcher.match(uri)) {
            case URI_ALL_ITEM_CODE:
                break;
            case URI_ONE_ITEM_CODE:
                break;
            default:
                throw new
                        SQLException("Failed to query " + uri);

        }
//        if(orderBy==null||orderBy==""){
//            orderBy
//        }
        Cursor cursor = mFavoriteSongsDatabase.query(null, projection,
                seclection, seclectionArg, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = mFavoriteSongsDatabase.insert(contentValues);
        if (rowID > 0) {
            Uri _uri = Uri.withAppendedPath(uri, "" + rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new
                SQLException("Failed to add a record into " + uri);

    }

    @Override
    public int delete(Uri uri, String selection, String[] slectionArg) {


        return mFavoriteSongsDatabase.delete(selection, slectionArg);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArg) {
        return mFavoriteSongsDatabase.update(contentValues, selection, selectionArg);
    }
}
