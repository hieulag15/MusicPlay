package com.example.musicplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.musicplay.fragment.CategoryManagerFragment;
import com.example.musicplay.fragment.SongManagerFragment;
import com.example.musicplay.fragment.UserManagerFragment;
import com.example.musicplay.utilities.Utility;
import com.example.musicplayer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    MenuItem menuItemSong,menuItemCategory, menuItemUser;
    ImageButton btnAdd, btnBack;
    Fragment currentFragment, previousFragment;
    TextView tvTitle;
    String title;
    FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        init();
    }

    private void init() {
        tvTitle = findViewById(R.id.tvTitleAdmin);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        menuItemSong = bottomNavigationView.getMenu().findItem(R.id.navigation_song);
        menuItemCategory = bottomNavigationView.getMenu().findItem(R.id.navigation_category);
        menuItemUser = bottomNavigationView.getMenu().findItem(R.id.navigation_user_maneger);

        Utility.setScrollText(tvTitle);

        fm = getSupportFragmentManager();
        loadBottomNavigationView();
    }

    private void loadData() {
        previousFragment = currentFragment;
        currentFragment = fm.findFragmentById(R.id.container_admin);
        setTitle();
        setEvent();
    }

    private void setTitle() {
        if (currentFragment instanceof SongManagerFragment) {
            title = "Danh sách bài hát";
            btnBack.setImageResource(R.drawable.ic_song);
        } else if (currentFragment instanceof CategoryManagerFragment) {
            title = "Danh sách danh muc";
            btnBack.setImageResource(R.drawable.ic_category);
        } else if (currentFragment instanceof UserManagerFragment) {
            title = "Danh sách người dùng";
            btnBack.setImageResource(R.drawable.ic_user);
        } else {
            title = "Song Manager";
        }
        tvTitle.setText(title);
    }

    private void setEvent() {
        btnAdd.setOnClickListener(v -> {
            Intent intent;
            if (currentFragment instanceof SongManagerFragment) {
                intent = new Intent(AdminActivity.this, SongFormActivity.class);
                startActivity(intent);
            } else if (currentFragment instanceof CategoryManagerFragment) {
                intent = new Intent(AdminActivity.this, CategoryFormActivity.class);
                startActivity(intent);
//            } else if (currentFragment instanceof UserManagerFragment) {
//                intent = new Intent(AdminActivity.this, UserFormActivity.class);
//                startActivity(intent);
//            }
            }
        });
    }

    private void forwardToFragment(Fragment fragment, String tag) {
        fm.beginTransaction()
                .replace(R.id.container_admin, fragment, tag)
                .commit();
        fm.executePendingTransactions();
        loadData();
    }

    private void loadBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_song:
                    forwardToFragment(new SongManagerFragment(), "SongManagerFragment");
                    return true;
                case R.id.navigation_category:
                    forwardToFragment(new CategoryManagerFragment(), "CategoryManagerFragment");
                    return true;
                case R.id.navigation_user_maneger:
                    forwardToFragment(new UserManagerFragment(), "UserManagerFragment");
                    return true;
            }
            return false;
        });
    }
}