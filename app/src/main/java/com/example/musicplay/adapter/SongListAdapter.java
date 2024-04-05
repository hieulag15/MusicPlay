package com.example.musicplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.domain.Song;
import com.example.musicplayer.R;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder>{

    private List<Song> SongList;
    private OnItemClickListener Listener;

    public SongListAdapter(List<Song> songList) {
        SongList = songList;
    }
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view, Listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.SongViewHolder holder, int position) {

        Song song = SongList.get(position);

        holder.SongName.setText(song.getName());
        holder.ArtistTextView.setText(song.getSinger());
        //holder.mImageView.setImageResource(song.getImage());

        Glide.with(holder.itemView.getContext())
                .load(song.getImage())
                .into(holder.ImageView);

    }

    @Override
    public int getItemCount() {
        return SongList != null ? SongList.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        public TextView SongName;
        public TextView ArtistTextView;
        public ImageView ImageView;

        public SongViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ImageView = itemView.findViewById(R.id.imgSong);
            SongName = itemView.findViewById(R.id.tvSongName);
            ArtistTextView = itemView.findViewById(R.id.tvSinger);

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
}
