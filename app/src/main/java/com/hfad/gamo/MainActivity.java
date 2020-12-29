package com.hfad.gamo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.hfad.gamo.Component.default_url;
import static com.hfad.gamo.Component.sharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class MainActivity extends AppCompatActivity {
    // TEST GIT
    private FragmentManager fm = getSupportFragmentManager();
    private com.hfad.gamo.timeTable.TimeTableFragment f_TimeTable2 = new com.hfad.gamo.timeTable.TimeTableFragment();
    private NoticeFragment f_Notice = new NoticeFragment();
    private BoardFragment f_Board = new BoardFragment();
    private MyPageFragment f_MyPage = new MyPageFragment();
    private BottomNavigationView bottomNavigationView;
    private boolean flag;
    private VolleyForHttpMethod volley;
    private SharedPreferences pref_token;

    private long firstBackPressTime = 0, secondBackPressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //default_url = "http://192.168.50.146:17394";
        //default_url = "http://112.148.161.36:17394";
          default_url = "http://172.30.1.2:17394";

        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);

        pref_token = getSharedPreferences("token", Context.MODE_PRIVATE);


        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // 토큰이 없었거나, 기존 토큰과 다르다면 SharedPreferences 에 저장.
                        // 서버에 number, token 값 전송
                        if (!(pref_token.getString("token", "null").equals(token))) {
                            String tokenUrl = "Good";
                            pref_token.edit().putString("token", token).apply();

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("number", sharedPreferences.getString("number", null));
                                jsonObject.put("token",token);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            volley.postJSONObjectString(jsonObject, tokenUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                }
                            }, null);
                        }

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", msg);
                    }
                });

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        Intent getIntent = getIntent();
        flag = getIntent.getBooleanExtra("flag", false);

        if(flag) {
            String itemName = getIntent.getStringExtra("itemName");
            replaceFragment(itemName);
            flag = false;
        } else {
            fragmentTransaction.add(R.id.fragment, f_TimeTable2);
            fragmentTransaction.commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragment(item.getItemId());
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
//        secondBackPressTime = System.currentTimeMillis();
//        Toast.makeText(this, "이전 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
//        if(secondBackPressTime - firstBackPressTime < 2000) {
//            super.onBackPressed();
//            finishAffinity();
//        }
//        firstBackPressTime = System.currentTimeMillis();
    }

    private void switchFragment(int itemId) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Log.d("MainActivity", "switchFragment: flag : " + flag);
        switch (itemId) {
            case R.id.bottomNavigationTimeTable: {
                fragmentTransaction.replace(R.id.fragment, f_TimeTable2).commitAllowingStateLoss();
                break;
            }

            case R.id.bottomNavigationNotice: {
                fragmentTransaction.replace(R.id.fragment, f_Notice).commitAllowingStateLoss();
//                if(!flag) {
//                    if (nowFragment == f_TimeTable2)
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                    else {
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                    }
//                }
                break;
            }

            case R.id.bottomNavigationBoard: {
                fragmentTransaction.replace(R.id.fragment, f_Board).commitAllowingStateLoss();
                break;
            }

            case R.id.bottomNavigationMyPage: {
                fragmentTransaction.replace(R.id.fragment, f_MyPage).commitAllowingStateLoss();
//                if(!flag)
//                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                break;
            }
        }
    }

    public void replaceFragment(Fragment framgent) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, framgent).commitAllowingStateLoss();
    }
    public void replaceFragment(String itemName) {
        switch (itemName) {
            case "timetable" :
                bottomNavigationView.setSelectedItemId(R.id.bottomNavigationTimeTable);
                switchFragment(R.id.bottomNavigationTimeTable);
                break;
            case "notice" :
                bottomNavigationView.setSelectedItemId(R.id.bottomNavigationNotice);
                switchFragment(R.id.bottomNavigationNotice);
                break;
            case "board" :
                bottomNavigationView.setSelectedItemId(R.id.bottomNavigationBoard);
                switchFragment(R.id.bottomNavigationBoard);
                break;
            case "mypage" :
                bottomNavigationView.setSelectedItemId(R.id.bottomNavigationMyPage);
                switchFragment(R.id.bottomNavigationMyPage);
                break;
        }
    }

    public void refreshFragment() {
        FragmentTransaction ft  = fm.beginTransaction();
        Fragment fragment = fm.findFragmentById(R.id.fragment);
        ft.detach(fragment).attach(fragment).commit();
    }

}
