package com.hfad.gamo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm = getSupportFragmentManager();
    private com.hfad.gamo.timeTable.TimeTableFragment f_TimeTable2 = new com.hfad.gamo.timeTable.TimeTableFragment();
    private NotificationFragment f_Notification = new NotificationFragment();
    private BoardFragment f_Board = new BoardFragment();
    private MyPageFragment f_MyPage = new MyPageFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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