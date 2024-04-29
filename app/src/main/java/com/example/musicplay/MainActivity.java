package com.example.musicplay;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplay.domain.Song;
import com.example.musicplay.domain.User;
import com.example.musicplay.fragment.HomeFragment;
import com.example.musicplay.fragment.LibraryFragment;
import com.example.musicplay.fragment.SearchFragment;
import com.example.musicplay.fragment.SettingFragment;
import com.example.musicplay.fragment.UserFragment;
import com.example.musicplay.utilities.Utility;
import com.example.musicplayer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private int valueMiniplayer, position;
    private TextView tvSongName, tvArtistName;
    private ImageButton btnPlay, btnNext, btnPre;
    private ImageView imgSong;
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private FrameLayout fragementHolder;

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

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

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
                            replaceFragment(new LibraryFragment());
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
            String currentSong = sharedPreferences.getString("songs", null);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Song>>() {}.getType();
            List<Song> songs = gson.fromJson(currentSong, type);
            position = sharedPreferences.getInt("position", 0);

            tvSongName = findViewById(R.id.miniPlayerSongName);
            tvArtistName = findViewById(R.id.miniPlayerArtistName);
            btnPlay = findViewById(R.id.btnPlay);
            btnNext = findViewById(R.id.btnNextSongMiniPlayer);
            btnPre = findViewById(R.id.btnPreviousSongMiniPlayer);
            imgSong = findViewById(R.id.miniPlayerAlbumArt);


            btnPlay.setImageResource(R.drawable.ic_pause);
            System.out.println("Position trong mainactivity: " + position);
            Song song = songs.get(position);
            System.out.println(song.getName() + " " + song.getAuthor() + " " + song.getImage());

            tvSongName.setText(songs.get(position).getName());
            tvArtistName.setText(songs.get(position).getAuthor());
            Picasso.get().load(songs.get(position).getImage()).into(imgSong);

            Utility.setScrollText(tvSongName);
            Utility.setScrollText(tvArtistName);

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playMusic();
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playNextSong(songs);
                }
            });
            btnPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playPreSong(songs);
                }
            });

            miniPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                    intent.putExtra("songs", new Gson().toJson(songs)); // Pass the songs list
                    intent.putExtra("position", position); // Pass the current position of the song
                    intent.putExtra("valuePlayerActivity", 2);
                    startActivity(intent);
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

    private void playNextSong(List<Song> songs) {
        musicService.setSongs(songs);
        musicService.playNextSong();
        updateSongUI();
        position = musicService.getCurrentPositionSong();
    }

    private void playPreSong(List<Song> songs) {
        musicService.setSongs(songs);
        musicService.playPreSong();
        updateSongUI();
        position = musicService.getCurrentPositionSong();
    }

    private void updateSongUI() {
        Song song = musicService.getCurrentSong();
        tvSongName.setText(song.getName());
        tvArtistName.setText(song.getAuthor());
        Picasso.get().load(song.getImage()).into(imgSong);
        btnPlay.setImageResource(R.drawable.ic_pause);
    }
    private void setFragement(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragementHolder.getId(), fragment);
        fragmentTransaction.commit();
    }
}