package com.bkav.mymusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MediaPlaybackService extends Service {
    private static final String NOTIFICATION_CHANNEL_ID = "1";
    public static final String ACTION_PERVIOUS = "xxx.yyy.zzz.ACTION_PERVIOUS";
    public static final String ACTION_PLAY = "xxx.yyy.zzz.ACTION_PLAY";
    public static final String ACTION_NEXT = "xxx.yyy.zzz.ACTION_NEXT";
    private Binder binder = new MusicBinder();
    private MediaPlayer sMediaPlayer = null;// sua static
    private Listenner listenner;
    private String link = "";
    private String artist = "";
    private String nameSong = "";
    private int mPositionCurrent = 0;
    private int loopSong = 0;// loopSong =0 (ko lap)// loopSong=-1 (lap ds) //loopSong =1 (lap 1)
    private boolean shuffleSong = false;
    private List<Song> mListAllSong= new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private  String SHARED_PREFERENCES_NAME="com.bkav.mymusic";
    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isMusicPlay()) {
            Log.d("getAction", intent.getAction() + "");
            switch (intent.getAction()) {
                case ACTION_PERVIOUS:
                    previousSong();
                    break;
                case ACTION_NEXT:
                    nextSong();
                    break;
                case ACTION_PLAY:
                    if (sMediaPlayer.isPlaying()) {
                        pauseSong();
                    } else {
                        playingSong();
                    }
                    break;
            }
        }
        // showNotification(nameSong, artist, link);
        return super.onStartCommand(intent, flags, startId);
    }

    public void getListenner(Listenner listenner) {
        this.listenner = listenner;
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

    public void setLink(String link) {
        this.link = link;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public int getLoopSong() {
        return loopSong;
    }

    public void setLoopSong(int loopSong) {
        this.loopSong = loopSong;
    }

    public boolean isShuffleSong() {
        return shuffleSong;
    }

    public void setShuffleSong(boolean shuffleSong) {
        this.shuffleSong = shuffleSong;
    }

    public void setmListAllSong(List<Song> mListAllSong) {
        this.mListAllSong = mListAllSong;
    }

    public void showNotification(String nameSong, String artist, String path) {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, ActivityMusic.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(ACTION_PERVIOUS);
        PendingIntent previousPendingIntent = null;

        Intent playIntent = new Intent(ACTION_PLAY);
        PendingIntent playPendingIntent = null;

        Intent nextIntent = new Intent(ACTION_NEXT);
        PendingIntent nextPendingIntent = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            previousPendingIntent = PendingIntent.getForegroundService(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            playPendingIntent = PendingIntent.getService(getApplicationContext(), 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nextPendingIntent = PendingIntent.getService(getApplicationContext(), 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        RemoteViews mCustomContentView = new RemoteViews(getPackageName(), R.layout.sub_notification);
        RemoteViews mCustomBigContentView = new RemoteViews(getPackageName(), R.layout.notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_music_note_black_24dp);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        builder.setCustomContentView(mCustomContentView);
        builder.setCustomBigContentView(mCustomBigContentView);
        builder.setContentIntent(pendingIntent);
        mCustomBigContentView.setTextViewText(R.id.textSongName, nameSong);
        mCustomBigContentView.setTextViewText(R.id.textNameSonger, artist);
        mCustomBigContentView.setImageViewResource(R.id.btnPlay, isMusicPlay() ? isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_play_arrow_black_24dp);
        mCustomBigContentView.setOnClickPendingIntent(R.id.btnPrevious, previousPendingIntent);
        mCustomBigContentView.setOnClickPendingIntent(R.id.btnPlay, playPendingIntent);
        mCustomBigContentView.setOnClickPendingIntent(R.id.btnNext, nextPendingIntent);
        if (imageArtist(path) != null) {
            mCustomBigContentView.setImageViewBitmap(R.id.img, imageArtist(path));
        } else
            mCustomBigContentView.setImageViewResource(R.id.img, R.drawable.default_cover_art);
/////========
        mCustomContentView.setImageViewResource(R.id.btnPlay, isMusicPlay() ? isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_play_arrow_black_24dp);
        mCustomContentView.setOnClickPendingIntent(R.id.btnPrevious, previousPendingIntent);
        mCustomContentView.setOnClickPendingIntent(R.id.btnPlay, playPendingIntent);
        mCustomContentView.setOnClickPendingIntent(R.id.btnNext, nextPendingIntent);
        if (imageArtist(path) != null) {
            mCustomContentView.setImageViewBitmap(R.id.img, imageArtist(path));
        } else
            mCustomContentView.setImageViewResource(R.id.img, R.drawable.default_cover_art);
        startForeground(1, builder.build());
    }

    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel musicServiceChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Music Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            musicServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(musicServiceChannel);
        }
    }

    public boolean isPlaying() {
        if (sMediaPlayer.isPlaying())
            return true;
        else
            return false;
    }

    public void seekToSong(int getProgress) {
        sMediaPlayer.seekTo(getProgress);
    }

    public int getmPosition() {
        return mPositionCurrent;
    }

    public void setmPosition(int mPosition) {
        this.mPositionCurrent = mPosition;
    }

    public int getCurrentPositionSong() {
        return sMediaPlayer.getCurrentPosition();
    }

    public int getDurationSong() {
        return sMediaPlayer.getDuration();
    }

    public void playSong(int mPositionCurrent) {
     Log.d("play song", mPositionCurrent+"//");
        Uri content_uri = Uri.parse(mListAllSong.get(mPositionCurrent).getFile());
        sMediaPlayer = new MediaPlayer();
        if (sMediaPlayer.isPlaying()) {
            sMediaPlayer.pause();
        }
        try {
            sMediaPlayer.setDataSource(getApplicationContext(), content_uri);
            sMediaPlayer.prepare();
            sMediaPlayer.setWakeMode(getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);
            sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            listenner.onItemListenner();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sMediaPlayer.start();
        link = mListAllSong.get(mPositionCurrent).getFile();
        nameSong = mListAllSong.get(mPositionCurrent).getTitle();
        artist = mListAllSong.get(mPositionCurrent).getArtist();
        showNotification(mListAllSong.get(mPositionCurrent).getTitle(), mListAllSong.get(mPositionCurrent).getArtist(), link);

        ///SharedPreferences
        mSharedPreferences= getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putInt("position", getmPosition());
        editor.putString("nameSong", getNameSong());
         editor.putString("nameArtist" , getArtist());
         editor.putString("path", link);
        editor.commit();
    }

    public void playingSong() {
        if (sMediaPlayer.isPlaying()) {
            sMediaPlayer.pause();
        } else {
            sMediaPlayer.start();
        }
        if (listenner != null) {
            listenner.onItemListenner();
        }
        showNotification(nameSong, artist, link);
    }

    public void pauseSong() {
        sMediaPlayer.pause();
        if (listenner != null) {
            listenner.onItemListenner();
        }
        showNotification(nameSong, artist, link);
    }

    public void previousSong() {
        sMediaPlayer.pause();
        if (getCurrentPositionSong() <= 3000) {
            if (shuffleSong == true) {
                mPositionCurrent = actionShuffleSong();
            } else {
                if (mPositionCurrent == 0) {
                    mPositionCurrent = mListAllSong.size() - 1;
                } else
                    mPositionCurrent--;
            }
            playSong(mPositionCurrent);
            listenner.actionNotification();
        }
        else {
            playSong(mPositionCurrent);
        }
    }

    public void nextSong() {
        sMediaPlayer.pause();

        if (shuffleSong == true) {
            mPositionCurrent = actionShuffleSong();
        } else {
            if (mPositionCurrent == mListAllSong.size() - 1)
                mPositionCurrent = 0;
            else
                mPositionCurrent++;
        }
        playSong(mPositionCurrent);
        listenner.actionNotification();
    }

    public int actionShuffleSong() {
        Random rd = new Random();
        int result = rd.nextInt(mListAllSong.size() - 1);
        return result;
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

    public void UpdateTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    public void onCompletionSong() {
        sMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer media) {
                sMediaPlayer.pause();
                if (loopSong == 0) {
                    if (mPositionCurrent < mListAllSong.size() - 1)
                        mPositionCurrent++;
                } else {
                    if (loopSong == -1) {
                        if (mPositionCurrent == mListAllSong.size() - 1) {
                            mPositionCurrent = 0;
                        } else {
                            mPositionCurrent++;
                        }
                    }
                }
                playSong(mPositionCurrent);
                listenner.actionNotification();
            }
        });
    }

    public Bitmap imageArtist(String path) {
        Log.d("path", path + "//");
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
        if (data != null) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public interface Listenner {
        void onItemListenner();

        void actionNotification();

    }

    class MusicBinder extends Binder {
        public MediaPlaybackService getMusicBinder() {
            return MediaPlaybackService.this;
        }
    }
}
