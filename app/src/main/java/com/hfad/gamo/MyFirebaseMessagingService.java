package com.hfad.gamo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.Toast;

import static com.hfad.gamo.Component.default_url;
import static com.hfad.gamo.Component.sharedPreferences;
import static com.hfad.gamo.Component.shared_notification_data;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    private VolleyForHttpMethod volley;
    private SharedPreferences pref_token;


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.i("token!!!", "firebase");

        String token = s;
        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);
        pref_token = getSharedPreferences("token", Context.MODE_PRIVATE);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));


        if(sharedPreferences.getBoolean("login", false)) {
            // 토큰이 없었거나, 기존 토큰과 다르다면 SharedPreferences 에 저장.
            // 서버에 number, token 값 전송
            if (!(pref_token.getString("token", "null").equals(token))) {
                String tokenUrl = default_url + "/token/add";
                pref_token.edit().putString("token", token).apply();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("number", sharedPreferences.getString("number", null));
                    jsonObject.put("token", token);
                    jsonObject.put("user_major", sharedPreferences.getString("department", null));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                volley.postJSONObjectString(jsonObject, tokenUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, null);
            }
        }
    }

    public void showDataMessage(String msgTitle, String msgContent) {
        Log.i("### data msgTitle : ", msgTitle);
        Log.i("### data msgContent : ", msgContent);
        String toastText = String.format("[Data 메시지] title: %s => content: %s", msgTitle, msgContent);
//        Looper.prepare();
//        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
//        Looper.loop();
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
//        Looper.prepare();
//        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
//        Looper.loop();
    }

    /**
     * 메시지 수신받는 메소드
     * @param msg
     */
    @Override
    public void onMessageReceived(RemoteMessage msg) {
        Log.i("### msg : ", msg.toString());
        String title, body;

        shared_notification_data = getSharedPreferences("notification_data", Context.MODE_PRIVATE);


        if (msg.getData().isEmpty()) {
            title = msg.getNotification().getTitle();
            body = msg.getNotification().getBody();
//            saveMessage(title, body);
            showNotificationMessage(title, body);  // Notification으로 받을 때
        } else {
            title = msg.getData().get("title");
            body = msg.getData().get("body");
//            saveMessage(title,body);
            showDataMessage(title, body);  // Data로 받을 때
        }
        sendNotification(title, body, "testName");

    }

    public void saveMessage(String title, String content) {
        Log.d(TAG, "saveMessage: called!");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String msg = String.format("{\"title\" : \"%s\", \"content\" : \"%s\", \"time\" : \"%s\", \"isRead\" : false}", title, content, df.format(date));
        String msg = String.format("{\"title\" : \"%s\", \"content\" : \"%s\", \"time\" : \"%s\", \"isRead\" : false}", title, content, df.format(date));
        try {
            JSONObject notificationData = new JSONObject(msg);
            int unread = DataIOKt.getUnread();
            unread++;

//            데이터가 40개가 넘으면 첫번째 데이터 지움.
//            if (newData.length() >= 40) {
//                if(!newData.getJSONObject(0).getBoolean("isRead"))
//                    unread--;
//                newData.remove(0);
//                Log.i(TAG, "saveMessage: first message deleted");
//            }
//            sharedPreferences.edit().putString("notification_data", newData.toString()).commit();
            DataIOKt.setNotifications(notificationData.toString());
            Log.i(TAG, "saveMessage: message saved!");

//            읽지 않은 알림 숫자
//            for (int i = 0 ; i < newData.length() ; i++) {
//                if(newData.getJSONObject(i).getBoolean("isRead") == false)
//                    unread++;
//            }
            DataIOKt.setUnread(unread);

            Intent intent = new Intent();
            intent.putExtra("unread", unread);
            intent.setAction("com.hfad.gamo.saveMessage");
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String title, String message, String name) {
        Intent intent;
        PendingIntent pendingIntent;

        intent = new Intent(this, SplashActivity.class);
        intent.putExtra("name", name);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if(Build.VERSION.SDK_INT >= 26) {
            String channelId = "Moga Push";
            String channelName = "Moga Push Message";
            String channelDescription = "Moga Push Information";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);

            channel.enableLights(true);
            channel.setLightColor(R.color.colorPrimary);
            channel.setVibrationPattern(new long[]{0, 300, 300, 300});
            notificationManager.createNotificationChannel(channel);

            notificationBuilder = new NotificationCompat.Builder(this, channelId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        notificationBuilder.setSmallIcon(R.mipmap.ic_main)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setContentText(message);

        saveMessage(title, message);

        notificationManager.notify(0, notificationBuilder.build());


    }


}