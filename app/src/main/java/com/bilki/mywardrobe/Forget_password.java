package com.bilki.mywardrobe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Forget_password extends AppCompatActivity {

    Button next, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        next = (Button) findViewById(R.id.next_1);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Forget_password.this, Reset_choose.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
                finish();
            }
        });

        back = (Button) findViewById(R.id.back_3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Forget_password.this, SignIn.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(Forget_password.this , SignIn.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }
}