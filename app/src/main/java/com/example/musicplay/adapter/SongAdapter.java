package com.example.musicplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.musicplay.domain.Song;
import com.example.musicplayer.R;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {
    private Context mContext;
    private int mResource;

    public SongAdapter(Context context, int resource, List<Song> songs) {
        super(context, resource, songs);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(mResource, parent, false);
        }

        Song song = getItem(position);

        ImageView albumArt = view.findViewById(R.id.imgSong);
        Glide.with(convertView.getContext())
                .load(song.getImage())
                .into(albumArt);


        TextView songTitle = view.findViewById(R.id.tvSongName);
        songTitle.setText(song.getName());

        TextView artistName = view.findViewById(R.id.tvSinger);
        artistName.setText(song.getSinger());

        return view;
    }
}
