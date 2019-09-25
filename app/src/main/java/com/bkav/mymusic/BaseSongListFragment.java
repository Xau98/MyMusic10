package com.bkav.mymusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BaseSongListFragment extends Fragment implements MusicAdapter.OnClickItemView {

    private RecyclerView mRecyclerView;
    protected MusicAdapter mAdapter;
    private ImageButton mClickPlay;
    private TextView mNameSong, mArtist;
    private ImageView mdisk;
    private ConstraintLayout constraintLayout;
    protected MediaPlaybackService mMusicService;
    private SharedPreferences mSharePreferences;
    private String SHARED_PREFERENCES_NAME = "com.bkav.mymusic";
    private boolean mExitService = false;
    private List<Song> mListSongs;
    private int position = 0;
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) iBinder;
            mMusicService = binder.getMusicBinder();
            mAdapter.setmMusicService(mMusicService);
            mMusicService.setmListAllSong(mListSongs);
            mMusicService.getListenner(new MediaPlaybackService.Listenner() {
                @Override
                public void onItemListenner() {
                    updateUI();
                }

                @Override
                public void actionNotification() {
                    updateUI();
                }

            });
            mExitService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mExitService = false;
            //   Toast.makeText(mMusicService, "dis", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mExitService == true) {
            updateUI();
            mAdapter.setmMusicService(mMusicService);
            Log.d("size", mListSongs.size() + "//");
          //  mMusicService.setmListAllSong(mListSongs);
        }
    }

    public void setSong(List<Song> songs) {
        this.mListSongs = songs;
        Log.d("size", songs.size() + "//");
        mAdapter.setSong(songs);
    }

    void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mClickPlay = view.findViewById(R.id.play);
        mArtist = view.findViewById(R.id.Artist);
        mdisk = view.findViewById(R.id.disk);
        mNameSong = view.findViewById(R.id.namePlaySong);
        mNameSong.setSelected(true);
        constraintLayout = view.findViewById(R.id.constraintLayout);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent it = new Intent(getActivity(), MediaPlaybackService.class);
        getActivity().bindService(it, mServiceConnection, 0);
        mAdapter = new MusicAdapter(this, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_songs_fragment, container, false);
        initView(view);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        setHasOptionsMenu(true);
        mSharePreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        position = mSharePreferences.getInt("position", 3);
        mNameSong.setText(mSharePreferences.getString("nameSong", "Name Song"));
        mArtist.setText(mSharePreferences.getString("nameArtist", "Name Artist"));

        if (!mSharePreferences.getString("path", "").equals(""))
            if (imageArtist(mSharePreferences.getString("path", "")) != null) {
                mdisk.setImageBitmap(imageArtist(mSharePreferences.getString("path", "")));
            } else
                mdisk.setImageResource(R.drawable.default_cover_art);

        mClickPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (mMusicService != null) {
                    if (mMusicService.isMusicPlay()) {
                        if (mMusicService.isPlaying()) {
                            mMusicService.pauseSong();
                        } else {
                            mMusicService.playingSong();
                        }
                        updateUI();
                    } else {
                        mMusicService.setmPosition(mSharePreferences.getInt("position", 0));
                        mMusicService.playSong(mSharePreferences.getInt("position", 0));
                        updateUI();
                    }
                }
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mMusicService.isMusicPlay()) {
                    mMusicService.playSong(position);
                }
                MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
                mMediaPlaybackFragment.setmMusicService(mMusicService);
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.framentContent, mMediaPlaybackFragment).commit();
            }
        });
        return view;
    }

    public void updateUI() {
        if (mMusicService.isMusicPlay()) {
            mMusicService.UpdateTime();
            if (mMusicService.isPlaying()) {
                mClickPlay.setBackgroundResource(R.drawable.ic_pause);
            } else {
                mClickPlay.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            }

            if (!mMusicService.getLink().equals(""))
                if (mMusicService.imageArtist(mMusicService.getLink()) != null) {
                    mdisk.setImageBitmap(mMusicService.imageArtist(mMusicService.getLink()));
                } else
                    mdisk.setImageResource(R.drawable.default_cover_art);

            mNameSong.setText(mMusicService.getNameSong());
            mArtist.setText(mMusicService.getArtist());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    @Override
    public void clickItem(Song songs) {
        mMusicService.setmPosition(songs.getId());
        if (mMusicService.isMusicPlay()) {
            mMusicService.pauseSong();
        }
        mMusicService.playSong(songs.getId());
        updateUI();
        mNameSong.setText(songs.getTitle());
        mArtist.setText(songs.getArtist());
        Log.d("click :", songs.getTitle()+"//"+songs.getId());
    }
}
