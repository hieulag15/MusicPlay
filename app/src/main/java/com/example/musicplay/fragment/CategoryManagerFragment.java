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

public class CategoryManagerFragment extends Fragment {
    private ImageButton btnAdd;
    private List<Category> categoryList;
    private View view;
    int currentPosition;
    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;

    private CategoryApi categoryApi;
    public CategoryManagerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category_manager, container, false);

        init();
        return  view;
    }

    private void loadData() {
        mRecyclerView = view.findViewById(R.id.rcvCategoryManager);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        getCategory();
    }

    private void getCategory() {
        categoryApi = RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        categoryApi.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    mAdapter = new CategoryAdapter(categoryList);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Category category = categoryList.get(position);
                            showDialog(category);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    private void init() {
        loadData();
    }

    private void showDialog(Category category) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        LinearLayout deleteLayout = dialog.findViewById(R.id.layout_delete);
        LinearLayout editLayout = dialog.findViewById(R.id.layout_edit);

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCategory(category, dialog);
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCategory(category);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void deleteCategory(Category category, Dialog dialog) {
        categoryApi = RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        categoryApi.delete(category.getId()).enqueue(new Callback<CategoryMessage>() {
            @Override
            public void onResponse(Call<CategoryMessage> call, Response<CategoryMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryMessage categoryMessage = response.body();
                    Toast.makeText(getActivity(), categoryMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    onResume();
                }
            }

            @Override
            public void onFailure(Call<CategoryMessage> call, Throwable t) {
                // Handle the failure case
                // For example, show a Toast message to the user
            }
        });
    }

    private void editCategory(Category category) {
        Intent intent = new Intent(getActivity(), CategoryFormActivity.class);
        intent.putExtra("category", category);
        Toast.makeText(getActivity(), "click Edit", Toast.LENGTH_SHORT).show();

        startActivity(intent);
    }
}
