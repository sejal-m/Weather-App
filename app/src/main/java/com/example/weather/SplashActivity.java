package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent i = getIntent();
        final String city_name = i.getStringExtra("city_name");

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(1000);  //Delay of 3 seconds
                } catch (Exception e) {
                    Log.v("SplashActivity", "Error: " + e);
                } finally {
                    Intent i = new Intent(SplashActivity.this, SearchActivity.class);
                    i.putExtra("city_name", city_name);
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}