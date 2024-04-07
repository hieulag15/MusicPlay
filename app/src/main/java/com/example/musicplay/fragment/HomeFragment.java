package com.example.musicplay.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.adapter.SongListAdapter;
import com.example.musicplay.api.CategoryApi;
import com.example.musicplay.api.FavouriteApi;
import com.example.musicplay.api.SongApi;
import com.example.musicplay.domain.Song;
import com.example.musicplayer.R;

import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView categoryList, lastCategoryRecyclerView;
    private SongListAdapter songListAdapter;
    CategoryApi categoryApi;
    List<Song> songRecents;
    FavouriteApi favouriteApi;
    SongApi songApi;
    View view;
    TextView favouriteDescription;
    LinearLayout favourite;
    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryList = view.findViewById(R.id.categoryRecyclerView);
        return view;
    }
}
