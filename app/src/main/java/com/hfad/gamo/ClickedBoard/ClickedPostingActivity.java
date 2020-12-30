package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/*import androidx.recyclerview.widget.DividerItemDecoration;*/
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.Component;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class ClickedPostingActivity extends AppCompatActivity {

    private LinearLayout like_btn;  // 공감 버튼
    private JSONObject responseJSONObject = new JSONObject();
    private VolleyForHttpMethod volley;
    private CommentAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private JSONObject commentJSONObject = new JSONObject();
    private EditText comment;
    private SharedPreferences prefs;

    private String url;
    private String userId;
    private String post_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_posting);

        Intent intent = getIntent();
        final toClickedPosting toClickedPosting = intent.getParcelableExtra("toClickedPosting");

        prefs = this.getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        userId = prefs.getString("id", null);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_clicked_board);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(toClickedPosting.getBoard_title());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);

        TextView title = findViewById(R.id.title_text);
        TextView date = findViewById(R.id.date_text);
        TextView contents = findViewById(R.id.contents_text);
        TextView reply_cnt = findViewById(R.id.reply_text);
        TextView post_like = findViewById(R.id.clicked_posting_post_like);
        comment = findViewById(R.id.comment);
        ImageView post_comment = findViewById(R.id.post_comment);

        title.setText(toClickedPosting.getPost_title());
        date.setText(toClickedPosting.getWrt_date());
        contents.setText(toClickedPosting.getPost_contents());
        reply_cnt.setText(toClickedPosting.getReply_cnt());
        post_like.setText(toClickedPosting.getPost_like());

        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String url = "http://172.30.1.2:17394/reply/insert/" + "jy11290" + "/" + toClickedPosting.getPost_no();

                String user = "jy11290";
                final String post_no = toClickedPosting.getPost_no();

                String url = Component.default_url.concat(getString(R.string.postReply,user,post_no));

                try {
                    commentJSONObject.put("reply_contents", comment.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                volley.postJSONObjectString(commentJSONObject, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "댓글이 작성되었습니다.", Toast.LENGTH_SHORT).show();

                        int original_length = responseJSONArray.length();
                        int current_length = original_length;
                        for(int i = 0; i < original_length; i++) {
                            responseJSONArray.remove(--current_length);
                        }

                        String url = Component.default_url.concat(getString(R.string.inquireReplies,"8"));

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
                }, null);
            }
        });


        like_btn = findViewById(R.id.post_like_btn);    // 공감 버튼

        RecyclerView recyclerView = findViewById(R.id.recycler_reply);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                R.drawable.line_divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        String url = Component.default_url.concat(getString(R.string.inquireReplies,"8"));

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

        adapter = new CommentAdapter(responseJSONArray);
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

