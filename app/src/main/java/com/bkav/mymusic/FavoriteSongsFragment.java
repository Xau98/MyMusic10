package com.bkav.mymusic;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.ArrayList;

public class FavoriteSongsFragment extends BaseSongListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private ArrayList<Song> mListAllSong ;
    public FavoriteSongsFragment(ArrayList<Song> mListAllSong) {
        this.mListAllSong =new ArrayList<>();
        this.mListAllSong = mListAllSong;

        Log.e("song Favo",mListAllSong.size()+"//");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getContext(), "F//"+mMusicService, Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(LOADER_ID, null, this);
//        Log.d("search", Log.getStackTraceString(new Exception()));
        Log.d("all song", "all song");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String URL = "content://com.bkav.provider";
        Uri uriSongs = Uri.parse(URL);
        return new CursorLoader(getContext(),uriSongs, null, null, null, null);
    }

    public void setmListAllSong(ArrayList<Song> mListAllSong) {
        this.mListAllSong = mListAllSong;

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.e("song Favo",mListAllSong.size()+"//");
        ArrayList<Song> mListFavoriteSongs = new ArrayList<>();
        Song song =null;
        if (cursor.moveToFirst()) {
            do {

                for(int i=0;i<mListAllSong.size();i++){

                     if(mListAllSong.get(i).getId()== cursor.getInt(cursor.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))){
                       // Log.d("song F", cursor.getInt(cursor.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))+"//");
                        if( cursor.getInt(cursor.getColumnIndex(FavoriteSongsProvider.FAVORITE)) == 2){
                            Log.d("song F1", cursor.getInt(cursor.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))+"//");
                            song = new Song( mListAllSong.get(i).getId(),
                                    mListAllSong.get(i).getTitle(),
                                    mListAllSong.get(i).getFile(),
                                    mListAllSong.get(i).getArtist(),
                                    mListAllSong.get(i).getDuration());
                            mListFavoriteSongs.add(song);
                         }
                     }
                }


          //      Log.d("FavoriteSong", cursor.getInt(cursor.getColumnIndex(FavoriteSongsProvider.DATA)) + "///" + cursor.getString(cursor.getColumnIndex(FavoriteSongsProvider.TITLE)));

            } while (cursor.moveToNext());

        }

         mAdapter.updateList(mListFavoriteSongs);
         setSong(mListFavoriteSongs);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getContext(), "destroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(mAdapter!=null){
            mAdapter.setSong(new ArrayList<Song>());
        }
    }
}
