package com.example.musicplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.musicplay.domain.Category;
import com.example.musicplayer.R;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<SliderModel> sliderList;

    public SliderAdapter(Context context, ArrayList<SliderModel> sliderList) {
        this.context = context;
        this.sliderList = sliderList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_item, null);

        ImageView sliderImage = view.findViewById(R.id.slider_image);
        TextView sliderTittle = view.findViewById(R.id.slider_item);


        sliderImage.setImageResource(sliderList.get(position).getImage());
        sliderTittle.setText(sliderList.get(position).getSlidename());
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return sliderList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((View) object);
    }
}
