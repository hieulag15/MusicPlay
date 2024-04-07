package com.example.musicplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.musicplay.domain.User;
import com.example.musicplay.fragment.HomeFragment;
import com.example.musicplay.fragment.SearchFragment;
import com.example.musicplay.fragment.SettingFragment;
import com.example.musicplay.fragment.UserFragment;
import com.example.musicplayer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = SharePrefManager.getInstance(getApplicationContext()).getUser();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.navigation_home) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new HomeFragment())
                                    .commit();
                            return true;
                        } else if (item.getItemId() == R.id.navigation_search) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new SearchFragment())
                                    .commit();
                            return true;
                        } else if (item.getItemId() == R.id.navigation_setting) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new SettingFragment())
                                    .commit();
                            return true;
                        } else if (item.getItemId() == R.id.navigation_user) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new UserFragment())
                                    .commit();
                            return true;
                        }
                        return false;
                    }
                }
        );

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new HomeFragment())
                .commit();
    }
}