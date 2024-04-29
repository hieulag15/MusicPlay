package com.example.musicplay.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.adapter.SongManagerAdapter;
import com.example.musicplay.api.SongApi;
import com.example.musicplay.asset.LoadingDialog;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.domain.Song;
import com.example.musicplay.domain.SongMessage;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongManagerFragment extends Fragment {
    ImageButton btnAdd;
    List<Song> songList;
    View view;
    int currentPosition;
    private RecyclerView mRecyclerView;
    private SongManagerAdapter mSongAdapter;

    SongApi songApi;
    public SongManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_song_manager, container, false);
        init();
        return view;
    }

    private void setEvent() {
    }

    private void loadData(){

        mRecyclerView = view.findViewById(R.id.rcvSongListManager);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create a list of songs
        songList = new ArrayList<>();
        songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
        songApi.getAllSong().enqueue(new Callback<SongMessage>() {
            @Override
            public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                songList = response.body().getSongs();

                // Create and set the adapter for the RecyclerView
                mSongAdapter = new SongManagerAdapter(songList);
                mRecyclerView.setAdapter(mSongAdapter);
                mSongAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        currentPosition = position;
                        Song song  = songList.get(currentPosition);
                        showDialog(song);
                    }
                });
            }

            @Override
            public void onFailure(Call<SongMessage> call, Throwable t) {

            }
        });
    }

    private void init() {
        setEvent();
        loadData();
    }

    private void showDialog(Song song) {
        Long id_song = song.getId();
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        LinearLayout deteteLayout = dialog.findViewById(R.id.layout_delete);
        LinearLayout editLayout = dialog.findViewById(R.id.layout_edit);


        deteteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
                LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                loadingDialog.show();
                songApi.deleteSong(id_song).enqueue(new Callback<SongMessage>() {

                    @Override
                    public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                        String string = response.body().getMessage();
                        Toast.makeText(getActivity(),string,Toast.LENGTH_SHORT).show();
                        loadingDialog.cancel();
                        dialog.dismiss();
                        onResume();
                    }

                    @Override
                    public void onFailure(Call<SongMessage> call, Throwable t) {
                        loadingDialog.cancel();
                    }
                });

            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song data = songList.get(currentPosition);
                dialog.cancel();
                Intent intent = new Intent(getActivity(), SongFormActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    @Override
    public void onResume() {
        super.onResume();
        init();
        // Perform any necessary updates here
    }
}
