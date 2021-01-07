package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.Component;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClickedBoardActivity extends AppCompatActivity {

    private static final String TAG = "ClickedBoardActivity";
    private JSONObject responseJSONObject = new JSONObject();
    private VolleyForHttpMethod volley;
    private ClickedBoard_RecyclerAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<String> a = new ArrayList<>();
    private String url;
    private String board_title;
    private String professor, user_no, department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_board);

        SharedPreferences sharedPreferences = Component.sharedPreferences;

        Intent intent = getIntent();
        board_title = intent.getExtras().getString("title");
        professor = intent.getExtras().getString("professor");
        user_no = sharedPreferences.getString("number", "");
        department = sharedPreferences.getString("department", "");
        Log.d(TAG, "onCreate: title : " + board_title + ", professor : " + professor);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_clicked_board);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setEnabled(false);
                int original_length = responseJSONArray.length();
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
                        swipeContainer.setRefreshing(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeContainer.setEnabled(true);
                            }
                        }, 1000);
                    }
                });
            }
        });

        swipeContainer.setColorSchemeResources(R.color.indigo500);

        Toolbar tb = (Toolbar) findViewById(R.id.activity_clicked_board_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(board_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);


        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        RecyclerView recyclerView = findViewById(R.id.activity_clicked_board_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*String subject = "컴퓨터구조";
        String professor = "이상순";*/
        url = Component.default_url.concat(getString(R.string.inquirePostingsOfBoard,board_title, professor, user_no));

        final long startTime = System.currentTimeMillis();
        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                long endTime = System.currentTimeMillis();
                Log.i("boardTime", String.valueOf(endTime - startTime));
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

        adapter = new ClickedBoard_RecyclerAdapter(responseJSONArray, board_title);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_clicked_board, menu) ;
        return true ;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            case R.id.action_search :
                intent = new Intent(getBaseContext(), SearchActivity.class);
                intent.putExtra("professor", professor);
                intent.putExtra("board_title", board_title);
                intent.putExtra("user_no", user_no);
                startActivity(intent);
                return true;
            case R.id.action_add :
                intent = new Intent(getBaseContext(), WritingActivity.class);
                intent.putExtra("major", department);
                intent.putExtra("subject", board_title);
                intent.putExtra("professor", professor);
                intent.putExtra("user_no", user_no);
                startActivity(intent);
                return true;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}