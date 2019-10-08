package com.bkav.mymusic;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;


public class MediaPlaybackFragment extends Fragment {

    private MediaPlaybackService mMusicService;
    private boolean mExitService = false;
    private ImageView btRepeat, btShuffle, imgBackGround, btLike, btDislike, btPrevious, btNext, btListMusic, btMore;
    private ImageButton btPlay;
    private SeekBar mSeekBar;
    private TextView mTimeStart, mTimeFinish, mArtist, mNameSong;
    private ImageView mdisk;
    private String SHARED_PREFERENCES_NAME = "com.bkav.mymusic";
    private SharedPreferences mSharePreferences;
    private  String mURL = "content://com.bkav.provider";
    private Uri mURISong= Uri.parse(mURL);

//    public ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) iBinder;
//            mMusicService = binder.getMusicBinder();
//            updateUI();
//            mMusicService.getListenner(new MediaPlaybackService.Listenner() {
//                @Override
//                public void onItemListenner() {
//                    updateUI();
//                }
//
//                @Override
//                public void actionNotification() {
//                    updateUI();
//                }
//
//            });
//            mExitService = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mExitService = false;
//        }
//    };

//    @Override
//    public void onStart() {
//        super.onStart();
//        Intent it = new Intent(getActivity(), MediaPlaybackService.class);
//        getActivity().bindService(it, mServiceConnection, 0);
//    }

    public void updateUI() {
        if (mMusicService != null && mSeekBar != null) {
            if (mMusicService.isMusicPlay()) {
               // Log.d("servu", "onItemListenner: "+mMusicService);
                updateTime();
                mSeekBar.setMax(mMusicService.getDurationSong());
                mNameSong.setText(mMusicService.getmNameSong() + "");
                mArtist.setText(mMusicService.getmArtist());
                mTimeFinish.setText(mMusicService.getDuration());
                if (!mMusicService.getmPath().equals(""))
                    if (mMusicService.imageArtist(mMusicService.getmPath()) != null) {
                        imgBackGround.setImageBitmap(mMusicService.imageArtist(mMusicService.getmPath()));
                        mdisk.setImageBitmap(mMusicService.imageArtist(mMusicService.getmPath()));
                    } else {
                        imgBackGround.setImageResource(R.drawable.default_cover_art);
                        mdisk.setImageResource(R.drawable.default_cover_art);
                    }
                if (mMusicService.isPlaying()) {
                    btPlay.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_50dp);

                } else {
                    btPlay.setBackgroundResource(R.drawable.ic_play_circle_filled_black_50dp);
                }

                if (mMusicService.ismShuffleSong()) {
                    btShuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
                } else
                    btShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_50dp);

                if (mMusicService.getmLoopSong() == 0) {
                    btRepeat.setBackgroundResource(R.drawable.ic_repeat_white_24dp);
                } else {
                    if (mMusicService.getmLoopSong() == -1) {
                        btRepeat.setBackgroundResource(R.drawable.ic_repeat_yellow_24dp);
                    } else
                        btRepeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow_24dp);
                }

