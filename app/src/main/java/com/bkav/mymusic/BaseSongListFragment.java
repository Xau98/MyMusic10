package com.bkav.mymusic;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BaseSongListFragment extends Fragment implements MusicAdapter.OnClickItemView  {

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
    private List<Song> mListSongs = new ArrayList<>();
    private int position = 0;
    private MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
    private  String mURL = "content://com.bkav.provider";
    private Uri mURISong= Uri.parse(mURL);
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) iBinder;
            mMusicService = binder.getMusicBinder();
            mAdapter.setmMusicService(mMusicService);
            mMusicService.setmListAllSong(mListSongs);
            updateUI();
          //  MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
         //   mMediaPlaybackFragment.setmMusicService(mMusicService);
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
        }
    };

//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mExitService == true) {
//
//            updateUI();
//            mAdapter.setmMusicService(mMusicService);
//            Log.d("size", mListSongs.size() + "//");
//        }
//    }

    public void setSong(List<Song> songs) {
        this.mListSongs = songs;
        mAdapter.setSong(songs);
        if (mExitService == true) {
            updateUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setmMusicService(mMusicService);
    }
//
//    public void setmMusicService(MediaPlaybackService mMusicService) {
//        this.mMusicService = mMusicService;
//
//    }

    void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mClickPlay = view.findViewById(R.id.play);
        mArtist = view.findViewById(R.id.Artist);
        mdisk = view.findViewById(R.id.disk);
        mNameSong = view.findViewById(R.id.namePlaySong);
        mNameSong.setSelected(true);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        if(getActivity().findViewById(R.id.frament2)!=null)
            constraintLayout.setVisibility(View.GONE);
    }


    @Override
    public void onStart() {
        super.onStart();
        Intent it = new Intent(getActivity(), MediaPlaybackService.class);
        getActivity().bindService(it, mServiceConnection, 0);
        mAdapter = new MusicAdapter(this, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(mMusicService!=null)
        mRecyclerView.scrollToPosition(mMusicService.getmPosition());
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
        mSharePreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);// move Service
        position = mSharePreferences.getInt("position", 0);
        mNameSong.setText(mSharePreferences.getString("nameSong", "Name Song"));
        mArtist.setText(mSharePreferences.getString("nameArtist", "Name Artist"));
        Log.d("create_db",mSharePreferences.getBoolean("create_db", false)+"//ok");
        if (!mSharePreferences.getString("path", "").equals(""))
            if (imageArtist(mSharePreferences.getString("path", "")) != null) {
                mdisk.setImageBitmap(imageArtist(mSharePreferences.getString("path", "")));
            } else
                mdisk.setImageResource(R.drawable.default_cover_art);

        if (mSharePreferences.getString("nameSong", "").equals(""))
            constraintLayout.setVisibility(View.GONE);

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
                if (getActivity().findViewById(R.id.framentContent) != null) {
                    if (!mMusicService.isMusicPlay()) {
                        mMusicService.playSong(position);
                    }

                    mMediaPlaybackFragment.setmMusicService(mMusicService);
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.framentContent, mMediaPlaybackFragment).commit();
                }
            }
        });
        return view;
    }

    public void updateUI() {
        if (mMusicService.isMusicPlay()) {
            constraintLayout.setVisibility(View.VISIBLE);
            mMusicService.UpdateTime();
            if (mMusicService.isPlaying()) {
                mClickPlay.setBackgroundResource(R.drawable.ic_pause);
            } else {
                mClickPlay.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            }

            if (!mMusicService.getmPath().equals(""))
                if (mMusicService.imageArtist(mMusicService.getmPath()) != null) {
                    mdisk.setImageBitmap(mMusicService.imageArtist(mMusicService.getmPath()));
                } else
                    mdisk.setImageResource(R.drawable.default_cover_art);

            mNameSong.setText(mMusicService.getmNameSong());
            mArtist.setText(mMusicService.getmArtist());

        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main, menu);
//        Log.d("search", Log.getStackTraceString(new Exception()));
//        Log.d("search", "search");
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
        ///===========///
        String selection=" id_provider ="+songs.getId();
        Cursor c = getActivity().managedQuery(mURISong, null, selection, null, null);
        if(c.moveToFirst()){
            do{
               Log.d("ID",c.getString(c.getColumnIndex("id_provider")));
               if(c.getInt(c.getColumnIndex(FavoriteSongsProvider.FAVORITE))!=1)
               if(c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT))<2){
                   ContentValues values = new ContentValues();
                   values.put(FavoriteSongsProvider.COUNT,c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT))+1);
                   getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,FavoriteSongsProvider.ID_PROVIDER +"= "+songs.getId(),null);
                   Log.d("ID",c.getString(c.getColumnIndex(FavoriteSongsProvider.COUNT))+"//"+c.getString(c.getColumnIndex(FavoriteSongsProvider.FAVORITE)));
               }else {
                   if(c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT))==2) {
                       ContentValues values = new ContentValues();
                       values.put(FavoriteSongsProvider.COUNT, 0);
                       values.put(FavoriteSongsProvider.FAVORITE, 2);
                       getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + songs.getId(), null);
                       Log.d("ID1", c.getString(c.getColumnIndex(FavoriteSongsProvider.COUNT)) + "//" + c.getString(c.getColumnIndex(FavoriteSongsProvider.FAVORITE)));
                   }
               }

            }while(c.moveToNext());
        }

        Log.d("click :", songs.getTitle() + "//" + songs.getId());

    }

}
