package com.example.musicplay.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.PlayerActivity;
import com.example.musicplay.SQLite.DatabaseHelper;
import com.example.musicplay.SharePrefManager;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        getCategory();
        getSongByFavorite();
        getRecent();
        addevent();
        return view;
    }

    private void getCategory() {
        categoryApi = RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        categoryApi.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    List<Category> categories = response.body();

                    CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
                    categoryList.setHasFixedSize(true);
                    categoryList.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();

                    //Click vao category
                    categoryAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Category category = categories.get(position);

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();

                            Fragment songListFragment = new SongListFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("title", category.getName());
                            bundle.putSerializable("category", category.getId());
                            songListFragment.setArguments(bundle);

                            transaction.replace(R.id.container, songListFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    private void getSongByFavorite() {
        User user = SharePrefManager.getInstance(getContext()).getUser();
        favouriteApi = RetrofitClient.getInstance().getRetrofit().create(FavouriteApi.class);
        favouriteApi.listByUser(user.getId()).enqueue(new Callback<FavouriteMessage>() {
            @Override
            public void onResponse(Call<FavouriteMessage> call, Response<FavouriteMessage> response) {
                if (response.isSuccessful()) {
                    FavouriteMessage favouriteMessage = response.body();
                    List<Favourite> favourites = favouriteMessage.getFavourites();
                    favouriteDescription = view.findViewById(R.id.favoriteDescriptiontest);
                    int len;
                    if(favourites == null) {
                        len = 0;
                        System.out.println("null");
                    } else {
                        len = favourites.size();
                    }
                    favouriteDescription.setText("Hiện tại có " +len+" ca khúc được bạn thích");
                }

            }

            @Override
            public void onFailure(Call<FavouriteMessage> call, Throwable t) {

            }
        });
    }

    private void getRecent() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        List<Long> recentList = databaseHelper.getAllData();
        Collections.reverse(recentList);

        lastCategoryRecyclerView = view.findViewById(R.id.lastestCategoryRecyclerView);
        lastCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);

        if (!recentList.isEmpty()) {
            songApi.getListId(recentList).enqueue(new Callback<SongMessage>() {
                @Override
                public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                    if (response.isSuccessful()) {
                        SongMessage songMessage = response.body();
                        if (songMessage != null && songMessage.getMessage() != null && songMessage.getMessage().equals("Successfully")) {
                            List<Song> songs = songMessage.getSongs();
                            songListAdapter = new SongListAdapter(songs);
                            lastCategoryRecyclerView.setAdapter(songListAdapter);
                            lastCategoryRecyclerView.setHasFixedSize(true);
                            songListAdapter.notifyDataSetChanged();;

                            if (songs != null && !songs.isEmpty()) {
                                songListAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                                        intent.putExtra("position", position);
                                        intent.putExtra("songs", (Serializable) songs);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<SongMessage> call, Throwable t) {

                }
            });
        }
    }

    private void addevent() {
        favourite = view.findViewById(R.id.favorite);
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                //Thay fragment moi
                Fragment songListFragment = new SongListFragment();
                transaction.replace(R.id.container, songListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

}
