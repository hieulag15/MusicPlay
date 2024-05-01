package com.example.musicplay;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
    private Button btnSubmit, btnCancel;
    private ImageButton btnChooseImg, btnChooseSong;
    private TextView tvTitle, tvSongLink, tvSongImg;
    private EditText edName, edAuthor, edSinger;
    private Boolean isEditForm;
    private Song song, obj;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_MP3_REQUEST = 2;
    private Uri mImageUri, mSongUri;
    private File fileImage, fileMp3;
    private Spinner spCategory;
    private CategoryApi categoryApi;
    private List<Category> categoryList;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private SongApi songApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_song);

        init();
    }

    private void setEvent() {
        if (isEditForm == false) {
            btnChooseImg.setOnClickListener(view -> chooseImage());
            btnChooseSong.setOnClickListener(view -> chooseSong());
        }
        btnSubmit.setOnClickListener(view -> submit());
        btnCancel.setOnClickListener(view -> finish());
    }

    private void loadData() {
        // nếu có dữ liệu là form edit còn không có dữ liệu là form add
        if (song != null) {
            setSpinerEdit();
            isEditForm = true;
            tvTitle.setText(getResources().getString(R.string.edit_song));
            edName.setText(song.getName());
            edAuthor.setText(song.getAuthor());
            edSinger.setText(song.getSinger());
            tvSongLink.setText(song.getLink());
            tvSongImg.setText(song.getImage());
            btnSubmit.setText(getResources().getString(R.string.edit));
        } else {
            setSpinerAdd();
            isEditForm = false;
            tvTitle.setText(getResources().getString(R.string.add_song));
            edName.setText("");
            edAuthor.setText("");
            edSinger.setText("");
            tvSongLink.setText(getResources().getString(R.string.choose_link));
            tvSongImg.setText(getResources().getString(R.string.choose_img));
            btnSubmit.setText(getResources().getString(R.string.add));
        }
    }
    private void setSpinerEdit() {
        spCategory = findViewById(R.id.spCategory);
        categoryApi = RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        categoryApi.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    categorySpinnerAdapter = new CategorySpinnerAdapter(categoryList, SongFormActivity.this);
                    spCategory.setAdapter(categorySpinnerAdapter);
                    int i = 0, pos = 0;
                    for (Category category : categoryList) {
                        if (category.getName().equals(song.getCategory().getName())) {
                            pos = i;
                        }
                        i++;
                    }
                    spCategory.setSelection(pos);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
            }
        });
    }
    private void setSpinerAdd() {
        spCategory = findViewById(R.id.spCategory);
        categoryApi = RetrofitClient.getInstance().getRetrofit().create(CategoryApi.class);
        categoryApi.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    categorySpinnerAdapter = new CategorySpinnerAdapter(categoryList, SongFormActivity.this);
                    spCategory.setAdapter(categorySpinnerAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
            }
        });
    }

    private void init() {
        btnChooseImg = findViewById(R.id.btnUpSongImg);
        btnChooseSong = findViewById(R.id.btnUpSongLink);
        btnSubmit = findViewById(R.id.btnSongSubmit);
        btnCancel = findViewById(R.id.btnSongCancel);
        tvTitle = findViewById(R.id.tvTitleSong);
        tvSongLink = findViewById(R.id.tvSongLink);
        tvSongImg = findViewById(R.id.tvSongImg);
        edName = findViewById(R.id.edSongName);
        edAuthor = findViewById(R.id.edSongAuthor);
        edSinger = findViewById(R.id.edSongSinger);
        spCategory = findViewById(R.id.spCategory);
        Intent intent = getIntent();
        song = (Song) intent.getSerializableExtra("song");
        loadData();
        setEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            String IMAGE_PATH = RealPathUtil.getRealPath(this, mImageUri);
            if (IMAGE_PATH != null) {
                fileImage = new File(IMAGE_PATH);
                tvSongImg.setText(fileImage.getName());
            } else {
                Toast.makeText(this, "Unable to access the selected file", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_MP3_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mSongUri = data.getData();
            String IMAGE_PATH = RealPathUtil.getRealPath(this, mSongUri);
            if (IMAGE_PATH != null) {
                fileMp3 = new File(IMAGE_PATH);
                tvSongLink.setText(fileMp3.getName());
            } else {
                Toast.makeText(this, "Unable to access the selected file", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void chooseSong() {
        Intent intent = new Intent();
        intent.setType("audio/mpeg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_MP3_REQUEST);
    }

    private void submit() {
        if(edName.getText().toString().isEmpty() || edAuthor.getText().toString().isEmpty() ||
                edSinger.getText().toString().isEmpty() ||tvSongLink.getText().toString().equals("Chưa có bài hát")||
                tvSongImg.getText().toString().equals("Chọn avatar"))
        {
            Toast.makeText(SongFormActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }else {
            int position = spCategory.getSelectedItemPosition();
            songApi = RetrofitClient.getInstance().getRetrofit().create(SongApi.class);
            obj = new Song();
            obj.setName(String.valueOf(edName.getText()));
            obj.setAuthor(String.valueOf(edAuthor.getText()));
            obj.setSinger(String.valueOf(edSinger.getText()));
            obj.setLink(tvSongLink.getText().toString());
            obj.setImage(tvSongImg.getText().toString());
            obj.setCategory((Category) categorySpinnerAdapter.getItem(position));
            if (isEditForm) {
                edit();
            } else {
                add();
            }
        }
    }

    private void edit(){
        Long id = song.getId();
        SongUpdate songUpdate = new SongUpdate();
        songUpdate.setAuthor(obj.getAuthor());
        songUpdate.setName(obj.getName());
        songUpdate.setSinger(obj.getSinger());
        songUpdate.setCategory(obj.getCategory());
        songApi.update(id, songUpdate).enqueue(new Callback<SongMessage>() {
            @Override
            public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                if (response.isSuccessful()) {
                    SongMessage songMessage = response.body();
                    Toast.makeText(SongFormActivity.this, songMessage.getMessage(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SongFormActivity.this, AdminActivity.class);
                    intent.putExtra("valueDefualt", 1);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<SongMessage> call, Throwable t) {
            }
        });
    }

    private void add() {
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
        Category selectedCategory = (Category) categorySpinnerAdapter.getItem(position);
        Long id  = selectedCategory.getId();
        RequestBody requestIdCategory = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));
        songApi = RetrofitClient.getRetrofit().create(SongApi.class);
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        songApi.createSong(mp3, image, songName, author, singer, id).enqueue(new Callback<SongMessage>() {
            @Override
            public void onResponse(Call<SongMessage> call, Response<SongMessage> response) {
                if (response.isSuccessful()) {
                    SongMessage songMessage = response.body();
                    Toast.makeText(SongFormActivity.this, songMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(songMessage.getMessage());
                    loadingDialog.cancel();

                    Intent intent = new Intent(SongFormActivity.this, AdminActivity.class);
                    intent.putExtra("valueDefualt", 1);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<SongMessage> call, Throwable t) {
                    loadingDialog.cancel();
                Toast.makeText(SongFormActivity.this, "faild", Toast.LENGTH_SHORT).show();
                Log.e("SongFormActivity", "Failed to add song", t);
                finish();
            }
        });
    }
}
