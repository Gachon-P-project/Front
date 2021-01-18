package com.gachon.moga;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gachon.moga.R;

import static com.gachon.moga.DataIOKt.appConstantPreferences;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Component.shared_notification_data = getSharedPreferences("notification_data", Context.MODE_PRIVATE);
        Component.sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);

        Component.default_url = getString(R.string.defaultUrl);


        Intent intent;
        if(Component.sharedPreferences.getBoolean("login", false)) {
            intent = new Intent(this, MainActivity.class);
        }  else {
            intent = new Intent(this, AgreementTermsActivity.class);
        }
        startActivity(intent);
        finish();


    }
}
