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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.musicplay.EditUserActivity;
import com.example.musicplay.adapter.UserAdapter;
import com.example.musicplay.api.UserApi;
import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.domain.User;
import com.example.musicplay.domain.UserMessage;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagerFragment extends Fragment {
    private List<User> users;
    private UserApi userApi;
    private View view;
    private int currentPosition;
    private RecyclerView mRecyclerView, headerRecyclerView;
    private UserAdapter userAdapter;
    private LinearLayout deleteLayout, editLayout;
    public UserManagerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_manager, container, false);

        init();
        return view;
    }

    private void init() {
        userApi = RetrofitClient.getInstance().getRetrofit().create(UserApi.class);
        mRecyclerView = view.findViewById(R.id.rcvUserListManager);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadData();
    }

    private void loadData() {
        users = new ArrayList<>();
        userAdapter = new UserAdapter(users);
        mRecyclerView.setAdapter(userAdapter);

        userApi.getAllUser().enqueue(new Callback<UserMessage>() {
            @Override
            public void onResponse(Call<UserMessage> call, Response<UserMessage> response) {
                if (response.isSuccessful()) {
                    users = response.body().getUsers();
                    userAdapter = new UserAdapter(users);
                    mRecyclerView.setAdapter(userAdapter);

                    userAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            User user = users.get(position);
                            showDialog(user);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UserMessage> call, Throwable t) {

            }
        });
    }
    private void showDialog(User user) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        deleteLayout = dialog.findViewById(R.id.layout_delete);
        editLayout = dialog.findViewById(R.id.layout_edit);

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(user, dialog);
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUser(user);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void deleteUser(User user, Dialog dialog) {
        userApi.deleteUser(user.getId()).enqueue(new Callback<UserMessage>() {
            @Override
            public void onResponse(Call<UserMessage> call, Response<UserMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String string = response.body().getMessage();
                    Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    onResume();
                }
            }

            @Override
            public void onFailure(Call<UserMessage> call, Throwable t) {
                // Handle the failure case
                // For example, show a Toast message to the user
            }
        });
    }

    private void editUser(User user) {
        Intent intent = new Intent(getActivity(), EditUserActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("valueFragment", 2);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
