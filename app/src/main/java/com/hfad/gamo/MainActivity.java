package com.hfad.gamo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm = getSupportFragmentManager();
    private com.hfad.gamo.timeTable.TimeTableFragment f_TimeTable2 = new com.hfad.gamo.timeTable.TimeTableFragment();
    private NotificationFragment f_Notification = new NotificationFragment();
    private BoardFragment f_Board = new BoardFragment();
    private MyPageFragment f_MyPage = new MyPageFragment();

    private static final String TAG = "TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                    }
                });*/




        setContentView(R.layout.activity_main);

        Component.sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment, f_TimeTable2);
        fragmentTransaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragment(item);
                return true;
            }
        });
    }


    private void switchFragment(MenuItem item) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        switch ((item.getItemId())) {
            case R.id.bottomNavigationTimeTable: {
                fragmentTransaction.replace(R.id.fragment, f_TimeTable2).commitAllowingStateLoss();
                break;
            }

            case R.id.bottomNavigationNotification: {
                fragmentTransaction.replace(R.id.fragment, f_Notification).commitAllowingStateLoss();
                break;
            }

            case R.id.bottomNavigationBoard: {
                fragmentTransaction.replace(R.id.fragment, f_Board).commitAllowingStateLoss();
                break;
            }

            case R.id.bottomNavigationMyPage: {
                fragmentTransaction.replace(R.id.fragment, f_MyPage).commitAllowingStateLoss();
                break;
            }
        }
    }
}