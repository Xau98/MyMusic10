package com.bkav.mymusic;


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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;

public class MediaPlaybackFragment extends Fragment {

    private MediaPlaybackService mMusicService;
    private boolean mExitService = false;
    private ImageView btRepeat, btShuffle, imgBackGround,btLike, btDislike, btPrevious, btNext, btListMusic, btMore;
    private ImageButton btPlay;
    private SeekBar mSeekBar;
    private TextView mTimeStart, mTimeFinish, mArtist, mNameSong;
    private ImageView mdisk;

    public void updateUI() {
        updateTime();
        mNameSong.setText(mMusicService.getmNameSong() + "");
        mArtist.setText(mMusicService.getmArtist());
        mTimeFinish.setText(mMusicService.getDuration());
        if (!mMusicService.getmPath().equals(""))
            if (mMusicService.imageArtist(mMusicService.getmPath()) != null) {
                imgBackGround.setImageBitmap(mMusicService.imageArtist(mMusicService.getmPath()));
                mdisk.setImageBitmap(mMusicService.imageArtist(mMusicService.getmPath()));
            } else{
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
    }

    void initView(View view) {
        imgBackGround=view.findViewById(R.id.imgBackGround);
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

    }

    public void setmMusicService(MediaPlaybackService mMusicService) {
        this.mMusicService = mMusicService;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_play_back_fragment, container, false);
        initView(view);
        ((AppCompatActivity ) getActivity() ).getSupportActionBar().hide();
        mSeekBar.setMax(mMusicService.getDurationSong());
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
                Log.d("loop", mMusicService.getmLoopSong() + "");
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
                mMusicService.nextSong();

                mSeekBar.setMax(mMusicService.getDurationSong());
                updateUI();
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
                mMusicService.onCompletionSong();
                handler.postDelayed(this, 500);
            }
        }, 100);
    }


}
