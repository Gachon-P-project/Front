package com.hfad.gamo;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.ClickedBoard.ClickedBoard_RecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;
import static com.hfad.gamo.Component.default_url;
import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class NotificationFragment extends Fragment {

    private SharedPreferences prefs;
    private String dept;
    private Notification_RecyclerAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private JSONArray tempJSONArray = new JSONArray();
    private JSONObject responseJSONObject = new JSONObject();
    private JSONObject loadingJsonObject;
    private ImageView cancel_button_notification;
    private EditText editText;

    private RecyclerView recyclerView = null;
    private SwipeRefreshLayout swipeContainer;
    private VolleyForHttpMethod volley;
    private String url;


    private int page = 0;


    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        dept = prefs.getString("department", null);


        volley = new VolleyForHttpMethod(Volley.newRequestQueue(this.getContext()));
//        url = "http://172.30.1.2:17394/notice/read/0/컴퓨터공학과";

        getAllNoti();
        try {
            loadingJsonObject = new JSONObject("{\"title\" : \"loading..\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();

        responseJSONArray = new JSONArray();

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        cancel_button_notification = view.findViewById(R.id.cancel_button_notification);
        editText = view.findViewById(R.id.edit);

        recyclerView = view.findViewById(R.id.recycler_notification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new Notification_RecyclerAdapter(responseJSONArray, dept);
        adapter.setRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        loadPost();

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

//        검색
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = default_url + "/notice/read/0/컴퓨터공학과/" + editText.getText().toString();
                volley.getJSONArray(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int original_length = responseJSONArray.length();
                        int current_length = original_length;
                        for (int i = 0; i < original_length; i++) {
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

//        검색 취소
        cancel_button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int original_length = responseJSONArray.length();
                int current_length = original_length;
                for (int i = 0; i < original_length; i++) {
                    responseJSONArray.remove(--current_length);
                }

                url = default_url + "/notice/read/0/컴퓨터공학과";

                volley.getJSONArray(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int original_length = responseJSONArray.length();
                        int current_length = original_length;
                        for (int i = 0; i < original_length; i++) {
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

        loadingDialog.cancel();
    }


//    첫번째에만 사용됨. 데이터 가져오는 함수.
    private void getAllNoti() {
//        url = default_url + "/notice/read/" + page + "/컴퓨터공학과";
        url = default_url + "/notice/read/" + page + "/" + dept;

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
                Log.d("GET NOTICE ::", responseJSONArray.toString());
                adapter.notifyDataSetChanged();
            }
        });
    }

    //    두번째부터 사용됨. 데이터 추가로 가져올때 사용.
    private void getMoreNoti(int page) {
        Log.d("GET_MORE_NOTICE ::", "page : " + page);
//        url = default_url + "/notice/read/" + page + "/컴퓨터공학과";
        url = default_url + "/notice/read/" + page + "/" + dept;

        tempJSONArray = new JSONArray();
        Log.d("GET_MORE_NOTICE", "url : " + url);
        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        tempJSONArray.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                adapter.updateMoreItemArray(tempJSONArray);
                Log.d("GET_MORE_NOTICE ::", tempJSONArray.toString());

                try {
                    for (int i = 0 ; i < tempJSONArray.length() ; i++) {
                        responseJSONArray.put(tempJSONArray.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                adapter.notifyItemRangeChanged(responseJSONArray.length() - 15, 15);
                adapter.notifyDataSetChanged();
                adapter.setIsLoading(false);
                Log.d("FRAGMENT", "responseJSONArray: " + responseJSONArray);
            }
        });
    }


    private void loadPost() {
        Log.d("LOADPOST", "before onLoadMore");
        adapter.setOnLoadMoreListener(new Notification_RecyclerAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d("FRAGMENT ::", "onLoadMore");

                adapter.setIsLoading(true);
                page++;
                if(responseJSONArray.length() <= 1000) {

                    responseJSONArray.put(loadingJsonObject);        // null을 삽입하여 리사이클뷰에서 뷰타입을 로딩타입으로 인식
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(responseJSONArray.length() - 1);
                            Log.d("FRAGMENT ::", "onLoadMore - notifyItemInserted");
                        }
                    });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            responseJSONArray.remove(responseJSONArray.length() - 1);
                            adapter.notifyItemRemoved(responseJSONArray.length());

                            Log.d("ACTIVITY ::", "LOADPOST ::: page : " + page);
                            getMoreNoti(page);

                        }
                    }, 2000);
                } else  {
                    Toast.makeText(getActivity(), "Loading data complete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}