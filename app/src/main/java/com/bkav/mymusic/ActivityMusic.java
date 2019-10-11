package com.bkav.mymusic;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle; ;

import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.bkav.mymusic.Online.SongOnlineFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityMusic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MediaPlaybackService mMusicService;
    private MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
    private AllSongsFragment mAllSongsFragment = new AllSongsFragment();
    private IConnectActivityAndBaseSong iConnectActivityAndBaseSong;
    private  boolean mStatus=false;
    private SharedPreferences mSharePreferences;
    private String SHARED_PREFERENCES_NAME = "com.bkav.mymusic";
    private  ArrayList<Song> mListAllSong =new ArrayList<>();
    private SongOnlineFragment mSongOnlineFragment =new SongOnlineFragment();
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) iBinder;
            mMusicService = binder.getMusicBinder();
            mMediaPlaybackFragment.setmMusicService(mMusicService);
            iConnectActivityAndBaseSong.connectActivityAndBaseSong();

            if(mStatus==false) {
                if (findViewById(R.id.frament2) != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frament2, mMediaPlaybackFragment).commit();
                } else if (findViewById(R.id.framentContent) != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();
                }
            }else {
                mSharePreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                Gson gson=new Gson();
                String json =mSharePreferences.getString("Songs", "");
                if(!json.isEmpty()){
                    Type type =new TypeToken<ArrayList<Song>>(){
                    }.getType();
                    mListAllSong =gson.fromJson(json , type);
                }
                FavoriteSongsFragment mFavoriteSongsFragment=new FavoriteSongsFragment(mListAllSong);
                if (findViewById(R.id.frament2) != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mFavoriteSongsFragment).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frament2, mMediaPlaybackFragment).commit();
                } else if (findViewById(R.id.framentContent) != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mFavoriteSongsFragment).commit();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(mMusicService, "dis", Toast.LENGTH_SHORT).show();
        }
    };

    public void setiConnectActivityAndBaseSong(IConnectActivityAndBaseSong iConnectActivityAndBaseSong) {
        this.iConnectActivityAndBaseSong = iConnectActivityAndBaseSong;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //============================================
        if(savedInstanceState!=null){
            mStatus  =savedInstanceState.getBoolean("Status");
        }
        Log.d("Status", mStatus+"//");
         if(mStatus==false) {
             if (findViewById(R.id.frament2) != null) {
                 getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();
                 getSupportFragmentManager().beginTransaction().replace(R.id.frament2, mMediaPlaybackFragment).commit();
             } else if (findViewById(R.id.framentContent) != null) {
                 getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();
             }
         }else {

             FavoriteSongsFragment mFavoriteSongsFragment=new FavoriteSongsFragment( );
             if (findViewById(R.id.frament2) != null) {
                 getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mFavoriteSongsFragment).commit();
                 getSupportFragmentManager().beginTransaction().replace(R.id.frament2, mMediaPlaybackFragment).commit();
             } else if (findViewById(R.id.framentContent) != null) {
                 getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mFavoriteSongsFragment).commit();
             }
         }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isMyServiceRunning(MediaPlaybackService.class)) {
            connectService();
        } else {
            startService();
            connectService();
        }
    }

    //    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
//            setContentView(R.layout.activity_main);
//            Log.d("xoay", "xoay");
//            Toast.makeText(this, "xoay", Toast.LENGTH_SHORT).show();
//
//        }else {
//            Toast.makeText(this, "xoay1", Toast.LENGTH_SHORT).show();
//
//        }
//    }

    public void startService() {
        Intent it = new Intent(ActivityMusic.this, MediaPlaybackService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(it);
        }
    }

    public void connectService() {
        Intent it = new Intent(ActivityMusic.this, MediaPlaybackService.class);
        bindService(it, mServiceConnection, 0);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ActivityMusic.this, "Permision Write File is Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ActivityMusic.this, "Permision Write File is Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(ActivityMusic.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(ActivityMusic.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("Status",mStatus);
    }

    //
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_favorite) {
            Toast.makeText(this, "favorite", Toast.LENGTH_SHORT).show();
            mStatus=true;
            FavoriteSongsFragment mFavoriteSongsFragment = new FavoriteSongsFragment((ArrayList<Song>) mMusicService.getmListAllSong());
            getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mFavoriteSongsFragment).commit();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_playlist) {
            mStatus=false;
            getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }else
            if (id== R.id.nav_onlinelist){

                getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mSongOnlineFragment).commit();
            }
        return true;
    }

    interface IConnectActivityAndBaseSong {
        void connectActivityAndBaseSong();
    }
}
