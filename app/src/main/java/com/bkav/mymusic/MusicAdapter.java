package com.bkav.mymusic;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements Filterable {
    private List<Song> mListSong = new ArrayList<>();
    private List<Song> mSong;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnClickItemView mClickItemView;
    private MediaPlaybackService mMusicService;
    public MusicAdapter(OnClickItemView mClickItemView, Context context ) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mClickItemView = mClickItemView;

    }

    public void setmMusicService(MediaPlaybackService mMusicService) {
        this.mMusicService = mMusicService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (mSong != null) {
            Song current = mSong.get(position);
            holder.mStt.setText(current.getId() + "");
           // Log.d(current.getId() + "show", current.getTitle());
            holder.mNameSong.setText(current.getTitle());
            SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
            holder.mHours.setText(formmatTime.format(current.getDuration()));

            final Song finalCurrent = current;
            holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(finalCurrent.getId() +"position ", position+"//"+ finalCurrent.getTitle());
                    mClickItemView.clickItem(finalCurrent);
                }
            });

            if (mMusicService != null) {
                mMusicService.setmListAllSong(mSong);
                if (mMusicService.getmNameSong().equals(mSong.get(position).getTitle())) {
                    holder.mStt.setText("");
                    holder.mNameSong.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    holder.mStt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_equalizer_black_24dp, 0, 0, 0);
                }else {
                    holder.mNameSong.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    holder.mStt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        } else {
            holder.mNameSong.setText("No Song");
        }
    }


    @Override
    public int getItemCount() {
        if (mSong != null)
            return mSong.size();
        else
            return 0;
    }

    public void setSong(List<Song> songs) {
        mSong = songs;
        Log.d("size2", songs.size() + "//");
        notifyDataSetChanged();
    }

    public void updateList(List<Song> songs) {
        mSong = songs;
       mListSong = new ArrayList<>(mSong);
        notifyDataSetChanged();


    }
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Song> filterList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(mListSong);
            } else {
                String filterPattern = unAccent(charSequence.toString().toLowerCase().trim());

                for (Song song : mListSong) {
                    if (unAccent(song.getTitle().toLowerCase()).contains(filterPattern)) {
                        filterList.add(song);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mSong.clear();
            mSong.addAll((Collection<? extends Song>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNameSong, mHours;
        ImageButton mMore;
        TextView mStt;
        ConstraintLayout mConstraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mConstraintLayout= itemView.findViewById(R.id.constraintLayoutItem);
            mNameSong = itemView.findViewById(R.id.nameSong);
            mHours = itemView.findViewById(R.id.hours);
            mStt = itemView.findViewById(R.id.stt);
            mMore = itemView.findViewById(R.id.more);
        }
    }

    interface OnClickItemView {
     void clickItem( Song song);
    }
}
