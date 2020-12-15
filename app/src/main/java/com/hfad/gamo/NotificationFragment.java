package com.hfad.gamo;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.gamo.ClickedBoard.ClickedBoard_RecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class NotificationFragment extends Fragment {

    private SharedPreferences prefs;
    private String dept;
    private Notification_RecyclerAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private JSONObject jsonObject1 = new JSONObject();
    private JSONObject jsonObject2 = new JSONObject();
    RecyclerView recyclerView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        dept = prefs.getString("department", null);

        try {
            jsonObject1.put("num", 0);
            jsonObject1.put("board_no", "1953");
            jsonObject1.put("title", "title1");
            jsonObject1.put("file", 1);
            jsonObject1.put("date", "2020-10-20");
            jsonObject1.put("view", "46");

            responseJSONArray.put(jsonObject1);

            jsonObject2.put("num", 0);
            jsonObject2.put("board_no", "1998");
            jsonObject2.put("title", "title2");
            jsonObject2.put("file", 0);
            jsonObject2.put("date", "2020-09-20");
            jsonObject2.put("view", "16");

            responseJSONArray.put(jsonObject2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.recycler_notification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new Notification_RecyclerAdapter(responseJSONArray, dept);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.toolbar_clicked_board);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(dept);
    }
}