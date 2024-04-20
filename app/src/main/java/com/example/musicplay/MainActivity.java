package com.example.musicplay;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        valueMiniplayer = intent.getIntExtra("valueMiniplayer", 1);

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
        } else {
            View miniPlayer = this.findViewById(R.id.miniPlayer);
            miniPlayer.setVisibility(View.GONE);
        }
    }

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
}