package com.example.musicplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.musicplay.api.UserApi;
import com.example.musicplay.domain.User;
import com.example.musicplay.fragment.SongManagerFragment;
import com.example.musicplay.fragment.UserManagerFragment;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    EditText edFirstName, ed_lastname, edEmail, edPhone, edPassword;

    Button btnSubmit;
    TextView tvCancel;

    User user;

    UserApi userApi;

    private int defaultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Intent intent = getIntent();
        defaultFragment = intent.getIntExtra("valueFragment", 1);
        init();
    }


    private void init() {
        edFirstName =findViewById(R.id.edFirstName);
        ed_lastname = findViewById(R.id.edLastname);
        edEmail = findViewById(R.id.edEmail);
        edPhone = findViewById(R.id.edPhone);
        edPassword = findViewById(R.id.edPassword);
        btnSubmit = findViewById(R.id.btnEditUserSubmit);
        tvCancel = findViewById(R.id.btnEditUserCancel);
        loadData();
        setEvent();
    }
    private void loadData(){
        Intent intent = getIntent();
        user =(User) intent.getSerializableExtra("user");
        edFirstName.setText(user.getFirst_name());
        ed_lastname.setText(user.getLast_name());
        edEmail.setText(user.getEmail());
        edPhone.setText(user.getPhone());
        edPassword.setText(user.getPassword());
    }
    private void setEvent() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edFirstName.getText().toString().isEmpty() || ed_lastname.getText().toString().isEmpty() ||
                        edEmail.getText().toString().isEmpty() ||edPassword.getText().toString().isEmpty() )
                {
                    Toast.makeText(EditUserActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }else {
                    submit();
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void submit() {
        userApi = RetrofitClient.getInstance().getRetrofit().create(UserApi.class);
        User userUpdate = user;
        userUpdate.setFirst_name(edFirstName.getText().toString());
        userUpdate.setLast_name(ed_lastname.getText().toString());
        userUpdate.setEmail(edEmail.getText().toString());
        userUpdate.setPassword(edPassword.getText().toString());

        Long id = userUpdate.getId();

        userApi.update(id, userUpdate).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUserActivity.this, "Thành Công!", Toast.LENGTH_SHORT).show();

                    if (defaultFragment == 1) {
                        Intent form = new Intent(EditUserActivity.this, LoginActivity.class);
                        startActivity(form);
                    }

                    else {
                        Intent intent = new Intent(EditUserActivity.this, AdminActivity.class);
                        intent.putExtra("valueDefualt", 3);
                        startActivity(intent);
                    }

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}