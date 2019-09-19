package com.bkav.mymusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
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

public class MediaPlaybackFragment extends Fragment {

    private MediaPlaybackService mMusicService;
    private boolean mExitService = false;
    private ImageButton btLike, btDislike, btPlay, btPrevious, btNext, btRepeat, btShuffle, btMore, btListMusic;
    private SeekBar seekBar;
    private TextView mTimeStart, mTimeFinish, mArtist, mNameSong;
    private ImageView mdisk;


    void updateUI() {
        mNameSong.setText(mMusicService.getNameSong());
        mArtist.setText(mMusicService.getArtist());
        if (mMusicService.isPlaying()) {
            btPlay.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_50dp);

        } else {
            btPlay.setBackgroundResource(R.drawable.ic_play_circle_filled_black_50dp);
        }
    }

    void initView(View view) {
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

        btListMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            AllSongsFragment mAllSongsFragment = new AllSongsFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ok", mMusicService+"./");
            }
        });


        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMusicService.nextSong();
                updateUI();
            }
        });
        return view;
    }


}
