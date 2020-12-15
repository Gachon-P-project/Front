package com.hfad.gamo;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
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
    private JSONObject responseJSONObject = new JSONObject();
    private ImageView cancel_button_notification;
    private EditText editText;

    private RecyclerView recyclerView = null;
    private SwipeRefreshLayout swipeContainer;
    private VolleyForHttpMethod volley;
    private String url;

    private int one = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        dept = prefs.getString("department", null);

        //
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(this.getContext()));
        url = "http://112.148.161.36:17394/notice/read/0/컴퓨터공학과";

        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        responseJSONObject = response.getJSONObject(i);
                        responseJSONArray.put(responseJSONObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        cancel_button_notification = view.findViewById(R.id.cancel_button_notification);
        editText = view.findViewById(R.id.edit);

        recyclerView = view.findViewById(R.id.recycler_notification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new Notification_RecyclerAdapter(responseJSONArray, dept);
        recyclerView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_notification);

        // Later!!
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*int original_length = responseJSONArray.length();
                int current_length = original_length;
                for(int i = 0; i < original_length; i++) {
                    responseJSONArray.remove(--current_length);
                }

                volley.getJSONArray(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                responseJSONObject = response.getJSONObject(i);
                                responseJSONArray.put(responseJSONObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });*/

                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.indigo500);

        ImageView search_button = view.findViewById(R.id.search_button);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://112.148.161.36:17394/notice/read/0/컴퓨터공학과/" + editText.getText().toString();
                volley.getJSONArray(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int original_length = responseJSONArray.length();
                        int current_length = original_length;
                        for(int i = 0; i < original_length; i++) {
                            responseJSONArray.remove(--current_length);
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                responseJSONObject = response.getJSONObject(i);
                                responseJSONArray.put(responseJSONObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();

                        cancel_button_notification.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        cancel_button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int original_length = responseJSONArray.length();
                int current_length = original_length;
                for(int i = 0; i < original_length; i++) {
                    responseJSONArray.remove(--current_length);
                }

                url = "http://112.148.161.36:17394/notice/read/0/컴퓨터공학과";

                volley.getJSONArray(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int original_length = responseJSONArray.length();
                        int current_length = original_length;
                        for(int i = 0; i < original_length; i++) {
                            responseJSONArray.remove(--current_length);
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                responseJSONObject = response.getJSONObject(i);
                                responseJSONArray.put(responseJSONObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();

                        cancel_button_notification.setVisibility(View.GONE);
                        editText.getText().clear();
                    }
                });

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.toolbar_clicked_board);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<b>" + dept + "</b>", 0));
    }
}