package com.gachon.moga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.gachon.moga.Component.default_url;
import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.Component.shared_notification_data;
import org.json.JSONException;
import org.json.JSONObject;

import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.getUnread;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    // TEST GIT
    private FragmentManager fm = getSupportFragmentManager();
    private com.gachon.moga.moga.TimeTableFragment f_TimeTable2 = new com.gachon.moga.moga.TimeTableFragment();
    private NoticeFragment f_Notice = new NoticeFragment();
    private BoardListFragment f_Board = new BoardListFragment();
    private MyPageFragment f_MyPage = new MyPageFragment();
    private NotificationFragment f_Notification = new NotificationFragment();
    private BottomNavigationView bottomNavigationView;
    private VolleyForHttpMethod volley;
    private SharedPreferences pref_token;
    private BadgeDrawable notificationBadge = null;
    LoadingDialog loadingDialog;

    private long firstBackPressTime = 0, secondBackPressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        default_url = getString(R.string.defaultUrl);
        loadingDialog = new LoadingDialog();

        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);
        shared_notification_data = getSharedPreferences("notification_data", Context.MODE_PRIVATE);

        pref_token = getSharedPreferences("token", Context.MODE_PRIVATE);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        Log.i("token!!!", "before : ".concat(pref_token.getString("token", "null")));
        //pref_token.edit().clear().commit();
        /*Log.i("token!!", "after".concat(pref_token.getString("token", "null")));*/
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
                            Log.i("token!!", "if inner");
                            String tokenUrl = default_url.concat(getString(R.string.postToken));
                            pref_token.edit().putString("token", token).apply();

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("token",token);
                                jsonObject.put("user_no", sharedPreferences.getString("number", null));
                                jsonObject.put("user_major", sharedPreferences.getString("department", null));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i("token!!", jsonObject.toString());
                            volley.postJSONObjectString(jsonObject, tokenUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("token!!!", "response");
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("token!!!", "error".concat(error.toString()));
                                }
                            });
                        }

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.i("token!!!", msg);
                    }
                });

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        Log.i("token!!!", " : before replaceFragment");
        replaceFragment("timetable");

//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        Intent getIntent = getIntent();
//        flag = getIntent.getBooleanExtra("flag", false);
//        if(flag) {
//            String itemName = getIntent.getStringExtra("itemName");
//            replaceFragment(itemName);
//            flag = false;
//        } else {
//            fragmentTransaction.replace(R.id.fragment, f_TimeTable2);
//            fragmentTransaction.commit();
//        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragment(item.getItemId());
                return true;
            }
        });
        setBadge(getUnread());
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.broadcastSaveMessage));
        MyBroadcastReceiver receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        secondBackPressTime = System.currentTimeMillis();
        Toast.makeText(this, "이전 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        if(secondBackPressTime - firstBackPressTime < 2000) {
            super.onBackPressed();
            finishAffinity();
        }
        firstBackPressTime = System.currentTimeMillis();
    }

    private void switchFragment(int itemId) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        switch (itemId) {
            case R.id.bottomNavigationTimeTable: {
                Fragment fragment = fm.findFragmentById(R.id.fragment);
                if(!(fragment instanceof com.gachon.moga.moga.TimeTableFragment)) {
                    loadingDialog.start(this);
                    fragmentTransaction.replace(R.id.fragment, f_TimeTable2).commitAllowingStateLoss();
                }
                break;
            }

            case R.id.bottomNavigationNotice: {
                fragmentTransaction.replace(R.id.fragment, f_Notice).commitAllowingStateLoss();
                break;
            }

            case R.id.bottomNavigationBoard: {
                fragmentTransaction.replace(R.id.fragment, f_Board).commitAllowingStateLoss();
                break;
            }

            case R.id.bottomNavigationNotification :
                fragmentTransaction.replace(R.id.fragment, f_Notification).commitAllowingStateLoss();
                break;

            case R.id.bottomNavigationMyPage: {
                fragmentTransaction.replace(R.id.fragment, f_MyPage).commitAllowingStateLoss();
            }
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment).commitAllowingStateLoss();
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


    public void setBadge(int count) {
        if(count > 0 ) {
            if(notificationBadge == null)
                notificationBadge = bottomNavigationView.getOrCreateBadge(R.id.bottomNavigationNotification);
            notificationBadge.setVisible(true);
            notificationBadge.setNumber(count);
        } else if(notificationBadge != null) {
            notificationBadge.setVisible(false);
            notificationBadge.clearNumber();
        }
    }


    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            int newUnread = extra.getInt("unread");
            setBadge(newUnread);
        }
    }

    public void stopLoadingDialog() {
        this.loadingDialog.finish();
    }

}
