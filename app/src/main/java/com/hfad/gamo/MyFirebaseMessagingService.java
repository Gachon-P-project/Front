package com.hfad.gamo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        final SharedPreferences pref = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);

        String token = s;

        // 토큰이 없었거나, 기존 토큰과 다르다면 SharedPreferences 에 저장.
        if (!(pref.getString("token", null).equals(token))) {
            pref.edit().putString("token", token).apply();
        }

    }

    public void showDataMessage(String msgTitle, String msgContent) {
        Log.i("### data msgTitle : ", msgTitle);
        Log.i("### data msgContent : ", msgContent);
        String toastText = String.format("[Data 메시지] title: %s => content: %s", msgTitle, msgContent);
        Looper.prepare();
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * 수신받은 메시지를 Toast로 보여줌
     * @param msgTitle
     * @param msgContent
     */
    public void showNotificationMessage(String msgTitle, String msgContent) {
        Log.i("### noti msgTitle : ", msgTitle);
        Log.i("### noti msgContent : ", msgContent);
        String toastText = String.format("%s : %s", msgTitle, msgContent);
        Looper.prepare();
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * 메시지 수신받는 메소드
     * @param msg
     */
    @Override
    public void onMessageReceived(RemoteMessage msg) {
        Log.i("### msg : ", msg.toString());
        if (msg.getData().isEmpty()) {
            showNotificationMessage(msg.getNotification().getTitle(), msg.getNotification().getBody());  // Notification으로 받을 때
        } else {
            showDataMessage(msg.getData().get("title"), msg.getData().get("content"));  // Data로 받을 때
        }
    }
}