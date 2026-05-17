package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        // Tampilkan HomeFragment pertama kali
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return loadFragment(new HomeFragment());
            } else if (id == R.id.nav_favorite) {
                return loadFragment(new FavoriteFragment());
            } else if (id == R.id.nav_add) {
                return loadFragment(new AddBookFragment());
            }
            return false;
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // Dipanggil dari AddBookFragment setelah berhasil tambah buku
    public void navigateToHome() {
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}
