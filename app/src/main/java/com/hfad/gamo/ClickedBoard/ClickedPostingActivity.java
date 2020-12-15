package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;

public class ClickedPostingActivity extends AppCompatActivity {

    private LinearLayout like_btn;  // 공감 버튼
    private JSONObject requestJSONObject = new JSONObject();
    private VolleyForHttpMethod volley;
    private CommentAdapter adapter;
    private JSONArray requestJSONArray = new JSONArray();

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_posting);

        Intent intent = getIntent();
        toClickedPosting toClickedPosting = intent.getParcelableExtra("toClickedPosting");

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_clicked_board);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(toClickedPosting.getBoard_title());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);

        TextView title = (TextView) findViewById(R.id.title_text);
        TextView date = (TextView) findViewById(R.id.date_text);
        TextView contents = (TextView) findViewById(R.id.contents_text);
        TextView reply_cnt = (TextView) findViewById(R.id.reply_text);

        title.setText(toClickedPosting.getPost_title());
        date.setText(toClickedPosting.getWrt_date());
        contents.setText(toClickedPosting.getPost_contents());
        reply_cnt.setText(toClickedPosting.getReply_cnt());

        ///

        like_btn = findViewById(R.id.post_like_btn);    // 공감 버튼

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));
        RecyclerView recyclerView = findViewById(R.id.recycler_reply);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        url = "http://192.168.254.2:17394/reply/read/20";

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

        adapter = new CommentAdapter(requestJSONArray);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
}

