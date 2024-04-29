package com.example.musicplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.domain.Song;

import java.util.List;

public class SongManagerAdapter extends RecyclerView.Adapter<SongManagerAdapter.SongViewHolder> {

    private List<Song> mSongList;
    private OnItemClickListener mListener;

    public SongManagerAdapter(List<Song> songList) {
        mSongList = songList;
    }

    @Override
    public SongManagerAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_manager, parent, false);
        return new SongManagerAdapter.SongViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(SongManagerAdapter.SongViewHolder holder, int position) {
        Song song = mSongList.get(position);

        holder.mSongName.setText(song.getName());
        holder.mArtistTextView.setText(song.getSinger());

//        holder.mImageView.setImageResource(song.getImage());
        Glide.with(holder.itemView.getContext())
                .load(song.getImage())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        public TextView mSongName;
        public TextView mArtistTextView;
        public ImageView mImageView;

        public SongViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imgSong);
            mSongName = itemView.findViewById(R.id.tvSongName);
            mArtistTextView = itemView.findViewById(R.id.tvSinger);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}