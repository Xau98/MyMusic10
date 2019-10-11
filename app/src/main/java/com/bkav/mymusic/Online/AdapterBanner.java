package com.bkav.mymusic.Online;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bkav.mymusic.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterBanner extends PagerAdapter {
    private Context mContent;
    private ArrayList<SongOnline> mListSongOnline;
    private LayoutInflater mInflater;

    public AdapterBanner(Context mContent, ArrayList<SongOnline> mListSongOnline) {
        mInflater = LayoutInflater.from(mContent);
        this.mContent = mContent;
        this.mListSongOnline = mListSongOnline;

    }

    @Override
    public int getCount() {
        return mListSongOnline.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    private TextView mNameSongBanner, mNameSongerBanner;
    private ImageView mImageBannerBackGround, mbtPlayBanner;

    private void initView(View view) {
        mNameSongBanner = view.findViewById(R.id.nameSongBanner);
        mNameSongerBanner = view.findViewById(R.id.nameSongerBanner);
        mbtPlayBanner = view.findViewById(R.id.btPlaybanner);
        mImageBannerBackGround = view.findViewById(R.id.imagebackgroundbanner);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.row_banner, null);
        initView(view);
        mNameSongBanner.setText(mListSongOnline.get(position).getNAMESONG());
        mNameSongerBanner.setText(mListSongOnline.get(position).getSINGER());
        Picasso.with(mContent).load(mListSongOnline.get(position).getIMAGE()).into(mImageBannerBackGround);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
         container.removeView((View)object);
    }
}
