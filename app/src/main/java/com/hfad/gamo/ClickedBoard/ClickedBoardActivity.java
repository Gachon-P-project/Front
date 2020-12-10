package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ClickedBoardActivity extends AppCompatActivity {

    private static JSONObject requestJSONObject = new JSONObject();
    private static VolleyForHttpMethod volley;
    private static JSONArray requestJSONArray = new JSONArray();
    private static ClickedBoard_RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_board);

        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");


        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_clicked_board);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        RecyclerView recyclerView = findViewById(R.id.recycler_clicked_board);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String url = "http://172.30.1.2:17394/select/피프로젝트/황희정";

        volley.getJSONArray(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        requestJSONObject = response.getJSONObject(i);
                        requestJSONArray.put(requestJSONObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new ClickedBoard_RecyclerAdapter(requestJSONArray);
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
                return true;
            case R.id.action_add :
                intent = new Intent(getBaseContext(), WritingActivity.class);
                intent.putExtra("subject", "모바일 웹");
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