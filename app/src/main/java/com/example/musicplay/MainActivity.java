package com.example.musicplay;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.musicplay.domain.User;
import com.example.musicplay.fragment.HomeFragment;
import com.example.musicplay.fragment.SearchFragment;
import com.example.musicplay.fragment.SettingFragment;
import com.example.musicplay.fragment.UserFragment;
import com.example.musicplayer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private int valueMiniplayer;
    private TextView tvSongName, tvArtistName;
    private ImageButton btnPlay;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        valueMiniplayer = intent.getIntExtra("valueMiniplayer", 1);
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

        User user = SharePrefManager.getInstance(getApplicationContext()).getUser();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.navigation_home) {
                            replaceFragment(new HomeFragment());
                            return true;
                        } else if (item.getItemId() == R.id.navigation_search) {
                            replaceFragment(new SearchFragment());
                            return true;
                        } else if (item.getItemId() == R.id.navigation_setting) {
                            replaceFragment(new SettingFragment());
                            return true;
                        } else if (item.getItemId() == R.id.navigation_user) {
                            replaceFragment(new UserFragment());
                            return true;
                        }
                        return false;
                    }
                }
        );

        replaceFragment(new HomeFragment());

        if (valueMiniplayer == 2) {
            View miniPlayer = this.findViewById(R.id.miniPlayer);
            miniPlayer.setVisibility(View.VISIBLE);

            // Check if there is a song playing
            SharedPreferences sharedPreferences = getSharedPreferences("PlayerState", MODE_PRIVATE);
            String currentSong = sharedPreferences.getString("currentSong", null);
            int position = sharedPreferences.getInt("position", 0);

            tvSongName = findViewById(R.id.miniPlayerSongName);
            tvArtistName = findViewById(R.id.miniPlayerArtistName);
            btnPlay = findViewById(R.id.miniPlayerPlayPauseButton);


            btnPlay.setImageResource(R.drawable.ic_pause);
            tvSongName.setText(currentSong);


            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playMusic();
                }
            });
        } else {
            View miniPlayer = this.findViewById(R.id.miniPlayer);
            miniPlayer.setVisibility(View.GONE);
        }


    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    public void showMiniPlayer() {
//        SharedPreferences sharedPreferences = getSharedPreferences("PlayerState", MODE_PRIVATE);
//        String currentSong = sharedPreferences.getString("currentSong", null);
//        int position = sharedPreferences.getInt("position", 0);
//
//        if (currentSong != null) {
//            // Hiển thị MiniPlayer với thông tin về bài hát và các nút điều khiển
//            // Tiếp tục phát bài hát từ vị trí đã lưu
//            mediaPlayer.seekTo(position);
//            mediaPlayer.start();
//        }
//    }

    private void playMusic() {
        if (musicService.isPlaying()) {
            musicService.pause();
            btnPlay.setImageResource(R.drawable.ic_play);
        } else {
            musicService.play();
            btnPlay.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicBound) {
            unbindService(musicConnection);
            musicBound = false;
        }
    }
}