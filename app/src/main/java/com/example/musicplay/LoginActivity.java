package com.example.musicplay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    EditText edPhone, edPassword;
    UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setEvent();
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
                if(edPhone.getText().toString().isEmpty() || edPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else {
                    String phone = edPhone.getText().toString().trim();
                    String password = edPassword.getText().toString().trim();
                    System.out.println(phone + " " + password);

                    if (!phone.isEmpty() && !password.isEmpty()) {
                        userApi = RetrofitClient.getInstance().getRetrofit().create(UserApi.class);
                        userApi.login(phone, password).enqueue(new Callback<UserMessage>() {
                            @Override
                            public void onResponse(Call<UserMessage> call, Response<UserMessage> response) {
                                UserMessage userLogin = response.body();
//                                                                System.out.println("-----------------");
//                                                                System.out.println("response" + userLogin.getUser().getRole());
                                if (userLogin.getUser() != null) {
                                    System.out.println("----------" + userLogin.getUser().getFirst_name());
                                    // Xử lý kết quả trả về nếu thành công
                                    Toast.makeText(LoginActivity.this, userLogin.getMessage(), Toast.LENGTH_SHORT).show();
                                    User user = userLogin.getUser();
                                    System.out.println("-----------------");
                                    System.out.println(user.getRole());
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                    //                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    //                                startActivity(intent);
                                    if (user.getRole().equals("user")) {
                                        System.out.println("is user");
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        System.out.println("is admin");
                                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    // Xử lý lỗi nếu kết quả trả về không thành công
                                    Toast.makeText(LoginActivity.this, userLogin.getMessage(), Toast.LENGTH_SHORT).show();
                                    //                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    //                                startActivity(intent);0383738128, 0383738129
                                }
                            }

                            @Override
                            public void onFailure(Call<UserMessage> call, Throwable t) {
                                // Xử lý lỗi nếu có lỗi xảy ra trong quá trình gọi API
                                System.out.println(t);
                            }
                        });
                    }
                }
            }
        });
    }

    private void init() {
        tvToRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);
        edPhone = findViewById(R.id.edLoginPhone);
        edPassword = findViewById(R.id.edLoginPW);
    }
}
