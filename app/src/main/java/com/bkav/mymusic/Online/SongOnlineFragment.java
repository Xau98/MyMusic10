package com.bkav.mymusic.Online;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bkav.mymusic.Online.APIService;
import com.bkav.mymusic.Online.Dataservice;
import com.bkav.mymusic.Online.SongOnline;
import com.bkav.mymusic.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongOnlineFragment extends Fragment {
private MediaPlayer mMediaPlayer;
private TextView mNameSongBanner ,mNameSongerBanner;
private ImageView mImageBannerBackGround , mbtPlayBanner;
private RecyclerView mRecyclerViewHomePage;
 private ArrayList<SongOnline> songOnline;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page, container, false);
     //   initView(view);
        Toast.makeText(getContext(), "SongOnlineFragment", Toast.LENGTH_SHORT).show();
//        Dataservice dataservice = APIService.getService();
//        Call<List<SongOnline>> callback = dataservice.GetData();
//        callback.enqueue(new Callback<List<SongOnline>>() {
//            @Override
//            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
//                  songOnline=(ArrayList<SongOnline>) response.body();
//                mNameSongBanner.setText(songOnline.get(0).getNAMESONG());
//                mNameSongerBanner.setText(songOnline.get(0).getNAMESONGER());
//                mbtPlayBanner.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(mMediaPlayer==null) {
//                            mMediaPlayer = new MediaPlayer();
//                            Log.d("Play ","null");
//                            new PLaySong().execute(songOnline.get(0).getLINKSONG());
//                        }else
//                        if(mMediaPlayer.isPlaying()){
//                            Log.d("Play ","Pause");
//                            mbtPlayBanner.setBackgroundResource(R.drawable.ic_pause);
//                            mMediaPlayer.pause();
//                        }else {
//                            Log.d("Play ","Play");
//                            mbtPlayBanner.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
//                            mMediaPlayer.start();
//                        }
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(Call<List<SongOnline>> call, Throwable t) {
//                Toast.makeText(getContext(), "Không có Internet", Toast.LENGTH_SHORT).show();
//            }
//        });

        return view;
    }

//    private void initView(View view) {
//        //mRecyclerViewHomePage = view.findViewById(R.id.recyclerviewHomepage);
//        mNameSongBanner =view.findViewById(R.id.nameSongBanner);
//        mNameSongerBanner= view.findViewById(R.id.nameSongerBanner);
//        mbtPlayBanner =view.findViewById(R.id.btPlaybanner);
//    }

    public void updateUI(){

    }

    class PLaySong extends AsyncTask<String , Void , String> {

        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }

        @Override
        protected void onPostExecute(String baihat) {
            super.onPostExecute(baihat);
            try {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mMediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mMediaPlayer.setDataSource(baihat);
                mMediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
            mMediaPlayer.start();


        }
    }
}
