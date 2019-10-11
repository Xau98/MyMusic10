package com.bkav.mymusic.Online;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bkav.mymusic.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_banner extends Fragment {
    private  View view;
    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;
    private AdapterBanner mAdapterBanner;
    private Runnable runnable;
    private Handler handler;
    private int mCurrentIndex;
    public void initView(View view){
        mViewPager=view.findViewById(R.id.viewpager);
        mCircleIndicator= view.findViewById(R.id.circleindicator);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_banner,container,false);
        initView(view);
        GetData();
        return view;
    }
    private void GetData(){
        Dataservice dataservice = APIService.getService();
        Call<List<SongOnline>> callback = dataservice.GetData();
        callback.enqueue(new Callback<List<SongOnline>>() {
            @Override
            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
               ArrayList<SongOnline> songOnline=(ArrayList<SongOnline>) response.body();

                mAdapterBanner =new AdapterBanner(getActivity(), songOnline);
                mViewPager.setAdapter(mAdapterBanner);
                mCircleIndicator.setViewPager(mViewPager);
                handler=new Handler();
                runnable=new Runnable() {
                    @Override
                    public void run() {
                    mCurrentIndex =mViewPager.getCurrentItem();
                    mCurrentIndex++;
                    if(mCurrentIndex >= mViewPager.getAdapter().getCount()){
                        mCurrentIndex=0;
                    }
                    mViewPager.setCurrentItem(mCurrentIndex,true);
                    handler.postDelayed(runnable,1000);
                    }
                };
            }

            @Override
            public void onFailure(Call<List<SongOnline>> call, Throwable t) {
                Toast.makeText(getContext(), "Không có Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
