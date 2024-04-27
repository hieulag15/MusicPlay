package com.example.musicplay.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplay.adapter.CategoryAdapter;
import com.example.musicplay.adapter.SliderAdapter;
import com.example.musicplay.adapter.SliderModel;
import com.example.musicplay.adapter.SongAdapter;
import com.example.musicplay.api.CategoryApi;
import com.example.musicplay.domain.Category;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplay.utilities.SliderTimer;
import com.example.musicplayer.R;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends Fragment {
    private ViewPager slider;
    private ArrayList<SliderModel> sliderList;
    private SliderAdapter sliderAdapter;
    CategoryApi categoryApi;
    private RecyclerView categoryList;

    private Timer timer;
    TabLayout tabLayout;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_library, container, false);
        slider = view.findViewById(R.id.slider);
        tabLayout = view.findViewById(R.id.slider_indicator);
        sliderList = new ArrayList<>();
        timer = new Timer();
        categoryList = view.findViewById(R.id.categoryRecyclerView);
        getCategory();
        sliderList.add(new SliderModel(R.drawable.poster1,"playlist 1"));
        sliderList.add(new SliderModel(R.drawable.poster2,"playlist 2"));
        sliderList.add(new SliderModel(R.drawable.poster3,"playlist 3"));
        sliderList.add(new SliderModel(R.drawable.poster4,"playlist 4"));



        sliderAdapter = new SliderAdapter(getContext(), sliderList);
        slider.setAdapter(sliderAdapter);

        tabLayout.setupWithViewPager(slider);
        timer.scheduleAtFixedRate(new SliderTimer(getActivity(), slider, sliderList.size()), 100, 3000);
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
}



