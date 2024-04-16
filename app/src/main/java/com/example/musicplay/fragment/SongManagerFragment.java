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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.SongFormActivity;
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
    private List<Song> songList;
    private View view;
    private int currentPosition;
    private RecyclerView mRecyclerView;
    private SongManagerAdapter mSongAdapter;
    private SongApi songApi;
    private LinearLayout deleteLayout, editLayout;
    private Dialog dialog;
    private Long id_song;


    public SongManagerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_song_manager, container, false);
        dialog = new Dialog(getActivity());
        init();
        loadData();
        setEvent();
        return view;
    }

    private void init() {
        mRecyclerView = view.findViewById(R.id.rcvSongListManager);
        songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
    }

    private void loadData() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        songList = new ArrayList<>();
        songApi.getAllSong().enqueue(new Callback<SongMessage>() {
            @Override
            public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                if (response.isSuccessful()) {
                    songList = response.body().getSongs();
                    mSongAdapter = new SongManagerAdapter(songList);
                    mRecyclerView.setAdapter(mSongAdapter);
                    mSongAdapter.notifyDataSetChanged();

                    mSongAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            currentPosition = position;
                            Song song = songList.get(currentPosition);
                            showDialog(song);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SongMessage> call, Throwable t) {

            }
        });
    }

    private void setEvent() {

    }

    private void showDialog(Song song) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);
        id_song = song.getId();

        deleteLayout = dialog.findViewById(R.id.layout_delete);
        editLayout = dialog.findViewById(R.id.layout_edit);

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                loadingDialog.show();

                songApi.deleteSong(id_song).enqueue(new Callback<SongMessage>() {
                    @Override
                    public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                        if (response.isSuccessful()) {
//                            songList.remove(currentPosition);
//                            mSongAdapter.notifyDataSetChanged();
                            String notify = response.body().getMessage();
                            Toast.makeText(getActivity(), notify, Toast.LENGTH_SHORT).show();
                            loadingDialog.cancel();
                            dialog.dismiss();
                            onResume();
                        }
                    }

                    @Override
                    public void onFailure(Call<SongMessage> call, Throwable t) {
                        loadingDialog.dismiss();
                    }
                });
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song song = songList.get(currentPosition);
                dialog.cancel();
                Intent intent = new Intent(getActivity(), SongFormActivity.class);
                intent.putExtra("data", song);
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
    }
}
