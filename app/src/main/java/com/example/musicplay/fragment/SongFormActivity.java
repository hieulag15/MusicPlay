package com.example.musicplay.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplay.RealPathUtil.RealPathUtil;
import com.example.musicplay.adapter.CategorySpinnerAdapter;
import com.example.musicplay.api.CategoryApi;
import com.example.musicplay.api.SongApi;
import com.example.musicplay.asset.LoadingDialog;
import com.example.musicplay.domain.Category;
import com.example.musicplay.domain.Song;
import com.example.musicplay.domain.SongMessage;
import com.example.musicplay.domain.SongUpdate;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplayer.R;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongFormActivity extends AppCompatActivity {
    Button btnSubmit, btnCancel;
    ImageButton btnChooseSong, btnChooseImg;
    TextView tvTitle, tvSongLink, tvSongImg;
    EditText edName, edAuthor, edSinger;
    Boolean isEditForm;
    int currentPosition;
    Song data, obj;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_MP3_REQUEST = 2;
    private Uri mImageUri, mSongUri;

    File fileImage, fileMp3;
    Spinner spCategory;
    CategoryApi categoryApi;
    List<Category> categories;
    CategorySpinnerAdapter spinneradapter;

    SongApi songApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_song);

        init();
    }

    private void setEvent() {
        if(isEditForm == false) {
            btnChooseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImage();
                }
            });

            btnChooseSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseSong();
                }
            });
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edName.getText().toString().isEmpty() || edAuthor.getText().toString().isEmpty() ||
                        edSinger.getText().toString().isEmpty() ||tvSongLink.getText().toString().equals("Chưa có bài hát")||
                        tvSongImg.getText().toString().equals("Chọn avatar"))
                {
                    Toast.makeText(SongFormActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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
//        Check: is Edit Form or Add Form
        if(data != null){
            setSpinnerEdit();
            isEditForm = true;
            tvTitle.setText(getResources().getString(R.string.add_song));
            edName.setText(data.getName());
            edAuthor.setText(data.getAuthor());
            edSinger.setText(data.getSinger());
            tvSongImg.setText(data.getImage());
            tvSongLink.setText(data.getLink());
            btnSubmit.setText(getResources().getString(R.string.add));
        } else{
            setSpinnerAdd();
            isEditForm = false;
            tvTitle.setText(getResources().getString(R.string.add_song));
//            edName.setText("");
//            edAuthor.setText("");
//            edSinger.setText("");
            tvSongImg.setText(getResources().getString(R.string.choose_img));
            tvSongLink.setText(getResources().getString(R.string.choose_link));
            btnSubmit.setText(getResources().getString(R.string.add));
        }
    }
    private void setSpinnerEdit() {
        spCategory = findViewById(R.id.spCategory);
        categoryApi= RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        List<Category> categoryList;
        categoryApi.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categories = response.body();
                spinneradapter = new CategorySpinnerAdapter(categories,SongFormActivity.this);
                spCategory.setAdapter(spinneradapter);
                int i =0,pos =0;
                for(Category category:categories)
                {
                    if(category.getName().equals(data.getCategory().getName())){
                        pos = i;
                    }
                    i++;
                }
                spCategory.setSelection(pos);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }
    private void setSpinnerAdd() {
        spCategory = findViewById(R.id.spCategory);
        categoryApi= RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        categoryApi.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categories = response.body();
                spinneradapter = new CategorySpinnerAdapter(categories,SongFormActivity.this);
                spCategory.setAdapter(spinneradapter);
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    private void init(){
        btnChooseImg = findViewById(R.id.btnUpSongImg);
        btnChooseSong = findViewById(R.id.btnUpSongLink);
        btnSubmit = findViewById(R.id.btnSongSubmit);
        btnCancel = findViewById(R.id.btnSongCancel);
        edName = findViewById(R.id.edSongName);
        edAuthor = findViewById(R.id.edSongAuthor);
        edSinger = findViewById(R.id.edSongSinger);
        tvTitle = findViewById(R.id.tvTitleSong);
        tvSongImg = findViewById(R.id.tvSongImg);
        tvSongLink = findViewById(R.id.tvSongLink);

        Intent intent = getIntent();
        data = (Song) intent.getSerializableExtra("data");
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
            tvSongImg.setText(fileImage.getName());
        } else if (requestCode == PICK_MP3_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mSongUri = data.getData();
            String IMAGE_PATH= RealPathUtil.getRealPath(this,mSongUri);
            // Handle the selected MP3 file here
            fileMp3 = new File(IMAGE_PATH);
            tvSongLink.setText(fileMp3.getName());
        }
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void chooseSong() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(intent, PICK_MP3_REQUEST);
    }

    private void submit(){
        int position = spCategory.getSelectedItemPosition();
        songApi= RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
        obj = new Song();
        obj.setName(String.valueOf(edName.getText()));
        obj.setAuthor(String.valueOf(edAuthor.getText()));
        obj.setSinger(String.valueOf(edSinger.getText()));
        obj.setCategory((Category) spinneradapter.getItem(position));
        System.out.println("------------"+position);
        System.out.println("------------"+obj.getCategory().getName());
//        obj.setImage(String.valueOf(tvSongImg.getText()));
//        obj.setLink(String.valueOf(tvSongLink.getText()));

        if(isEditForm){
            edit();
        } else {
            add();
        }
    }

    private void edit(){
        Long id = data.getId();

        SongUpdate songUpdate = new SongUpdate();
        songUpdate.setAuthor(obj.getAuthor());
        songUpdate.setName(obj.getName());
        songUpdate.setSinger(obj.getSinger());
        songUpdate.setCategory(obj.getCategory());
        songApi.update(id, songUpdate).enqueue(new Callback<SongMessage>() {
            @Override
            public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                System.out.println("---------------");
                Toast.makeText(SongFormActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT);
//                System.out.println(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SongMessage> call, Throwable t) {

            }
        });
    }

    private void add() {
        System.out.println("------------11111");
        int position = spCategory.getSelectedItemPosition();
        // Tạo một đối tượng RequestBody từ tệp tin
        RequestBody requestFileImage=RequestBody.create(MediaType.parse("multipart/form-data"), fileImage);
        RequestBody requestFileMp3=RequestBody.create(MediaType.parse("multipart/form-data"), fileMp3);

        // Tạo một đối tượng MultipartBody.Part từ RequestBody
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", fileImage.getName(), requestFileImage);
        MultipartBody.Part mp3 = MultipartBody.Part.createFormData("file", fileMp3.getName(), requestFileMp3);

        String songName = edName.getText().toString().trim();
        RequestBody requestSongName = RequestBody.create(MediaType.parse("text/plain"), songName);

        String author = edAuthor.getText().toString().trim();
        RequestBody requestAuthor = RequestBody.create(MediaType.parse("text/plain"), author);

        String singer = edSinger.getText().toString().trim();
        RequestBody requestSinger = RequestBody.create(MediaType.parse("text/plain"), singer);

        Category selectedCategory = (Category) spinneradapter.getItem(position);
        Long id  = selectedCategory.getId();
        RequestBody requestIdCategory = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));

        songApi = RetrofitClient.getRetrofit().create(SongApi.class);
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        songApi.createSong(mp3, image, requestSongName, requestAuthor, requestSinger, requestIdCategory).enqueue(new Callback<SongMessage>() {
            @Override
            public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                SongMessage songMessage = response.body();
                System.out.println(songMessage.getMessage());
                Toast.makeText(SongFormActivity.this, songMessage.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.cancel();
                finish();
            }

            @Override
            public void onFailure(Call<SongMessage> call, Throwable t) {
                    loadingDialog.cancel();
                    finish();
            }
        });
    }
}
