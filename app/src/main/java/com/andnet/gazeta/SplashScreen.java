package com.andnet.gazeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * splash screen
 */

public class SplashScreen extends AppCompatActivity {

    public static final String FIRST_TIME_KEY="pref_key_first_time";
    private boolean firstTime=true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SharedPreferences firstTimePreference = getSharedPreferences(FIRST_TIME_KEY,MODE_PRIVATE);
//        firstTime= firstTimePreference.getBoolean(FIRST_TIME_KEY,firstTime);
//        firstTime=true;
//
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();



    }
}
