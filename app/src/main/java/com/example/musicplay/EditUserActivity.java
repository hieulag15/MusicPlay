package com.example.musicplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplay.api.UserApi;
import com.example.musicplay.domain.User;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {
    EditText edFirstName, edLastname, edEmail, edPhone, edPassword;
    Button btnSubmit, btnCancel;
    User user;
    UserApi userApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        init();
        loadData();
        setEvent();
    }

    private void init() {
        edFirstName = findViewById(R.id.edFirstName);
        edLastname = findViewById(R.id.edLastname);
        edEmail = findViewById(R.id.edEmail);
        edPhone = findViewById(R.id.edPhone);
        edPassword = findViewById(R.id.edPassword);
        btnSubmit = findViewById(R.id.btnEditUserSubmit);
        btnCancel = findViewById(R.id.btnEditUserCancel);
    }

    private void loadData() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("data");
        edFirstName.setText(user.getFirst_name());
        edLastname.setText(user.getLast_name());
        edEmail.setText(user.getEmail());
        edPhone.setText(user.getPhone());
        edPassword.setText(user.getPassword());
    }

    private void setEvent() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edFirstName.getText().toString().isEmpty() || edLastname.getText().toString().isEmpty() ||
                        edEmail.getText().toString().isEmpty() ||edPassword.getText().toString().isEmpty() )
                {
                    Toast.makeText(EditUserActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }else {
                    submit();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
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
        userUpdate.setLast_name(edLastname.getText().toString());
        userUpdate.setEmail(edEmail.getText().toString());
        userUpdate.setPassword(edPassword.getText().toString());

        Long id = userUpdate.getId();

        userApi.update(id, userUpdate).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUserActivity.this, "Thành Công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}
