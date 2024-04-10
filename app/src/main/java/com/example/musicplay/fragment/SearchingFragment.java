package com.example.musicplay.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.musicplay.api.SongApi;
import com.example.musicplay.domain.Song;
import com.example.musicplay.domain.SongMessage;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchingFragment extends Fragment {
    private View view;
    private EditText edSearch;
    private SongApi songApi;
    private ImageButton btnSearch, btnBack;
    private FragmentManager fragmentManager;
    private TextView tvSearchResult;

    public SearchingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_searching, container, false);

        init();
        setEvent();

        return view;
    }

    private void init() {
        edSearch = view.findViewById(R.id.edSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnBack = view.findViewById(R.id.btnBack);
        tvSearchResult = view.findViewById(R.id.tvFirstSong);
    }

    private void setEvent() {
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.appbar_text));
        edSearch.setBackgroundTintList(colorStateList);

        edSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    edSearch.setHintTextColor(getResources().getColor(R.color.transparent));
                } else {
                    edSearch.setHintTextColor(colorStateList);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager = getParentFragmentManager();
                SearchFragment searchFragment = new SearchFragment();
                fragmentManager.beginTransaction().replace(R.id.container, searchFragment).commit();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String songName = edSearch.getText().toString();

                songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
                songApi.getByName(songName).enqueue(new Callback<SongMessage>() {
                    @Override
                    public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                        if (response.isSuccessful()) {
                            SongMessage songMessage = response.body();
                            List<Song> songs = songMessage.getSongs();

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();

                            Fragment searchFragment = new SearchFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("songs", (Serializable) songs);
                            searchFragment.setArguments(bundle);

                            transaction.replace(R.id.container, searchFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
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
