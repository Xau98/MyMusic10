package com.bkav.mymusic;

import android.Manifest;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ActivityMusic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MediaPlaybackService mMusicService;
    private boolean mExitService = false;
    private  MediaPlaybackFragment  mMediaPlaybackFragment= new MediaPlaybackFragment();;
    private AllSongsFragment mAllSongsFragment= new AllSongsFragment();
private FavoriteSongsFragment mFavoriteSongsFragment=new FavoriteSongsFragment();
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) iBinder;
            mMusicService = binder.getMusicBinder();
            mMediaPlaybackFragment.setmMusicService(mMusicService);
            //mAllSongsFragment.setmMusicService(mMusicService);
            mExitService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(mMusicService, "dis", Toast.LENGTH_SHORT).show();
        }
    };

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

     if(findViewById(R.id.frament2)!=null){
//         Toast.makeText(this, "fragment 1,2", Toast.LENGTH_SHORT).show();
         Log.d("search", "onCreate: frament1");
         getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();
         getSupportFragmentManager().beginTransaction().replace(R.id.frament2, mMediaPlaybackFragment).commit();
     }else if(findViewById(R.id.framentContent)!=null) {
         Log.d("search", "onCreate: framentContent");
//         Toast.makeText(this, "fragment content", Toast.LENGTH_SHORT).show();
         getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {
            Toast.makeText(this, "favorite", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mFavoriteSongsFragment).commit();
        } else if (id == R.id.nav_playlist) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//        public void updateFragment() {
//            if(findViewById(R.id.frament1)!=null&& findViewById(R.id.frament2)!=null) {
//                //    mAllSongsFragment.updateUI();
//                //  if(findViewById(R.id.framentContent)!=null)
//                // mMediaPlaybackFragment.updateUI();
//            }
//    }
}
