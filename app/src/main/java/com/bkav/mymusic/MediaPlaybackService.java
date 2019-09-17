package com.bkav.mymusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class MediaPlaybackService extends Service {
    private Binder binder = new MusicBinder();
    MediaPlayer sMediaPlayer = null;
    private Listenner listenner;
    private String link = "";
    private String artist = "";
    private String nameSong = "";
    private int mPosition = 0;
    private int loop;

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel musicServiceChannel = new NotificationChannel(
                    "0",
                    "Music Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            musicServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(musicServiceChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isMusicPlay()) {
            Log.d("getAction", intent.getAction() + "");
            switch (intent.getAction()) {
                case "Previous":
                    previousSong();
                    break;
                case "Next":
                    nextSong();
                    break;
                case "Play":
                    if (sMediaPlayer.isPlaying()) {
                        pauseSong();
                    } else {
                        playingSong();
                    }
                    break;
            }
        }
        // showNotification(nameSong,artist);
        return START_NOT_STICKY;
    }

    public void getListenner(Listenner listenner) {
        this.listenner=listenner;
    }

    public String getNameSong() {
        return nameSong;
    }

    public String getLink() {
        return link;
    }

    public String getArtist() {
        return artist;
    }

    public void showNotification(String nameSong, String artist) {
        Intent notificationIntent = new Intent(this, ActivityMusic.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, MediaPlaybackService.class);
        previousIntent.setAction("Previous");
        PendingIntent previousPendingIntent = null;

        Intent playIntent = new Intent(this, MediaPlaybackService.class);
        playIntent.setAction("Play");
        PendingIntent playPendingIntent = null;

        Intent nextIntent = new Intent(this, MediaPlaybackService.class);
        nextIntent.setAction("Next");
        PendingIntent nextPendingIntent = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            previousPendingIntent = PendingIntent.getForegroundService(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            playPendingIntent = PendingIntent.getForegroundService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nextPendingIntent = PendingIntent.getForegroundService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        Bitmap largeImage = BitmapFactory.decodeResource(getResources(), R.drawable.icon_disk2);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "0")
                .setSmallIcon(R.drawable.icon_disk2)
                .setContentTitle(nameSong)
                .setContentText(artist)
                .setLargeIcon(largeImage)
                .addAction(R.drawable.ic_previous_black_24dp, "previous", previousPendingIntent)
            .addAction(isMusicPlay()?isPlaying() ? R.drawable.ic_play_arrow_black_24dp:R.drawable.ic_pause : R.drawable.ic_pause, "play", playPendingIntent)
                .addAction(R.drawable.ic_skip_next_black_24dp, "next", nextPendingIntent)

                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

    }

    public boolean isPlaying() {
        if (sMediaPlayer.isPlaying())
            return true;
        else
            return false;
    }

     public  int getDurationSong(){
        return  sMediaPlayer.getDuration();
     }

     public void seekToSong(int getProgress){
        sMediaPlayer.seekTo(getProgress);
     }


    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public void playSong(String path) {
        Uri content_uri = Uri.parse(path);
        sMediaPlayer = new MediaPlayer();

        try {
            sMediaPlayer.setDataSource(getApplicationContext(), content_uri);
            sMediaPlayer.prepare();
            sMediaPlayer.setWakeMode(getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);
            sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.Media.TITLE};
            Cursor c = getApplication().getContentResolver().query(uri, projection, null, null, null);
            if (c != null) {
                while (c.moveToNext()) {
                    link = c.getString(0);
                    if (link.equals(path)) {
                        artist = c.getString(1);
                        nameSong = c.getString(2);
                    }
                }
                c.close();
            }
            //showNotification(nameSong, artist);
            listenner.onItemListenner();
        } catch (IOException e) {
            e.printStackTrace();
        }
        link=path;
    }

    public void playingSong() {
        sMediaPlayer.start();
        if (listenner != null) {
            listenner.onItemListenner();
        }
        showNotification(nameSong, artist);
    }

    public void pauseSong() {
        sMediaPlayer.pause();
        if (listenner != null) {
            listenner.onItemListenner();
        }
        showNotification(nameSong, artist);
    }

    void previousSong() {
        listenner.actionPrevious();
    }

    public  void nextSong(){
        listenner.actionNext();
    }

    public String getDuration() {
        SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
        return formmatTime.format(sMediaPlayer.getDuration());
    }

    public boolean isMusicPlay() {
        if (sMediaPlayer != null)
            return true;
        return false;
    }

    void UpdateTime(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer media) {


                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public interface Listenner {
        void onItemListenner();
        void  actionPrevious();
        void actionNext();
    }

    class MusicBinder extends Binder {
        public MediaPlaybackService getMusicBinder()
        {
            return MediaPlaybackService.this;
        }
    }
}
