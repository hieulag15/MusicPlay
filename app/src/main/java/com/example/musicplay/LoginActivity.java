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

import com.example.musicplay.api.UserApi;
import com.example.musicplay.domain.User;
import com.example.musicplay.domain.UserMessage;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextView tvToRegister;
    Button btnLogin;
    EditText etPhone, etPassword;
    UserApi userApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setEvent();
    }

    private void init() {
        tvToRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);
        etPhone = findViewById(R.id.edLoginPhone);
        etPassword = findViewById(R.id.edLoginPW);
    }

    private void setEvent() {
        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPhone.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    String phone = etPhone.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (!phone.isEmpty() && !password.isEmpty()) {
                        userApi = RetrofitClient.getInstance().getRetrofit().create(UserApi.class);
                        userApi.login(phone, password).enqueue(new Callback<UserMessage>() {
                            @Override
                            public void onResponse(Call<UserMessage> call, Response<UserMessage> response) {
                                if (response.isSuccessful()) {
                                    UserMessage userMessage = response.body();
                                    if (userMessage.getUser() != null) {
                                        Toast.makeText(LoginActivity.this, userMessage.getMessage(), Toast.LENGTH_SHORT).show();
                                        User user = userMessage.getUser();
                                        if (user.getRole().equals("admin")) {
                                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, userMessage.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<UserMessage> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            }
        });
    }
}
