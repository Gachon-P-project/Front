package com.hfad.gamo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//import static com.hfad.gamo.DataIOKt.getNotificationIndex;
//import static com.hfad.gamo.DataIOKt.getNotificationSetting;
//import static com.hfad.gamo.DataIOKt.getNotifications;
//import static com.hfad.gamo.DataIOKt.getUnread;
//import static com.hfad.gamo.DataIOKt.setNotificationSetting;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private Context context;
    private VolleyForHttpMethod volley;
    private Notification_RecyclerAdapter adapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private JSONArray dataArray;
//    private Map<String, ?> dataMap;
    private TextView tvNoData;
    private int unread = 0;
//    private int last_index = DataIOKt.getNotificationIndex();
    private boolean isNotificationEnabled;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(this.getContext()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        tvNoData = view.findViewById(R.id.tvNotificationNoData);
        swipeRefreshLayout = view.findViewById(R.id.swipeLayoutNotification);
        recyclerView = view.findViewById(R.id.rViewNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//        dataMap = getNotifications();

        try {
            dataArray = new JSONArray(DataIOKt.getNotifications());
        } catch (JSONException e){
            e.printStackTrace();
        }

        adapter = new Notification_RecyclerAdapter(dataArray, this);
//        adapter = new Notification_RecyclerAdapter(dataMap, this, last_index);
        adapter.setRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();



        swipeRefreshLayout.setColorSchemeResources(R.color.indigo500);
//        swipe refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refresh();

                swipeRefreshLayout.setRefreshing(false);
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.hfad.gamo.saveMessage");
        NotificationFragment.MyBroadcastReceiver receiver = new NotificationFragment.MyBroadcastReceiver();
        context.registerReceiver(receiver, intentFilter);

    }

    public void setBadge(int newUnread) {
        unread = newUnread;
        DataIOKt.setUnread(newUnread);
        DataIOKt.setNotifications(dataArray.toString());
        ((MainActivity)getActivity()).setBadge(newUnread);
    }


    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            int newUnread = extra.getInt("unread");
//            setBadge(newUnread);
            Log.d(TAG, "onReceive: unread : " + unread + ", newUnread : " + newUnread);
            if(unread == 0 || unread != newUnread) {
//                try {
//                    dataArray = new JSONArray(getNotifications());
//                    adapter.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                last_index = getNotificationIndex();
//                dataMap = getNotifications();
//                adapter.notifyDataSetChanged();

                refresh();
            }
        }
    }

    private void refresh() {

        try {
            dataArray = new JSONArray(DataIOKt.getNotifications());
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        last_index = getNotificationIndex();
//        dataMap = getNotifications();
        recyclerView.removeAllViews();
        adapter = new Notification_RecyclerAdapter(dataArray, NotificationFragment.this);
//        adapter = new Notification_RecyclerAdapter(dataMap, this, last_index);
        adapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


}