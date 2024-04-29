package com.example.musicplay.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.CategoryFormActivity;
import com.example.musicplay.adapter.CategoryAdapter;
import com.example.musicplay.api.CategoryApi;
import com.example.musicplay.domain.Category;
import com.example.musicplay.domain.CategoryMessage;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class    CategoryManagerFragment extends Fragment {
    ImageButton btnAdd;
    List<Category> categoryList;
    View view;
    int currentPosition;
    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;

    CategoryApi categoryApi;
    public CategoryManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_category_manager, container, false);

        init();

        return view;
    }

    private void setEvent() {
    }

    private void loadData(){

        mRecyclerView = view.findViewById(R.id.rcvCategoryManager);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Create a list of songs
        getCategory();
        // Create and set the adapter for the RecyclerView

    }

    private void getCategory(){
        categoryApi= RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        categoryApi.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categoryList = response.body();
                mAdapter = new CategoryAdapter(categoryList);
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Category data = categoryList.get(position);
                        showDialog(data);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    private void init() {
        setEvent();
        loadData();
    }

    private void showDialog(Category data) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        LinearLayout deteteLayout = dialog.findViewById(R.id.layout_delete);
        LinearLayout editLayout = dialog.findViewById(R.id.layout_edit);

        deteteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryApi = RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
                categoryApi.delete(data.getId()).enqueue(new Callback<CategoryMessage>() {
                    @Override
                    public void onResponse(Call<CategoryMessage> call, Response<CategoryMessage> response) {
                        CategoryMessage categoryMessage = response.body();
                        Toast.makeText(getActivity(), categoryMessage.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        onResume();
                    }

                    @Override
                    public void onFailure(Call<CategoryMessage> call, Throwable t) {

                    }
                });
                Toast.makeText(getActivity(), "click Delete", Toast.LENGTH_SHORT).show();
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CategoryFormActivity.class);
                intent.putExtra("data", data);
                Toast.makeText(getActivity(), "click Edit", Toast.LENGTH_SHORT).show();

                startActivity(intent);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
