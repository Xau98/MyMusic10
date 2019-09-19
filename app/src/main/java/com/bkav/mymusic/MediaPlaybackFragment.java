package com.bkav.mymusic;


import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;

public class MediaPlaybackFragment extends Fragment {

    private MediaPlaybackService mMusicService;
    private boolean mExitService = false;
    private ImageButton btLike, btDislike, btPlay, btPrevious, btNext, btRepeat, btShuffle, btMore, btListMusic;
    private SeekBar mSeekBar;
    private TextView mTimeStart, mTimeFinish, mArtist, mNameSong;
    private ImageView mdisk;


   public void updateUI() {
        updateTime();
        mNameSong.setText(mMusicService.getNameSong() + "");
        mArtist.setText(mMusicService.getArtist());
        mTimeFinish.setText(mMusicService.getDuration());
        if (!mMusicService.getLink().equals(""))
            if (mMusicService.imageArtist(mMusicService.getLink()) != null) {
                mdisk.setImageBitmap(mMusicService.imageArtist(mMusicService.getLink()));
            } else
                mdisk.setImageResource(R.drawable.default_cover_art);

        if (mMusicService.isPlaying()) {
            btPlay.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_50dp);

        } else {
            btPlay.setBackgroundResource(R.drawable.ic_play_circle_filled_black_50dp);
        }

        if (mMusicService.isShuffleSong()) {
            btShuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
        } else
            btShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_50dp);

        if (mMusicService.getLoopSong() == 0) {
            btRepeat.setBackgroundResource(R.drawable.ic_repeat_white_24dp);
        } else {
            if (mMusicService.getLoopSong() == -1) {
                btRepeat.setBackgroundResource(R.drawable.ic_repeat_yellow_24dp);
            } else
                btRepeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow_24dp);
        }
    }

    void initView(View view) {
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
    }

    public void setmMusicService(MediaPlaybackService mMusicService) {
        this.mMusicService = mMusicService;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_play_back_fragment, container, false);
        initView(view);
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
                Log.d("loop",mMusicService.getLoopSong()+"" );
                if (mMusicService.getLoopSong() == 0) {
                    mMusicService.setLoopSong(-1);
                    btRepeat.setBackgroundResource(R.drawable.ic_repeat_yellow_24dp);
                } else {
                    if (mMusicService.getLoopSong() == 1) {
                        mMusicService.setLoopSong(0);
                        btRepeat.setBackgroundResource(R.drawable.ic_repeat_white_24dp);
                    } else {
                        mMusicService.setLoopSong(1);
                        btRepeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow_24dp);
                    }
                }
            }
        });

        btShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMusicService.isShuffleSong()) {
                    mMusicService.setShuffleSong(false);
                     btShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_50dp);
                } else {
                    mMusicService.setShuffleSong(true);
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
