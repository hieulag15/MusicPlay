package com.example.musicplay.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicplay.domain.Category;

import java.util.List;

public class CategorySpinnerAdapter extends BaseAdapter {
    private List<Category> categories;
    private Context context;

    public CategorySpinnerAdapter(List<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    public List<Category> getCategories(){return categories;}

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            // If the view is not recycled, create a new one
            textView = new TextView(context);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(20);
            textView.setTextColor(Color.BLACK);
        } else {
            // Otherwise, use the recycled view
            textView = (TextView) convertView;
        }

        // Set the text for the view
        textView.setText(categories.get(position).getName());

        return textView;
    }
}
