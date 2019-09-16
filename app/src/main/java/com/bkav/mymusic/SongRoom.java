package com.bkav.mymusic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Song.class}, version = 2, exportSchema = false)
public abstract class SongRoom extends RoomDatabase {

    public  abstract SongDao songDao();
    public static SongRoom Instance;

    static SongRoom getDatabase(final Context context){
        if(Instance==null){
            synchronized (SongRoom.class){
                if(Instance==null){
                 Instance=Room.databaseBuilder(context.getApplicationContext(), SongRoom.class,"song_database")
                         .fallbackToDestructiveMigration()
                         .addCallback(sRoomDatabase)
                         .build();
                }
            }
        }
        return Instance;
    }

    private static  RoomDatabase.Callback sRoomDatabase=new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new SongRoom.Database(Instance).execute();
        }
    };

private  static  class  Database extends AsyncTask<Void , Void, Void>{
private SongDao songDao;

  Database(SongRoom db){
      songDao=db.songDao();
  }

    @Override
    protected Void doInBackground(Void... voids) {
        if (songDao.getAnySong().length < 1) {
            for(int i=0;i<Music.sListMusic.size();i++){
                Log.d("oko",Music.sListMusic.get(i).getTitle());
                Song songs = new Song(Music.sListMusic.get(i).getId(),
                        Music.sListMusic.get(i).getTitle(),
                        Music.sListMusic.get(i).getFile(),
                        Music.sListMusic.get(i).getArtist(),
                        Music.sListMusic.get(i).getDuration(),
                        Music.sListMusic.get(i).getLove());
                songDao.insert(songs);
                Log.d("nameSongt",songs.getId()+"");

            }
        }
        return null;
    }
}


}
