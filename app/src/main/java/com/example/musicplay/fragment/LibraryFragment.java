package com.example.musicplay.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplay.adapter.SliderAdapter;
import com.example.musicplay.adapter.SliderModel;
import com.example.musicplay.adapter.SongAdapter;
import com.example.musicplayer.R;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {
    private ViewPager slider;
    private ArrayList<SliderModel> sliderList;
    private SliderAdapter sliderAdapter;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_library, container, false);
        slider = view.findViewById(R.id.slider);
        sliderList = new ArrayList<>();
        sliderList.add(new SliderModel(R.drawable.poster1,"playlist 1"));
        sliderList.add(new SliderModel(R.drawable.poster2,"playlist 2"));
        sliderList.add(new SliderModel(R.drawable.poster3,"playlist 3"));
        sliderList.add(new SliderModel(R.drawable.poster4,"playlist 4"));



        sliderAdapter = new SliderAdapter(getContext(), sliderList);
        slider.setAdapter(sliderAdapter);
        return view;
    }

}

