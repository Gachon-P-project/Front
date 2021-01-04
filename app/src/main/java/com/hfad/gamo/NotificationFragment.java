package com.hfad.gamo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private Context context;
    private VolleyForHttpMethod volley;
    private Notification_RecyclerAdapter adapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private JSONArray dataArray;
    private TextView tvNoData;
    private ImageView imgNoData;
    private ImageButton imgBtnClearNotifications;
    private int unread = DataIOKt.getUnread();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        tvNoData = view.findViewById(R.id.tvNotificationNoData);
        imgNoData = view.findViewById(R.id.imgNotificationNoData);
        imgBtnClearNotifications = view.findViewById(R.id.imgBtnClearNotifications);
        swipeRefreshLayout = view.findViewById(R.id.swipeLayoutNotification);
        recyclerView = view.findViewById(R.id.rViewNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        getNewData();

        adapter = new Notification_RecyclerAdapter(dataArray, this);
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
        imgBtnClearNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataIOKt.setNotifications("");
                DataIOKt.setUnread(0);
                refresh();
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.broadcastSaveMessage));
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
            Log.d(TAG, "onReceive: unread : " + unread + ", newUnread : " + newUnread);
            if(unread == 0 || unread != newUnread) {
                refresh();
            }
        }
    }

    private void refresh() {

        getNewData();

        recyclerView.removeAllViews();
        adapter = new Notification_RecyclerAdapter(dataArray, NotificationFragment.this);
        adapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(dataArray.toString().equals("[]")) {
            recyclerView.setVisibility(View.GONE);
            imgNoData.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            imgNoData.setVisibility(View.GONE);
            tvNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getNewData(){

        String s = DataIOKt.getNotifications();

        if(s.equals("")) {
            recyclerView.setVisibility(View.GONE);
            imgNoData.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.VISIBLE);
            dataArray = new JSONArray();
        } else {
            imgNoData.setVisibility(View.GONE);
            tvNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            try {
                dataArray = new JSONArray(DataIOKt.getNotifications());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}