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
    View view;
    EditText edSearching;

    private SongApi songApi;
    ImageButton ibBack, ibSearch;
    TextView tv;
    FragmentManager fragmentManager;
    public SearchingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_searching, container, false);

//        init();
        setEvent();

        return view;
    }

//    private void init(){
//        edSearching = view.findViewById(R.id.edSearching);
//        ibBack = view.findViewById(R.id.ibBackSearching);
//        tv = view.findViewById(R.id.tvFirstSong);
//        ibSearch= view.findViewById(R.id.ibSearch);
//    }

    private void setEvent(){
        //to opacity for hint of editext (edSearching) when selected
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.appbar_text));
        edSearching.setHintTextColor(colorStateList);
        edSearching.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edSearching.setHintTextColor(getResources().getColor(R.color.transparent));
                } else {
                    edSearching.setHintTextColor(colorStateList);
                }
            }
        });

        //back to previous page
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                SearchFragment searchFragment = new SearchFragment();
                fragmentManager.beginTransaction().replace(R.id.container, searchFragment).commit();
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringSong = edSearching.getText().toString();

                songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
                songApi.GetByName(stringSong).enqueue(new Callback<SongMessage>() {
                    @Override
                    public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {

                        SongMessage songMessage = response.body();
                        List<Song> songList = songMessage.getSongs();
                        // Start a new fragment transaction
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        // Replace the current fragment with a new fragment
                        Fragment searchFragment = new SearchFragment();
                        Bundle args = new Bundle();
                        System.out.println("--------"+songList);
                        args.putSerializable("songs", (Serializable) songList);
                        searchFragment.setArguments(args);

                        transaction.replace(R.id.container, searchFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                    @Override
                    public void onFailure(Call<SongMessage> call, Throwable t) {

                    }
                });
            }
        });
    }
}
