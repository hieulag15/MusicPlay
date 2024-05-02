package com.example.musicplay.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.PlayerActivity;
import com.example.musicplay.adapter.SongListAdapter;
import com.example.musicplay.api.SongApi;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.domain.Song;
import com.example.musicplay.domain.SongMessage;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private List<Song> songs;
    private EditText edSearch;
    private RecyclerView songRecyclerView;
    private SongListAdapter songListAdapter;
    private ImageButton btnSearch;
    private View view;
    private SongApi songApi;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        init();
        setEvent();
        return view;
    }

    private void init() {
        edSearch = view.findViewById(R.id.edSearch);
        btnSearch = view.findViewById(R.id.btnSearchSong);
        songRecyclerView = view.findViewById(R.id.songRecyclerView);

        songRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getSong(List<Song> songs) {
        if (songs != null && !songs.isEmpty()) {
            songListAdapter.setOnItemClickListener(new OnItemClickListener() {
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

    private void setEvent() {
//        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.appbar_text));
//        edSearch.setBackgroundTintList(colorStateList);

        edSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    edSearch.setHintTextColor(getResources().getColor(R.color.transparent));
//                } else {
//                    edSearch.setHintTextColor(colorStateList);
//                }
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String songName = edSearch.getText().toString();

                songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
                songApi.GetByName(songName).enqueue(new Callback<SongMessage>() {
                    @Override
                    public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                        if (response.isSuccessful()) {
                            SongMessage songMessage = response.body();
                            songs = songMessage.getSongs();

                            songListAdapter = new SongListAdapter(songs);
                            songRecyclerView.setAdapter(songListAdapter);
                            songRecyclerView.setHasFixedSize(true);
                            songListAdapter.notifyDataSetChanged();

                            getSong(songs);
                        }
                    }

                    @Override
                    public void onFailure(Call<SongMessage> call, Throwable t) {

                    }
                });
            }
        });
    }
}
