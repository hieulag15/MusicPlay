package com.example.musicplay.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.PlayerActivity;
import com.example.musicplay.SQLite.DatabaseHelper;

import com.example.musicplay.SharedPrefManager;
import com.example.musicplay.adapter.CategoryAdapter;
import com.example.musicplay.adapter.SongListAdapter;
import com.example.musicplay.api.CategoryApi;
import com.example.musicplay.api.FavouriteApi;
import com.example.musicplay.api.SongApi;
import com.example.musicplay.domain.Category;
import com.example.musicplay.domain.Favourite;
import com.example.musicplay.domain.FavouriteMessage;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.domain.Song;
import com.example.musicplay.domain.SongMessage;
import com.example.musicplay.domain.User;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView categoryList, lastestCategoryRecyclerView;

    private SongListAdapter mSongAdapter;
    CategoryApi categoryApi;

    List<Song> songRecents;

    FavouriteApi favouriteApi;

    SongApi songApi;

    View view;

    TextView favoriteDescription;

    LinearLayout favorite;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_home, container, false);

        categoryList = view.findViewById(R.id.categoryRecyclerView);

        GetCategory();
        getSongByFavourite();
        getRecent();
        event();
        return view ;
    }

    private void event() {
        favorite = view.findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Replace the current fragment with a new fragment
                Fragment songListFragment = new SongListFragment();
                transaction.replace(R.id.container, songListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }
    private void getRecent(){
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Long> recentList= dbHelper.getAllData();
        Collections.reverse(recentList);
        lastestCategoryRecyclerView = view.findViewById(R.id.lastestCategoryRecyclerView);
        lastestCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
        if(!recentList.isEmpty()) {
            songApi.GetById(recentList).enqueue(new Callback<SongMessage>() {
                @Override
                public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                    List<Song> songs;
                    if (response.body().getMessage().equals("Successfully")) {
                        SongMessage songMessage = response.body();
                        songs = songMessage.getSongs();

                        mSongAdapter = new SongListAdapter(songs);
                        lastestCategoryRecyclerView.setAdapter(mSongAdapter);
                        lastestCategoryRecyclerView.setHasFixedSize(true);
                        mSongAdapter.notifyDataSetChanged();
                        if (songs != null && !songs.isEmpty()) {
                            mSongAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Song data = songs.get(position);
                                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                                    intent.putExtra("position", position);
                                    intent.putExtra("songs", (Serializable) songs);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                }

                @Override
                public void onFailure(Call<SongMessage> call, Throwable t) {

                }
            });
        }
    }

    private void GetCategory() {
        categoryApi= RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        categoryApi.getAllCategory().enqueue(new Callback<List<Category>>() {

            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categoryList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

                List<Category> categories;

                categories = response.body();


                CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
                categoryList.setHasFixedSize(true);
                categoryList.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();
                categoryAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // Get the clicked item's data
                        Category data = categories.get(position);

                        // Start a new fragment transaction
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        // Replace the current fragment with a new fragment
                        Fragment songListFragment = new SongListFragment();
                        Bundle args = new Bundle();
                        args.putString("title", data.getName());
                        args.putSerializable("category", data.getId());
                        songListFragment.setArguments(args);

                        transaction.replace(R.id.container, songListFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }
    private void getSongByFavourite(){
        User user = SharedPrefManager.getInstance(getContext()).getUser();
        favouriteApi =  RetrofitClient.getInstance().getRetrofit().create(FavouriteApi.class);

        favouriteApi.listByUser(user.getId()).enqueue(new Callback<FavouriteMessage>() {
            @Override
            public void onResponse(Call<FavouriteMessage> call, Response<FavouriteMessage> response) {


                FavouriteMessage favouriteMessage = response.body();
                List<Favourite> favourites = favouriteMessage.getFavourites();
                favoriteDescription = view.findViewById(R.id.favoriteDescriptiontest);
                int len = favourites.size();
                favoriteDescription.setText("Hiện tại có " +len+" ca khúc được bạn thích");
            }

            @Override
            public void onFailure(Call<FavouriteMessage> call, Throwable t) {

            }
        });
    }
}