                if(mMusicService.actionLike()==0){
                            btLike.setImageResource(R.drawable.ic_like);
                            btDislike.setImageResource(R.drawable.ic_dislike);
                        }
                        if(mMusicService.actionLike()==1){
                            btLike.setImageResource(R.drawable.ic_like);
                            btDislike.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                        }
                        if(mMusicService.actionLike()==2){
                            btLike.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                            btDislike.setImageResource(R.drawable.ic_dislike);
                        }
                //===== buttton like/ 0: not like , not dislike // 1: dislike , not like /// 2: like , not dislike
//                String selection=" id_provider ="+mMusicService.getmPosition();
//                Log.d("ok","loi"+selection);
//                Cursor c = getActivity().getContentResolver().query(mURISong, null, selection, null, null);
//                Log.d("ok","loi"+selection+"=="+c);
//                if(c.moveToFirst()&& c!=null){
//                    do{
//                        if(c.getInt(c.getColumnIndex(FavoriteSongsProvider.FAVORITE))==0){
//                            btLike.setImageResource(R.drawable.ic_like);
//                            btDislike.setImageResource(R.drawable.ic_dislike);
//                        }
//                        if(c.getInt(c.getColumnIndex(FavoriteSongsProvider.FAVORITE))==1){
//                            btLike.setImageResource(R.drawable.ic_like);
//                            btDislike.setImageResource(R.drawable.ic_thumb_down_black_24dp);
//                        }
//                        if(c.getInt(c.getColumnIndex(FavoriteSongsProvider.FAVORITE))==2){
//                            btLike.setImageResource(R.drawable.ic_thumb_up_black_24dp);
//                            btDislike.setImageResource(R.drawable.ic_dislike);
//                        }
//                    }while(c.moveToNext());
//                }

            } else {
                mMusicService.setmPosition(mMusicService.getmPosition());
                mMusicService.playSong(mMusicService.getmPosition());
                mMusicService.pauseSong();
                updateUI();

            }

      }
    }

    void initView(View view) {
        imgBackGround = view.findViewById(R.id.imgBackGround);
        mNameSong = view.findViewById(R.id.namesong);
        mArtist = view.findViewById(R.id.nameArtist);
        mTimeFinish = view.findViewById(R.id.finishTime);
        mTimeStart = view.findViewById(R.id.starttime);
        mdisk = view.findViewById(R.id.disk);
        mSeekBar = view.findViewById(R.id.seekbar);
        btPlay = view.findViewById(R.id.Play);
        btLike = view.findViewById(R.id.like);
        btDislike = view.findViewById(R.id.dislike);
        btListMusic = view.findViewById(R.id.listMusic);
        btMore = view.findViewById(R.id.more);
        btNext = view.findViewById(R.id.next);
        btPrevious = view.findViewById(R.id.previous);
        btRepeat = view.findViewById(R.id.repeat);
        btShuffle = view.findViewById(R.id.shuffle);
        mNameSong.setSelected(true);
        if (getActivity().findViewById(R.id.frament2) != null) {
            btListMusic.setVisibility(View.GONE);
            imgBackGround.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imgBackGround.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public void setmMusicService(final MediaPlaybackService mMusicService) {
        this.mMusicService = mMusicService;
        mMusicService.getListenner2(new MediaPlaybackService.ConnectSeviceFragmentInterface() {
            @Override
            public void onActionConnectSeviceFragment() {
                updateUI();
            }
        });
       updateUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_play_back_fragment, container, false);
        initView(view);
        if (getActivity().findViewById(R.id.frament2) != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } else
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMusicService.seekToSong(mSeekBar.getProgress());
            }
        });


        btListMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        btRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMusicService.getmLoopSong() == 0) {
                    mMusicService.setmLoopSong(-1);
                    btRepeat.setBackgroundResource(R.drawable.ic_repeat_yellow_24dp);
                } else {
                    if (mMusicService.getmLoopSong() == 1) {
                        mMusicService.setmLoopSong(0);
                        btRepeat.setBackgroundResource(R.drawable.ic_repeat_white_24dp);
                    } else {
                        mMusicService.setmLoopSong(1);
                        btRepeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow_24dp);
                    }
                }
                mSharePreferences =  getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharePreferences.edit();
                editor.putInt("mLoopSong", mMusicService.getmLoopSong());
                editor.commit();
            }
        });

        btShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMusicService.ismShuffleSong()) {
                    mMusicService.setmShuffleSong(false);
                    btShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_50dp);
                } else {
                    mMusicService.setmShuffleSong(true);
                    btShuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
                }
                mSharePreferences =  getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharePreferences.edit();
                editor.putBoolean("mShuffleSong",mMusicService.ismShuffleSong());
                editor.commit();
            }
        });

        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMusicService.previousSong();
                mSeekBar.setMax(mMusicService.getDurationSong());
                updateUI();
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMusicService.playingSong();
                updateUI();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMusicService != null) {
                    mMusicService.nextSong();
                    mSeekBar.setMax(mMusicService.getDurationSong());
                    updateUI();
                }
            }
        });

        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                if(mMusicService.actionLike()==0||mMusicService.actionLike()==1)
                values.put(FavoriteSongsProvider.FAVORITE, 2);
                if(mMusicService.actionLike()==2)
                    values.put(FavoriteSongsProvider.FAVORITE, 0);
                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + mMusicService.getmPosition(), null);
                 Toast.makeText(getContext(), "like song //" + mMusicService.getmNameSong(), Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });

        btDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                if(mMusicService.actionLike()==0||mMusicService.actionLike()==2)
                    values.put(FavoriteSongsProvider.FAVORITE, 1);
                if(mMusicService.actionLike()==1)
                    values.put(FavoriteSongsProvider.FAVORITE, 0);
                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + mMusicService.getmPosition(), null);
                Toast.makeText(getContext(), "dislike song //" + mMusicService.getmNameSong(), Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });

        btMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

       updateUI();
        return view;
    }

    public void updateTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
                mTimeStart.setText(formmatTime.format(mMusicService.getCurrentPositionSong()));
                mSeekBar.setProgress(mMusicService.getCurrentPositionSong());
                mMusicService.getmMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer media) {
                        mMusicService.onCompletionSong();
                        updateUI();
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }


}
