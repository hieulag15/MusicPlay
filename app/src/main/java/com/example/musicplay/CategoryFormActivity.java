package com.example.musicplay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplay.RealPathUtil.RealPathUtil;
import com.example.musicplay.api.CategoryApi;
import com.example.musicplay.asset.LoadingDialog;
import com.example.musicplay.domain.Category;
import com.example.musicplay.domain.CategoryMessage;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFormActivity extends AppCompatActivity {
    private ImageButton btnImg;
    private Button btnSubmit, btnCancel;
    private TextView tvImg, tvTitle;
    private Category category, obj;
    private Boolean isEditForm;
    private EditText edName, edDescription;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_MP3_REQUEST = 2;
    private Uri mImageUri, mSongUri;
    private File fileImage, fileMp3;
    private CategoryApi categoryApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_category);
        init();
    }

    private void init() {
        btnImg = findViewById(R.id.btnChooseImage);
        btnSubmit = findViewById(R.id.btnCategorySubmit);
        btnCancel = findViewById(R.id.btnCategoryCancel);
        tvImg = findViewById(R.id.tvCategoryImg);
        tvTitle = findViewById(R.id.tvTitleCategory);
        edName = findViewById(R.id.edCategoryName);
        edDescription = findViewById(R.id.edCategoryDescription);

        Intent intent = getIntent();
        category = (Category) intent.getSerializableExtra("category");

        loadData();
        setEvent();
    }

    private void loadData() {
        // nếu có dữ liệu là form edit còn không có dữ liệu là form add
        if (category != null) {
            isEditForm = true;
            tvTitle.setText(getResources().getString(R.string.edit_category));
            edName.setText(category.getName());
            edDescription.setText(category.getDescription());
            tvImg.setText(category.getImage());
        } else {
            isEditForm = false;
            tvTitle.setText(getResources().getString(R.string.add_category));
            edName.setText("");
            edDescription.setText("");
            tvImg.setText(getResources().getString(R.string.choose_img));
        }
    }

    private void setEvent() {
        if (isEditForm == false) {
            btnImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImage();
                }
            });
        }

        btnSubmit.setOnClickListener(view -> {
            if (edName.getText().toString().isEmpty() || edDescription.getText().toString().isEmpty() ||
                    tvImg.getText().toString().equals("Chọn avatar")) {
                Toast.makeText(CategoryFormActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                submit();
            }
        });

        btnCancel.setOnClickListener(view -> finish());
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void submit() {
        obj = new Category();
        obj.setName(edName.getText().toString());
        obj.setDescription(String.valueOf(edDescription.getText()));
        obj.setImage(String.valueOf(tvImg.getText()));

        if (isEditForm) {
            edit();
        } else {
            add();
        }
    }

    private void add() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileImage);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", fileImage.getName(), requestBody);

        String name = edName.getText().toString();
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);

        String description = edDescription.getText().toString();
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

        categoryApi = RetrofitClient.getRetrofit().create(CategoryApi.class);

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        categoryApi.createCategory(nameBody, image, descriptionBody).enqueue(new Callback<CategoryMessage>() {
            @Override
            public void onResponse(Call<CategoryMessage> call, Response<CategoryMessage> response) {
                if (response.isSuccessful()) {
                    CategoryMessage categoryMessage = response.body();
                    Toast.makeText(CategoryFormActivity.this, categoryMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.cancel();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CategoryMessage> call, Throwable t) {
                loadingDialog.cancel();
                finish();
            }
        });
    }

    private void edit() {
        categoryApi = RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        Category categoryUpdate = category;
        categoryUpdate.setName(edName.getText().toString());
        categoryUpdate.setDescription(edDescription.getText().toString());

        categoryApi.update(categoryUpdate.getId(), categoryUpdate).enqueue(new Callback<CategoryMessage>() {
            @Override
            public void onResponse(Call<CategoryMessage> call, Response<CategoryMessage> response) {
                if (response.isSuccessful()) {
                    CategoryMessage categoryMessage = response.body();
                    Toast.makeText(CategoryFormActivity.this, categoryMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CategoryMessage> call, Throwable t) {
                finish();
            }
        });
        setEvent();
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            tvImg.setText(fileImage.getName());
            fileImage = new File(RealPathUtil.getRealPath(this, mImageUri));
        } else if (requestCode == PICK_MP3_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mSongUri = data.getData();
            fileMp3 = new File(RealPathUtil.getRealPath(this, mSongUri));
        }
    }
}
