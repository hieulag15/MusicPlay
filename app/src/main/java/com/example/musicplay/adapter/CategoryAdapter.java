package com.example.musicplay.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.domain.Category;
import com.bumptech.glide.Glide;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplayer.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context mContext;
    private List<Category> mCategories;
    Bundle mBundle;
    Fragment mFragment;
    private OnItemClickListener mListener;

    public CategoryAdapter(List<Category> mCategories) {
        this.mCategories = mCategories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = mCategories.get(position);

        // Load the image from the URL using Glide library
        Glide.with(holder.itemView.getContext())
                .load(category.getImage())
                .into(holder.categoryImage);

        holder.categoryName.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryImage;
        public TextView categoryName;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.imgCategory);
            categoryName = itemView.findViewById(R.id.tvCategoryName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
