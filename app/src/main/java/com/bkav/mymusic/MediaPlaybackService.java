package com.bkav.mymusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
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
import android.os.Build;
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
    private MediaPlayer mMediaPlayer = null;
    private Listenner mListenner;
    private String mPath = "";
    private String mArtist = "";
    private String mNameSong = "";
    private int mPositionCurrent = 0;
    private int mLoopSong = 0;// mLoopSong =0 (ko lap)// mLoopSong=-1 (lap ds) //mLoopSong =1 (lap 1)
    private boolean mShuffleSong = false;
    private List<Song> mListAllSong = new ArrayList<>();
    private String SHARED_PREFERENCES_NAME = "com.bkav.mymusic";
    private SharedPreferences mSharePreferences;
    private  ConnectSeviceFragmentInterface mConnectSeviceFragment2;
    @Override
    public void onCreate() {
        super.onCreate();
        mSharePreferences =   getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);// move Service
        mPositionCurrent = mSharePreferences.getInt("position", 3);
        mNameSong=mSharePreferences.getString("nameSong", "Name Song");
        mArtist=mSharePreferences.getString("nameArtist", "Name Artist");
        mPath=mSharePreferences.getString("path", "");

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
                    if (mMediaPlayer.isPlaying()) {
                        pauseSong();
                    } else {
                        playingSong();
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void getListenner(Listenner listenner) {
        this.mListenner = listenner;
    }

    public void getListenner2(ConnectSeviceFragmentInterface listenner2) {
        this.mConnectSeviceFragment2 =listenner2;
    }

    public String getmNameSong() {
        return mNameSong;
    }

    public MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }

    public String getmPath() {
        return mPath;
    }

    public String getmArtist() {
        return mArtist;
    }

    public int getmLoopSong() {
        return mLoopSong;
    }

    public void setmLoopSong(int mLoopSong) {
        this.mLoopSong = mLoopSong;
    }

    public boolean ismShuffleSong() {
        return mShuffleSong;
    }

    public void setmShuffleSong(boolean mShuffleSong) {
        this.mShuffleSong = mShuffleSong;
    }

    public void setmListAllSong(List<Song> mListAllSong) {
        this.mListAllSong = mListAllSong;
    }

    public List<Song> getmListAllSong() {
        return mListAllSong;
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
        builder.setCustomContentView(mCustomContentView);
        builder.setCustomBigContentView(mCustomBigContentView);
        builder.setContentIntent(pendingIntent);
        mCustomBigContentView.setTextViewText(R.id.textSongName, nameSong);
        mCustomBigContentView.setTextViewText(R.id.textNameSonger, artist);
        mCustomBigContentView.setImageViewResource(R.id.btnPlay, isMusicPlay() ? isPlaying() ? R.drawable.ic_pause_circle_filled_black_50dp : R.drawable.ic_play_circle_filled_black_50dp : R.drawable.ic_play_circle_filled_black_50dp);
        mCustomBigContentView.setOnClickPendingIntent(R.id.btnPrevious, previousPendingIntent);
        mCustomBigContentView.setOnClickPendingIntent(R.id.btnPlay, playPendingIntent);
        mCustomBigContentView.setOnClickPendingIntent(R.id.btnNext, nextPendingIntent);
        if (imageArtist(path) != null) {
            mCustomBigContentView.setImageViewBitmap(R.id.img, imageArtist(path));
        } else
            mCustomBigContentView.setImageViewResource(R.id.img, R.drawable.default_cover_art);
/////========
        mCustomContentView.setImageViewResource(R.id.btnPlay, isMusicPlay() ? isPlaying() ? R.drawable.ic_pause_circle_filled_black_50dp : R.drawable.ic_play_circle_filled_black_50dp : R.drawable.ic_play_circle_filled_black_50dp);
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
        if (mMediaPlayer.isPlaying())
            return true;
        else
            return false;
    }

    public void seekToSong(int getProgress) {
        mMediaPlayer.seekTo(getProgress);
    }

    public int getmPosition() {
        return mPositionCurrent;
    }

    public void setmPosition(int mPosition) {
        this.mPositionCurrent = mPosition;
    }

    public int getCurrentPositionSong() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDurationSong() {
        return mMediaPlayer.getDuration();
    }

    public void playSong(int mPositionCurrent) {
        mMediaPlayer = new MediaPlayer();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        try {
            Log.d("play song", mPositionCurrent + "//" + mListAllSong.size());
            for (int i = 0; i <= mListAllSong.size() - 1; i++) {
                if (mListAllSong.get(i).getId() == mPositionCurrent) {
                    Log.d("mPath", mListAllSong.get(i).getFile());
                    Uri content_uri = Uri.parse(mListAllSong.get(i).getFile());
                    mMediaPlayer.setDataSource(getApplicationContext(), content_uri);
                    mMediaPlayer.prepare();
                    mMediaPlayer.setWakeMode(getApplicationContext(),
                            PowerManager.PARTIAL_WAKE_LOCK);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.start();
                    mPath = mListAllSong.get(i).getFile();
                    mNameSong = mListAllSong.get(i).getTitle();
                    mArtist = mListAllSong.get(i).getArtist();
                    showNotification(mListAllSong.get(i).getTitle(), mListAllSong.get(i).getArtist(), mPath);
                    mListenner.onItemListenner();
                    mConnectSeviceFragment2.onActionConnectSeviceFragment();
                    //==

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ///SharedPreferences
        mSharePreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharePreferences.edit();
        editor.putInt("position", getmPosition());
        editor.putString("nameSong", getmNameSong());
        editor.putString("nameArtist", getmArtist());
        editor.putString("path", mPath);
        editor.putInt("duration", getDurationSong());
        editor.commit();
    }

    public void playingSong() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
        if (mListenner != null) {
            mListenner.onItemListenner();
        }
        showNotification(mNameSong, mArtist, mPath);
    }

    public void pauseSong() {
        mMediaPlayer.pause();
        if (mListenner != null) {
            mListenner.onItemListenner();
        }
        showNotification(mNameSong, mArtist, mPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH);
        }
    }

    public void previousSong() {
        mMediaPlayer.pause();
        if (getCurrentPositionSong() <= 3000) {
            if (mShuffleSong == true) {
                mPositionCurrent = actionShuffleSong();
            } else {
                if (mPositionCurrent == 0) {
                    mPositionCurrent = mListAllSong.size() - 1;
                } else
                    mPositionCurrent--;
            }
            playSong(mPositionCurrent);
            mListenner.actionNotification();
        } else {
            playSong(mPositionCurrent);
        }
    }

    public void nextSong() {
        mMediaPlayer.pause();
        if (mShuffleSong == true) {
            mPositionCurrent = actionShuffleSong();
        } else {
            if (mPositionCurrent == mListAllSong.size() - 1)
                mPositionCurrent = 0;
            else
                mPositionCurrent++;
        }
        mListenner.onItemListenner();
        playSong(mPositionCurrent);
        mListenner.actionNotification();

    }

    public int actionShuffleSong() {
        Random rd = new Random();
        int result = rd.nextInt(mListAllSong.size() - 1);
        return result;
    }

    public String getDuration() {
        SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
        return formmatTime.format(mMediaPlayer.getDuration());
    }

    public boolean isMusicPlay() {
        if (mMediaPlayer != null)
            return true;
        return false;
    }

    public void UpdateTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            getmMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer media) {
                      onCompletionSong();
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    public void onCompletionSong() {
        mMediaPlayer.pause();
        if (mLoopSong == 0) {
            if (mPositionCurrent < mListAllSong.size() - 1)
                mPositionCurrent++;
        } else {
            if (mLoopSong == -1) {
                if (mPositionCurrent == mListAllSong.size() - 1) {
                    mPositionCurrent = 0;
                } else {
                    mPositionCurrent++;
                }
            }
        }
        playSong(mPositionCurrent);
        mListenner.actionNotification();
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

    public interface  ConnectSeviceFragmentInterface{
        void  onActionConnectSeviceFragment();
    }

    class MusicBinder extends Binder {
        public MediaPlaybackService getMusicBinder() {
            return MediaPlaybackService.this;
        }
    }
}
