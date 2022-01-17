package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;

public class Closet extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Camera_fragment camera_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.closet_layout, new Closet_fragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.bot_nav_closet);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()){

                    case R.id.bot_nav_closet:
                        item.setChecked(true);
                        fragment = new Closet_fragment();
                        break;
                    case R.id.bot_nav_looks:
                        item.setChecked(true);
                        fragment = new Looks_fragment();
                        break;
                    case R.id.bot_nav_camera:
                        item.setChecked(true);
                        fragment = new Camera_fragment();
                        break;

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.closet_layout, fragment).commit();

                return false;
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(Closet.this, MainActivity.class);
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }


}