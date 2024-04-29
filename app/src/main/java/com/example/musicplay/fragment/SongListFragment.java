package com.example.musicplay.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.PlayerActivity;

import com.example.musicplay.SharedPrefManager;
import com.example.musicplay.adapter.SongListAdapter;
import com.example.musicplay.api.FavouriteApi;
import com.example.musicplay.api.SongApi;
import com.example.musicplay.domain.Favourite;
import com.example.musicplay.domain.FavouriteMessage;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.domain.Song;
import com.example.musicplay.domain.SongMessage;
import com.example.musicplay.domain.User;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private SongListAdapter mSongAdapter;
    private TextView tvTitle;
    private SongApi songApi;
    private FavouriteApi favouriteApi;
    View view;
    static boolean isCategory;
    private Long categoryId;
    String title;

    public SongListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_song_list, container, false);
        init();
        return view;
    }

    private void init() {
        tvTitle = view.findViewById(R.id.tvTitle);
        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryId = bundle.getLong("category");
            title = bundle.getString("title");
            tvTitle.setText(title);
            if (categoryId != null) {
                isCategory = true;
            } else {
                isCategory = false;
            }
        } else {
            isCategory = false;
        }

        mRecyclerView = view.findViewById(R.id.rcvSongList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getSong();
    }

    private void getSong() {
        if (isCategory == true) {
            getSongByCategory();
        } else {
            getSongByFavourite();
        }
    }

    private void getSongByCategory(){
        songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);

        songApi.SongCategory(categoryId).enqueue(new Callback<SongMessage>() {
            @Override
            public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                List<Song> songs;
                SongMessage songMessage = response.body();
                songs = songMessage.getSongs();
                List<Song> songList = songs;
                mSongAdapter = new SongListAdapter(songs);
                mRecyclerView.setAdapter(mSongAdapter);
                mRecyclerView.setHasFixedSize(true);
                mSongAdapter.notifyDataSetChanged();
                if (songs != null && !songs.isEmpty()) {
                    mSongAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Song data = songs.get(position);
                            Intent intent = new Intent(getActivity(), PlayerActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("songs", (Serializable) songs);
                            System.out.println("-----------------");
                            System.out.println(data);
                            //intent.putExtra("songList", new ArrayList<>(songList));
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SongMessage> call, Throwable t) {
                System.out.println(t);
            }
        });
    }

    private void getSongByFavourite() {
        User user = SharedPrefManager.getInstance(getContext()).getUser();
        Long id = user.getId();

        favouriteApi = RetrofitClient.getRetrofit().create(FavouriteApi.class);
        favouriteApi.listByUser(id).enqueue(new Callback<FavouriteMessage>() {
            @Override
            public void onResponse(Call<FavouriteMessage> call, Response<FavouriteMessage> response) {
                if (response.isSuccessful()) {
                    FavouriteMessage favouriteMessage = response.body();
                    List<Song> songs = new ArrayList<>();
                    List<Favourite> favourites = favouriteMessage.getFavourites();
                    if (favourites == null) {
                        System.out.println("null");
                    } else {

                        for (Favourite favourite : favourites) {
                            songs.add(favourite.getSong());
                            System.out.println(favourite.getSong().getName());
                        }
                    }
                    setupSongListAdapter(songs);
                }
            }

            @Override
            public void onFailure(Call<FavouriteMessage> call, Throwable t) {
                System.out.println("khong lay dc favourite");
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupSongListAdapter(List<Song> songs) {
        if (songs != null) {
            mSongAdapter = new SongListAdapter(songs);
            mRecyclerView.setAdapter(mSongAdapter);
            mRecyclerView.setHasFixedSize(true);
            mSongAdapter.notifyDataSetChanged();

            if (!songs.isEmpty()) {
                mSongAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Song song = songs.get(position);
                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("songs", (Serializable) songs);
                        startActivity(intent);
                    }
                });
            }
        }
        else {
            Toast.makeText(getContext(), "Không có bài hát nào", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
