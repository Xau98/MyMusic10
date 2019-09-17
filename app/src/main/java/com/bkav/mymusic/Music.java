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

public class Music {// Xóa

//    public Bitmap imageArtist(String path){
//        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
//        mediaMetadataRetriever.setDataSource(path);
//        byte [] data=mediaMetadataRetriever.getEmbeddedPicture();
//        if(data!=null){
//            return BitmapFactory.decodeByteArray(data, 0 , data.length);
//        }
//        return null;
//    }

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





