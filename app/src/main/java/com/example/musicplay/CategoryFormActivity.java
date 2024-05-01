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
    ImageButton  btnChooseImg;
    Button btnSubmit, btnCancel;
    TextView tvCancel, tvImg, tvTitle, tvSubmit;
    int currentPosition;
    Category data, obj;
    Boolean isEditForm;
    EditText edName, edDescription;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_MP3_REQUEST = 2;
    private Uri mImageUri, mSongUri;

    File fileImage, fileMp3;
    CategoryApi categoryApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_category);
        init();
    }

    private void setEvent() {
        if (isEditForm == false){
            btnChooseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImage();
                }
            });
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edName.getText().toString().isEmpty() || edDescription.getText().toString().isEmpty() ||
                        tvImg.getText().toString().equals("Chọn avatar") )
                {
                    Toast.makeText(CategoryFormActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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

    private void loadData(){
        //   Kiểm tra: nếu có dữ liệu là form edit, khong là form add
        if(data != null){
            isEditForm = true;
            tvTitle.setText(getResources().getString(R.string.edit_category));
            edName.setText(data.getName());
            edDescription.setText(data.getDescription());
            tvImg.setText(data.getImage());
        } else {
            isEditForm = false;
            tvTitle.setText(getResources().getString(R.string.add_category));
            edName.setText("");
            edDescription.setText("");
            tvImg.setText(getResources().getString(R.string.choose_img));
            btnSubmit.setText(getResources().getString(R.string.add_category));
        }
    }

    private void init(){
        btnChooseImg = findViewById(R.id.btnChooseImage);
        btnSubmit = findViewById(R.id.btnCategorySubmit);
        btnCancel = findViewById(R.id.btnCategoryCancel);
        edName = findViewById(R.id.edCategoryName);
        edDescription = findViewById(R.id.edCategoryDescription);
        tvImg = findViewById(R.id.tvCategoryImg);
        tvTitle = findViewById(R.id.tvTitleCategory);

        Intent intent = getIntent();
        data = (Category) intent.getSerializableExtra("data");


        loadData();
        setEvent();
    }

    //Upload Image
    // This method is called when the user selects an image from their device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            String IMAGE_PATH= RealPathUtil.getRealPath(this,mImageUri);
            fileImage = new File(IMAGE_PATH);
            tvImg.setText(fileImage.getName());
        } else if (requestCode == PICK_MP3_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mSongUri = data.getData();
            String IMAGE_PATH= RealPathUtil.getRealPath(this,mSongUri);
            // Handle the selected MP3 file here
            fileMp3 = new File(IMAGE_PATH);
            //tvSongLink.setText(fileMp3.getName());
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void submit(){
        obj = new Category();
        obj.setName(String.valueOf(edName.getText()));
        obj.setDescription(String.valueOf(edDescription.getText()));
        obj.setImage(String.valueOf(tvImg.getText()));

        if(isEditForm){
            edit();
        } else {
            add();
        }
    }

    private void add(){
        //file image
        RequestBody requestFileImage=RequestBody.create(MediaType.parse("multipart/form-data"), fileImage);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", fileImage.getName(), requestFileImage);
        //text
        String name = edName.getText().toString();
        RequestBody requestName = RequestBody.create(MediaType.parse("text/plain"), name);

        String description = edDescription.getText().toString();
        RequestBody requestDescription = RequestBody.create(MediaType.parse("text/plain"), description);

        categoryApi = RetrofitClient.getRetrofit().create(CategoryApi.class);
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        categoryApi.createCategory(requestName, image, requestDescription).enqueue(new Callback<CategoryMessage>() {
            @Override
            public void onResponse(Call<CategoryMessage> call, Response<CategoryMessage> response) {
                CategoryMessage categoryMessage = response.body();
                Toast.makeText(CategoryFormActivity.this, categoryMessage.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.cancel();
                finish();

                Intent intent = new Intent(CategoryFormActivity.this, AdminActivity.class);
                intent.putExtra("valueDefualt", 2);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<CategoryMessage> call, Throwable t) {
                loadingDialog.cancel();
                finish();
            }
        });

    }

    private void edit(){
        categoryApi= RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        Category categoryUpdate = data;
        categoryUpdate.setName(edName.getText().toString());
        categoryUpdate.setDescription(edDescription.getText().toString());

        Long id = categoryUpdate.getId();

        categoryApi.update(id, categoryUpdate).enqueue(new Callback<CategoryMessage>() {
            @Override
            public void onResponse(Call<CategoryMessage> call, Response<CategoryMessage> response) {
                CategoryMessage categoryMessage = response.body();
                Toast.makeText(getApplicationContext(), categoryMessage.getMessage(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CategoryFormActivity.this, AdminActivity.class);
                intent.putExtra("valueDefualt", 2);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<CategoryMessage> call, Throwable t) {
            }
        });
        setEvent();
        loadData();
    }

}
