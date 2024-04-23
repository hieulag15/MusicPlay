package com.example.musicplay;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.musicplay.domain.MusicServiceListener;

public class PlayerActivity extends AppCompatActivity {
    TextView tvSongName, tvEndTime, tvBeginTime, tvSongSinger, tvHeaderTitle;
    CircleImageView imgMusic;
    SeekBar seekBar;
    ImageView btnPre, btnPlay, btnNext, btnfavourite, btnShuffle, btnRepeat, btnBack, btnOption;
    ObjectAnimator objectAnimator;
    static int position, miniValue;
    static boolean isFavorite, isPlaying;
    List<Song> songs;
    FavouriteApi favouriteApi;
    private Boolean isShuffle, isRepeat;
    private ExecutorService executorService;
    private Song currentSong;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        init();
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        miniValue = intent.getIntExtra("valuePlayerActivity", 1);

        if (miniValue == 2) {
            String songsJson = intent.getStringExtra("songs");
            Type type = new TypeToken<List<Song>>() {}.getType();
            songs = new Gson().fromJson(songsJson, type);
            currentSong = songs.get(position);

            isFavorite = false;
            setFavourite(currentSong);
            tvSongName.setText(currentSong.getName());
            tvSongSinger.setText(currentSong.getSinger());
            tvHeaderTitle.setText(currentSong.getCategory().getName());
            tvSongName.setSelected(true);
            btnPlay.setImageResource(R.drawable.ic_pause);
            Picasso.get().load(currentSong.getImage()).into(imgMusic);
            musicBound = true;
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnectionMiniPlayer, Context.BIND_AUTO_CREATE);
            startService(playIntent);
//            musicService.updateSeekBar();
            rontation();
            setEventButton();
        } else {
            songs = Collections.unmodifiableList((List<Song>) Objects.requireNonNull(intent.getSerializableExtra("songs")));
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int currentPosition = intent.getIntExtra("currentPosition", 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(currentPosition);
                        tvBeginTime.setText(musicService.formatTime(currentPosition));
                        seekBar.setMax(musicService.getDuration());
                        TimeSong();
                    }
                });
            }
        };


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
        btnOption = findViewById(R.id.btnOption);
        btnOption.setOnClickListener(view -> {onOptionButtonClicked();});


        Utility.setScrollText(tvSongName);
        Utility.setScrollText(tvSongSinger);
        Utility.setScrollText(tvHeaderTitle);
        Utility.setScrollText(tvBeginTime);
        Utility.setScrollText(tvEndTime);

        isShuffle = false;
        isRepeat = false;

        executorService = Executors.newSingleThreadExecutor();
        isPlaying = false;


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
        musicBound = true;
        if (musicBound) {
            musicService.setSongs(songs);
            musicService.playSong(song.getLink());
            seekBar.setProgress(0);
        }
        rontation();
        musicService.updateSeekBar();

        setEventButton();

    }

    private void onShuffle() {
        if (isShuffle) {
            isShuffle = false;
            btnShuffle.setImageResource(R.drawable.icon_shuffle_white);
        } else {
            isShuffle = true;
            btnShuffle.setImageResource(R.drawable.icon_shuffle_black);
        }
        musicService.setShuffle(isShuffle);
    }

    private void onRepeat() {
        if (isRepeat) {
            isRepeat = false;
            btnRepeat.setImageResource(R.drawable.ic_repeat_white);
        } else {
            isRepeat = true;
            btnRepeat.setImageResource(R.drawable.ic_repeat_black);
        }
        musicService.setRepeat(isRepeat);
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
        if (musicService.isPlaying()) {
            musicService.pause();
            btnPlay.setImageResource(R.drawable.ic_play);
            objectAnimator.pause();
        } else {
            musicService.play();
            btnPlay.setImageResource(R.drawable.ic_pause);
            objectAnimator.resume();
        }
    }

    private void stopMusic() {
        if (musicService.isPlaying()) {
            musicService.pause();
        }
        finish();
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
        User user = SharePrefManager.getInstance(getApplicationContext()).getUser();
        favouriteApi = RetrofitClient.getInstance().getRetrofit().create(FavouriteApi.class);
        Call<FavouriteMessage> call;

        if (isFavorite) {
            call = favouriteApi.deleteFavourite(song.getId(), user.getId());
        } else {
            call = favouriteApi.addFavourite(song.getId(), user.getId());
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

    private void playNextSong(List<Song> songs) {
        musicService.setSongs(songs);
        musicService.playNextSong();
        Song CurrentSong = musicService.getCurrentSong();
        updateUI(CurrentSong);
        position = musicService.getCurrentPositionSong();
    }

    private void playPreSong(List<Song> songs) {
        musicService.setSongs(songs);
        musicService.playPreSong();
        Song CurrentSong = musicService.getCurrentSong();
        updateUI(CurrentSong);
        position = musicService.getCurrentPositionSong();
    }

    private void TimeSong() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {
                    tvEndTime.setText(musicService.formatTime(musicService.getDuration()));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        musicService.stopMusic();
        super.onBackPressed();
    }

    private void valueMiniplayer() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("valueMiniplayer", 2);
        startActivity(intent);
        finish();
    }

    public void onOptionButtonClicked() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlayerState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonSongs = gson.toJson(songs);
        editor.putString("songs", jsonSongs);

        editor.putInt("position", position);
        editor.apply();

        valueMiniplayer();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicBound = true;

            musicService.setPosition(position);
            currentSong = songs.get(position);
            musicService.setSongs(songs);

            playMusic(currentSong);

            musicService.setListener(new MusicServiceListener() {
                @Override
                public void onProgressChanged(int progress) {

                }

                @Override
                public void onStartTrackingTouch() {

                }

                @Override
                public void onStopTrackingTouch() {

                }

                @Override
                public void onCompletion() {

                }

                @Override
                public void onSongChanged(Song song) {
                    updateUI(song);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private ServiceConnection musicConnectionMiniPlayer = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicBound = true;


            musicService.setListener(new MusicServiceListener() {
                @Override
                public void onProgressChanged(int progress) {

                }

                @Override
                public void onStartTrackingTouch() {

                }

                @Override
                public void onStopTrackingTouch() {

                }

                @Override
                public void onCompletion() {

                }

                @Override
                public void onSongChanged(Song song) {
                    updateUI(song);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(MusicService.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void updateUI(Song song) {
        tvSongName.setText(song.getName());
        tvSongSinger.setText(song.getSinger());
        tvHeaderTitle.setText(song.getCategory().getName());
        tvSongName.setSelected(true);
        Picasso.get().load(song.getImage()).into(imgMusic);
        objectAnimator.start();
        btnPlay.setImageResource(R.drawable.ic_pause);
        setFavourite(song);
    }

    private void setEventButton() {
        Song song = songs.get(position);
        btnPlay.setOnClickListener(view -> playMusic());
        btnPre.setOnClickListener(view -> playPreSong(songs));
        btnNext.setOnClickListener(view -> playNextSong(songs));
        btnfavourite.setOnClickListener(view -> favouriteSong(song));
        btnBack.setOnClickListener(view -> stopMusic());

        btnShuffle.setOnClickListener(view -> onShuffle());
        btnRepeat.setOnClickListener(view -> onRepeat());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    musicService.seekTo(i);
                    tvBeginTime.setText(musicService.formatTime(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                musicService.onStartTrackingTouch();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                musicService.onStopTrackingTouch();
            }
        });
    }
}
