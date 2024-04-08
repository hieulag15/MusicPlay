package com.example.musicplay;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {
    Button btnRegister;
    EditText edFirstName, edLastName, edPhone, edEmail, edPassword, edConfirmPassword;
    UserApi userApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        setEvent();
    }

    private void setEvent() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInput()) {
                    registerUser();
                }
            }
        });
    }

    private boolean validateInput() {
        if(edFirstName.getText().toString().isEmpty() || edLastName.getText().toString().isEmpty() ||
                edEmail.getText().toString().isEmpty() ||edPassword.getText().toString().isEmpty() ||
                edPhone.getText().toString().isEmpty() || edConfirmPassword.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        String password = edPassword.getText().toString().trim();
        String confirmPassword = edConfirmPassword.getText().toString().trim();
        if(!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Add more validation here if needed

        return true;
    }

    private void registerUser() {
        String firstName = edFirstName.getText().toString().trim();
        String lastName = edLastName.getText().toString().trim();
        String phone = edPhone.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        User user = new User(phone, firstName, lastName, email, password);
        Call<UserMessage> call = userApi.register(user);
        call.enqueue(new Callback<UserMessage>() {
            @Override
            public void onResponse(Call<UserMessage> call, Response<UserMessage> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserMessage> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        btnRegister = findViewById(R.id.btnRegister);
        edFirstName = findViewById(R.id.edFirstName);
        edLastName = findViewById(R.id.edLastName);
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);

        userApi = RetrofitClient.getInstance().getRetrofit().create(UserApi.class);
    }
}
