package com.bkav.mymusic;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class Music {

     static ArrayList<Song> sListMusic;
    //static int sPosition = 0;// id cua bai hien tai

    ArrayList<Song> Addsong(Context context){
        sListMusic=new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.Media.TITLE,  MediaStore.Audio.Media.DURATION};
        Cursor c = context.getContentResolver().query(uri, projection, null,null, null);
       int id=0;
        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                String album = c.getString(1);
                String artist = c.getString(2);
                String name =c.getString(3);
                String duration =c.getString(4);
                //duration
                sListMusic.add(new Song(id, name, path, artist, Integer.parseInt(duration) ,0));
                id++ ;
                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist + " Duration "+duration);
            }
            c.close();
        }
        return sListMusic;
    }

    public Bitmap imageArtist(String path){
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte [] data=mediaMetadataRetriever.getEmbeddedPicture();
        if(data!=null){
            return BitmapFactory.decodeByteArray(data, 0 , data.length);
        }
        return null;
    }

   static void checkLoopSong(int index){//index = getId
//        int loop=-1;
//        for (int i=0;i<MainActivity.listRecently.size();i++){
//            int dem=Integer.parseInt(MainActivity.listRecently.get(i).getId()+"");
//            if(index==dem){
//                loop=i;
//            }
//        }
//        if(loop==-1){
//            MainActivity.listRecently.add(new song(index,
//                    Music.sListMusic.get(index).getTitle(),
//                    Music.sListMusic.get(index).getFile(),
//                    Music.sListMusic.get(index).getArtist(),
//                    Music.sListMusic.get(index).getLove()));
//        }
//        else {
//            Log.d("remove", Music.sListMusic.get(index).getTitle()+"");
//            MainActivity.listRecently.remove(loop);
//            MainActivity.listRecently.add(new song(index,
//                    Music.sListMusic.get(index).getTitle(),
//                    Music.sListMusic.get(index).getFile(),
//                    Music.sListMusic.get(index).getArtist(),
//                    Music.sListMusic.get(index).getLove()));
//        }
    }


}





