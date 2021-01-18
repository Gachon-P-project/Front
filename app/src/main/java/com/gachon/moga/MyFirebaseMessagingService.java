package com.gachon.moga;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import static com.gachon.moga.Component.default_url;
import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.Component.shared_notification_data;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.gachon.moga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.gachon.moga.DataIOKt.appConstantPreferences;

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
                String tokenUrl = default_url.concat(getString(R.string.postToken));
                pref_token.edit().putString("token", token).apply();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("token", token);
                    jsonObject.put("user_no", sharedPreferences.getString("number", null));
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

    public void showDataMessage(String msgTitle, String msgContent, String msgBoardNo) {
        Log.i("### data msgTitle : ", msgTitle);
        Log.i("### data msgContent : ", msgContent);
        Log.i("### data msgBoardNo : ", msgBoardNo);
//        String toastText = String.format("[Data 메시지] title: %s => content: %s", msgTitle, msgContent);
        String toastText = String.format("%s : %s", msgTitle, msgContent);
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
        String title, body, board_no;
        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);
        shared_notification_data = getSharedPreferences("notification_data", Context.MODE_PRIVATE);


        if (msg.getData().isEmpty()) {
            title = msg.getNotification().getTitle();
            body = msg.getNotification().getBody();
            board_no = "";
            showNotificationMessage(title, body);  // Notification으로 받을 때
        } else {
            title = msg.getData().get("title");
            body = msg.getData().get("body");
            board_no = msg.getData().get("num");
            Log.d(TAG, "onMessageReceived: title : " + title + ", body : " + body + ", board_no : " + board_no);
            showDataMessage(title, body, board_no);  // Data로 받을 때
        }
        sendNotification(title, body, board_no);

    }

    public void saveMessage(String title, String content, String board_no) {
        Log.d(TAG, "saveMessage: called!");
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String msg = String.format("{\"title\" : \"%s\", \"content\" : \"%s\",  \"board_no\" : \"%s\", \"time\" : \"%s\", \"isRead\" : false}", title, content, board_no, df.format(date));
        try {
            JSONObject obj = new JSONObject(msg);
            String oldData = DataIOKt.getNotifications();
            JSONArray newData;
            if(oldData.equals(""))
                newData = new JSONArray();
            else
                newData = new JSONArray(oldData);
            newData.put(obj);
            int unread = DataIOKt.getUnread() + 1;

//            데이터가 40개가 넘으면 첫번째 데이터 지움.
            if (newData.length() >= 40) {
                if(!newData.getJSONObject(0).getBoolean("isRead"))
                    unread--;
                newData.remove(0);
                Log.i(TAG, "saveMessage: first message deleted");
            }
            DataIOKt.setNotifications(newData.toString());
            Log.i(TAG, "saveMessage: message saved!");

//            읽지 않은 알림 숫자
//            for (int i = 0 ; i < newData.length() ; i++) {
//                if(newData.getJSONObject(i).getBoolean("isRead") == false)
//                    unread++;
//            }
            DataIOKt.setUnread(unread);

            Intent intent = new Intent();
            intent.putExtra("unread", unread);
            intent.setAction(getString(R.string.broadcastSaveMessage));
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String title, String message, String board_no) {
        Intent intent;
        PendingIntent pendingIntent;
        int unread = DataIOKt.getUnread();
        Log.d(TAG, "sendNotification: unread : " + unread);
        boolean isNotify = DataIOKt.getNotificationSetting();

        intent = new Intent(this, SplashActivity.class);
        intent.putExtra("name", "name");

//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_ONE_SHOT);
        pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder summaryNotification;

        if(Build.VERSION.SDK_INT >= 26) {
            String channelId = "Moga Push";
            String channelName = "Push Message";
            String channelDescription = "Moga Push Message";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);

            channel.enableLights(true);
            channel.setLightColor(R.color.colorPrimary);
            channel.setVibrationPattern(new long[]{0, 300, 300, 300});
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);

            notificationBuilder = new NotificationCompat.Builder(this, channelId);
//            summaryNotification = new NotificationCompat.Builder(this, channelId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
//            summaryNotification = new NotificationCompat.Builder(this);
        }

//        String group_id = "push_group";
//        String group_name = "푸시 그룹";
        String group_name = "com.hfad.gamo.Push_Group";
        notificationBuilder.setSmallIcon(R.mipmap.ic_main)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setContentText(message);

//        summaryNotification.set

        saveMessage(title, message, board_no);

        if(isNotify) {
            notificationManager.notify((int) (System.currentTimeMillis() / 1000), notificationBuilder.build());
        }


    }


}