package com.example.musicplay;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplay.SQLite.DatabaseHelper;
import com.example.musicplay.api.FavouriteApi;
import com.example.musicplay.domain.FavouriteMessage;
import com.example.musicplay.domain.Song;
import com.example.musicplay.domain.User;
import com.example.musicplay.retrofit.RetrofitClient;
import com.example.musicplay.utilities.Utility;
import com.example.musicplayer.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity {
    TextView tvSongName, tvEndTime, tvBeginTime, tvSongSinger, tvHeaderTitle;
    CircleImageView imgMusic;
    SeekBar seekBar;
    ImageView btnPre, btnPlay, btnNext, btnfavourite, btnShuffle, btnRepeat, btnBack;
    ObjectAnimator objectAnimator;
    MediaPlayer mediaPlayer;
    static int position;
    static boolean isFavorite;
    List<Song> songs;
    FavouriteApi favouriteApi;
    private Boolean isShuffle, isRepeat;
    private ExecutorService executorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        songs = (List<Song>) intent.getSerializableExtra("songs");
        Song songCurrent = songs.get(position);
        init();
        playMusic(songCurrent);
    }

    private void init() {
        tvSongName = findViewById(R.id.tvSongName);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvBeginTime = findViewById(R.id.tvBeginTime);
        tvSongSinger = findViewById(R.id.tvSongSinger);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        imgMusic = findViewById(R.id.imgMusic);
        seekBar = findViewById(R.id.seekBar);
        btnPre = findViewById(R.id.btnPrevious);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnfavourite = findViewById(R.id.btnFavorite);
        btnShuffle = findViewById(R.id.btnSuffle);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnBack = findViewById(R.id.btnBack);

        Utility.setScrollText(tvSongName);
        Utility.setScrollText(tvSongSinger);
        Utility.setScrollText(tvHeaderTitle);
        Utility.setScrollText(tvBeginTime);
        Utility.setScrollText(tvEndTime);

        isShuffle = false;
        isRepeat = false;

        executorService = Executors.newSingleThreadExecutor();
    }

    private void playMusic(Song song) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.addData(song.getId());

        isFavorite = false;
        setFavourite(song);
        tvSongName.setText(song.getName());
        tvSongSinger.setText(song.getSinger());
        tvHeaderTitle.setText(song.getCategory().getName());
        tvSongName.setSelected(true);
        btnPlay.setImageResource(R.drawable.ic_pause);
        Picasso.get().load(song.getImage()).into(imgMusic);

        PlayMp3(song);

        rontation();
        UpdateSeekBar();

        btnPlay.setOnClickListener(view -> playMusic());
        btnPre.setOnClickListener(view -> playPreSong(songs));
        btnNext.setOnClickListener(view -> playNextSong(songs));
        btnfavourite.setOnClickListener(view -> favouriteSong(song));
        btnBack.setOnClickListener(view -> finish());

        btnShuffle.setOnClickListener(view -> onShuffle());
        btnRepeat.setOnClickListener(view -> onRepeat());
    }

    private void onShuffle() {
        if (isShuffle) {
            isShuffle = false;
            btnShuffle.setImageResource(R.drawable.icon_shuffle_white);
        } else {
            isShuffle = true;
            btnShuffle.setImageResource(R.drawable.icon_shuffle_black);
        }
    }

    private void onRepeat() {
        if (isRepeat) {
            isRepeat = false;
            btnRepeat.setImageResource(R.drawable.ic_repeat_white);
        } else {
            isRepeat = true;
            btnRepeat.setImageResource(R.drawable.ic_repeat_black);
        }
    }

    private void rontation() {
        imgMusic.setRotation(45);
        objectAnimator = ObjectAnimator.ofFloat(imgMusic, "rotation", 0f, 360f);
        objectAnimator.setDuration(10000);
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    private void playMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlay.setImageResource(R.drawable.ic_play);
            objectAnimator.pause();
        } else {
            mediaPlayer.start();
            btnPlay.setImageResource(R.drawable.ic_pause);
            objectAnimator.resume();
        }
    }

    private void setFavourite(Song song) {
        User user = SharePrefManager.getInstance(getApplicationContext()).getUser();
        favouriteApi = RetrofitClient.getInstance().getRetrofit().create(FavouriteApi.class);
        favouriteApi.findFavourite(song.getId(), user.getId()).enqueue(new Callback<FavouriteMessage>() {
            @Override
            public void onResponse(Call<FavouriteMessage> call, Response<FavouriteMessage> response) {
                if (response.isSuccessful()) {
                    FavouriteMessage favouriteMessage = response.body();
                    if (favouriteMessage.getMessage().equals("Successful")) {
                        isFavorite = true;
                        btnfavourite.setImageResource(R.drawable.ic_favorite_black);
                    } else {
                        isFavorite = false;
                        btnfavourite.setImageResource(R.drawable.ic_favorite_white);
                    }
                }
            }

            @Override
            public void onFailure(Call<FavouriteMessage> call, Throwable t) {

            }
        });
    }

    private void favouriteSong(Song song) {
        System.out.println("isFavorite: " + isFavorite);
        User user = SharePrefManager.getInstance(getApplicationContext()).getUser();
        favouriteApi = RetrofitClient.getInstance().getRetrofit().create(FavouriteApi.class);
        Call<FavouriteMessage> call;

        if (isFavorite) {
            call = favouriteApi.deleteFavourite(song.getId(), user.getId());
            System.out.println("delete");
        } else {
            call = favouriteApi.addFavourite(song.getId(), user.getId());
            System.out.println("add");
        }

        call.enqueue(new Callback<FavouriteMessage>() {
            @Override
            public void onResponse(Call<FavouriteMessage> call, Response<FavouriteMessage> response) {
                if (response.isSuccessful()) {
                    System.out.println("success");
                    FavouriteMessage favouriteMessage = response.body();
                    if (favouriteMessage.getMessage().equals("Successful")) {
                        isFavorite = !isFavorite;
                        btnfavourite.setImageResource(isFavorite ? R.drawable.ic_favorite_black : R.drawable.ic_favorite_white);
                    }
                } else {
                    // Handle the case when the response is not successful
                    // For example, show a Toast message to the user
                }
            }

            @Override
            public void onFailure(Call<FavouriteMessage> call, Throwable t) {
                // Handle the failure case
                // For example, show a Toast message to the user
            }
        });
    }

    private void UpdateSeekBar() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    tvBeginTime.setText(formatTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private String formatTime(int timeInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(timeInMillis);
    }

    private void PlayMp3(Song song) {
        executorService.execute(() -> {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(song.getLink());
                mediaPlayer.prepare();
                mediaPlayer.start();

                // Set seekBar after mediaPlayer is prepared
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (isRepeat) {
                            seekBar.setProgress(0);
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                        } else {
                            if (isShuffle) {
                                playNextSong(songs);
                            } else {
                                playNextSong(songs);
                            }
                        }
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (b) {
                            mediaPlayer.seekTo(i);
                            tvBeginTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                TimeSong();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void playNextSong(List<Song> songs) {
        if (position < songs.size() - 1) {
            mediaPlayer.stop();
            position++;
        } else {
            mediaPlayer.stop();
            position = 0;
        }
        objectAnimator.pause();
        playMusic(songs.get(position));
    }

    private void playPreSong(List<Song> songs) {
        if (position > 0) {
            mediaPlayer.stop();
            position--;
        } else {
            mediaPlayer.stop();
            position = songs.size() - 1;
        }
        objectAnimator.pause();
        mediaPlayer.stop();
        playMusic(songs.get(position));
    }

    private void TimeSong() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvEndTime.setText(formatTime(mediaPlayer.getDuration()));
            }
        });
    }

}
