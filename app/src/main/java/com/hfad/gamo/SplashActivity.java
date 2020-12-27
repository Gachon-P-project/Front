package com.hfad.gamo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.hfad.gamo.Component;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        sharedPreferences.Editor

        Component.sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);
        Component.default_url = "http://192.168.50.146:17394";
//        Component.default_url = "http://172.30.1.2:17394";


        Intent intent;
        if(Component.sharedPreferences.getBoolean("login", false)) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }  else {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

//        intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();

    }
}
