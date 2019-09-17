package com.bkav.mymusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<Song> mSong;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnClickItemView mClickItemView;
    private MediaPlaybackService mMusicService;
     TextView oldName, oldIndex;

    public ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) iBinder;
            mMusicService = binder.getMusicBinder();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public MusicAdapter( OnClickItemView mClickItemView , Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mClickItemView = mClickItemView;

        Intent it = new Intent(context, MediaPlaybackService.class);
        context.bindService(it, serviceConnection, 0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }
     int index=0;
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position){
        if (mSong != null) {
            Song current = mSong.get(position);
            holder.mStt.setText(current.getId() +"");
            Log.d(current.getId()+"show", current.getTitle());
            holder.mNameSong.setText(current.getTitle());
            SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
            holder.mHours.setText(formmatTime.format(current.getDuration()) );

            holder.mNameSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickItemView.clickItem(holder.mNameSong.getText()+"");
                    if(oldIndex!=null &&oldName!=null){
                       oldIndex.setText(index+"");
                        oldName.setTypeface( Typeface.DEFAULT, Typeface.NORMAL);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                            oldIndex.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
                        }
                    }
                   holder.mStt.setText("");
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                        holder.mStt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_equalizer_black_24dp,0,0,0);
                    }
                    holder.mNameSong.setTypeface( Typeface.DEFAULT, Typeface.BOLD);

                    oldName=holder.mNameSong;
                    oldIndex=holder.mStt;
                    index=position+1;
                }
            });
        } else {
            holder.mNameSong.setText("No Song");
        }
    }

    @Override
    public int getItemCount() {
        if(mSong !=null)
            return mSong.size();
        else
        return 0;
    }

    public void setSong(List<Song> songs) {
        mSong = songs;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView mNameSong , mHours;
         ImageButton mMore;
         TextView mStt;
        int mPosition=-1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameSong =itemView.findViewById(R.id.nameSong);
            mHours =itemView.findViewById(R.id.hours);
            mStt = itemView.findViewById(R.id.stt);
            mMore= itemView.findViewById(R.id.more);

        }
    }

    interface  OnClickItemView{
        void clickItem(String namSong);
    }
}
