package com.hfad.gamo;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.Volley;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationFragment extends Fragment {

    private Context context;
    private VolleyForHttpMethod volley;
    private Notification_RecyclerAdapter adapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private JSONArray dataArray;
    private int unread = 0;

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

        swipeRefreshLayout = view.findViewById(R.id.swipeLayoutNotification);
        recyclerView = view.findViewById(R.id.rViewNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        dataArray = new JSONArray();

//        임시데이터
        String content = "";
        for (int i = 0 ; i < 10 ; i++) {
            String title = "제목 "+i;
            content += " 내용 내용 내용 " + i;
            String type = "";
            switch ((i / 3)) {
                case 1 :
                    type = "notice_new";
                    break;
                case 2:
                    type = "board_reply";
                    break;
                case 0:
                    type = "board_like";
                    break;
            }
            String board_no = String.valueOf(1919+3*i);
            String time = "2020-12-19 15:35:57";

            try {
                JSONObject object = new JSONObject("{\"title\" : \"" + title + "\", \"content\" : \"" + content + "\", \"type\" : \"" + type + "\", " +
                        "\"baord_no\" : \"" + board_no + "\", \"time\" : \"" + time + "\" }");
                dataArray.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        adapter = new Notification_RecyclerAdapter(dataArray);
        adapter.setRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();



        swipeRefreshLayout.setColorSchemeResources(R.color.indigo500);
//        swipe refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ((MainActivity)getActivity()).refreshFragment();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });

        return view;
    }
}