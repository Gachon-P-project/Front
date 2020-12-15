package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClickedBoardActivity extends AppCompatActivity {

    private JSONObject responseJSONObject = new JSONObject();
    private VolleyForHttpMethod volley;
    private ClickedBoard_RecyclerAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<String> a = new ArrayList<>();
    private String url;
    private String board_title;
    private String professor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_board);

        Intent intent = getIntent();
        board_title = intent.getExtras().getString("title");
        professor = intent.getExtras().getString("professor");

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_clicked_board);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                    }
                });
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.indigo500);

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_clicked_board);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(board_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);


        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        RecyclerView recyclerView = findViewById(R.id.recycler_clicked_board);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        url = "http://172.30.1.2:17394/select/피프로젝트/황희정";

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
                intent.putExtra("subject", "모바일 웹");
                intent.putExtra("board_title", board_title);
                startActivity(intent);
                return true;
            case R.id.action_add :
                /*
                major_name: req.body.major_name,
                subject_name: req.body.subject_name,
                professor_name: req.body.professor_name,
                user_id: req.body.user_id
                 */
                intent = new Intent(getBaseContext(), WritingActivity.class);
                intent.putExtra("major", "컴퓨터공학과");
                intent.putExtra("subject", "컴퓨터구조");
                intent.putExtra("professor", "이상순");
                intent.putExtra("user", "jy11290");
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
}