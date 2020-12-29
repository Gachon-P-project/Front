package com.hfad.gamo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import static com.hfad.gamo.Component.sharedPreferences;

import androidx.annotation.NonNull;

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

        String token = s;
        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE);
        pref_token = getSharedPreferences("token", Context.MODE_PRIVATE);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

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
            saveMessage(msg.getNotification().getTitle(), msg.getNotification().getBody());
            showNotificationMessage(msg.getNotification().getTitle(), msg.getNotification().getBody());  // Notification으로 받을 때
        } else {
            saveMessage(msg.getData().get("title"), msg.getData().get("content"));
            showDataMessage(msg.getData().get("title"), msg.getData().get("content"));  // Data로 받을 때
        }
    }

    public void saveMessage(String title, String content) {
        Log.d(TAG, "saveMessage: called!");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String msg = String.format("{\"title\" : \"%s\", \"content\" : \"%s\", \"time\" : \"%s\", \"isRead\" : false}", title, content, df.format(date));
        try {
            JSONObject obj = new JSONObject(msg);
            String oldData = sharedPreferences.getString("notification_data", null);
            JSONArray newData;
            int unread = 0;
            if(oldData == null)
                newData = new JSONArray();
            else
                newData = new JSONArray(oldData);

            newData.put(obj);
            if (newData.length() >= 40) {
                newData.remove(0);
                Log.i(TAG, "saveMessage: first message deleted");
            }
            sharedPreferences.edit().putString("notification_data", newData.toString()).commit();
            Log.i(TAG, "saveMessage: message saved!");

//            읽지 않은 알림 숫자
            for (int i = 0 ; i < newData.length() ; i++) {
                if(newData.getJSONObject(i).getBoolean("isRead") == false)
                    unread++;
            }
            DataIOKt.setUnread(unread);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}