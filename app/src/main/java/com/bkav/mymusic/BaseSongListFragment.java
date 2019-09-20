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
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BaseSongListFragment extends Fragment implements MusicAdapter.OnClickItemView {

    private RecyclerView mRecyclerView;
    private MusicAdapter mAdapter;
    private ImageButton mClickPlay;
    private TextView mNameSong, mArtist;
    private ImageView mdisk;
    private ConstraintLayout constraintLayout;
    protected MediaPlaybackService mMusicService;
    private boolean mExitService = false;
    private int mPosition = 0;
    private List<Song> songs;
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) iBinder;
            mMusicService = binder.getMusicBinder();
            mAdapter.setmMusicService(mMusicService);
            mMusicService.setmListAllSong(songs);

            mMusicService.getListenner(new MediaPlaybackService.Listenner() {// thua onclick
                @Override
                public void onItemListenner() {
                    updateUI();
                }

                @Override
                public void actionNotification() {
                    updateUI();
                }

            });
            // updateUI();
            Log.d("log", "879");
            mExitService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mExitService = false;
//            SharedPreferences.Editor editor = mSharedPreferences.edit();
//            editor.putInt("play", mPosition);
//            editor.putString("nameSong",mNameSong.getText()+"");
//            editor.apply();
        }
    };

    public void setSong(List<Song> songs) {
        this.songs = songs;
        mAdapter.setSong(songs);
    }

    void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mClickPlay = view.findViewById(R.id.play);
        mArtist = view.findViewById(R.id.Artist);
        mdisk = view.findViewById(R.id.disk);
        mNameSong = view.findViewById(R.id.namePlaySong);
        constraintLayout = view.findViewById(R.id.constraintLayout);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new MusicAdapter(this, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = new Intent(getActivity(), MediaPlaybackService.class);
        getActivity().bindService(it, mServiceConnection, 0);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_songs_fragment, container, false);
        initView(view);

//        if(mMusicService!=null){
//            Log.d("log tb", mMusicService.getNameSong());
//            updateUI();
//        }

        mClickPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMusicService!=null) {
                    if (mMusicService.isPlaying()) {
                        mMusicService.pauseSong();
                    } else {
                        mMusicService.playingSong();
                    }
                    updateUI();
                }
                else {

                }
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
                mMediaPlaybackFragment.setmMusicService(mMusicService);
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.framentContent, mMediaPlaybackFragment).commit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        while(mMusicService==null) {
//            Intent it = new Intent(getActivity(), MediaPlaybackService.class);
//            getActivity().bindService(it, mServiceConnection, 0);
//            Log.d("service3", mMusicService + "//");
//        }
    }
//    void updateSong() {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mMusicService.sMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer media) {
//                        mMusicService.pauseSong();
//                        mPosition =  mPosition + 1;
//                        if ( mPosition > Music.sListMusic.size() - 1) {
//                            mPosition = 0;
//                        }
//                        playingSong( mPosition);
//                    }
//                });
//                handler.postDelayed(this, 500);
//            }
//        }, 100);
//    }
//
//    public void playingSong(final int position) {
//        for (int i = 0; i < mSongViewModel.getmSong().getValue().size(); i++) {
//            if (position == mSongViewModel.getmSong().getValue().get(i).getId()) {
//                String path = mSongViewModel.getmSong().getValue().get(i).getFile();
//                if (mMusicService.sMediaPlayer.isPlaying()) {
//                    mMusicService.pauseSong();
//                }
//                mMusicService.playSong(path);
//                mMusicService.playingSong();
//                Music.checkLoopSong(position);
//            }
//        }
//        updateUI();
//    }

    void updateUI() {
        if (mMusicService.isMusicPlay()) {
            mMusicService.UpdateTime();
            if (mMusicService.isPlaying()) {
                mClickPlay.setBackgroundResource(R.drawable.ic_pause);
            } else {
                mClickPlay.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            }

            if(!mMusicService.getLink().equals(""))
            if (mMusicService.imageArtist(mMusicService.getLink()) != null) {
                mdisk.setImageBitmap(mMusicService.imageArtist(mMusicService.getLink()));
            } else
                mdisk.setImageResource(R.drawable.default_cover_art);

            mNameSong.setText(mMusicService.getNameSong());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void clickItem(Song songs) {
       mMusicService.setmPosition(songs.getId());
        mPosition = songs.getId();
        if (mMusicService.isMusicPlay()) {
            mMusicService.pauseSong();
        }
        mMusicService.playSong(songs.getId());
        mNameSong.setText(songs.getTitle());
        mArtist.setText(songs.getArtist());
        updateUI();
    }
}
