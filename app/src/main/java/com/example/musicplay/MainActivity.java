package com.example.musicplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}