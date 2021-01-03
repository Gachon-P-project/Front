package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/*import androidx.recyclerview.widget.DividerItemDecoration;*/
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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

    private JSONObject responseJSONObject = new JSONObject();
    private VolleyForHttpMethod volley;
    private ReplyAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private JSONObject commentJSONObject = new JSONObject();
    private EditText reply_text;
    private SharedPreferences prefs;
    private DisplayMetrics displayMetricsForDeviceSize = null;

    private String url;
    private String userId;
    private String post_no;
    private String writer_number;
    private String user_number;
    private boolean isLiked;
    private int like_cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_posting);

        Intent intent = getIntent();
        final toClickedPosting toClickedPosting = intent.getParcelableExtra("toClickedPosting");

        prefs = this.getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        userId = prefs.getString("id", null);
        user_number = prefs.getString("number", null);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        Toolbar tb = (Toolbar) findViewById(R.id.activity_clicked_posting_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(toClickedPosting.getBoard_title());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);

        displayMetricsForDeviceSize = getApplicationContext().getResources().getDisplayMetrics();

        TextView title = findViewById(R.id.activity_clicked_posting_title);
        TextView nickName = findViewById(R.id.activity_clicked_posting_nickname);
        TextView date = findViewById(R.id.activity_clicked_posting_wrt_date);
        TextView contents = findViewById(R.id.activity_clicked_posting_contents);
        TextView reply_cnt = findViewById(R.id.activity_clicked_posting_reply_cnt);
        final ImageView post_like_img = findViewById(R.id.activity_clicked_posting_post_like_img);
        final TextView post_like_text = findViewById(R.id.activity_clicked_posting_post_like_text);

        reply_text = findViewById(R.id.activity_clicked_posting_post_reply_text);
        ImageView post_reply = findViewById(R.id.activity_clicked_posting_post_reply);

        title.setText(toClickedPosting.getPost_title());
        nickName.setText("익명");
        date.setText(toClickedPosting.getWrt_date());
        contents.setText(toClickedPosting.getPost_contents());
        reply_cnt.setText(toClickedPosting.getReply_cnt());
        post_like_text.setText(toClickedPosting.getLike_cnt());


        if(toClickedPosting.getLike_user().equals("0")) {
            isLiked = false;
            post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like, null));
        } else {
            isLiked = true;
            post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like_filled, null));
        }

        post_no = toClickedPosting.getPost_no();
        writer_number = toClickedPosting.getUser_no();
        like_cnt = Integer.parseInt(toClickedPosting.getLike_cnt());


        post_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String url = "http://172.30.1.2:17394/reply/insert/" + "jy11290" + "/" + toClickedPosting.getPost_no();

                String url = Component.default_url.concat(getString(R.string.postReply,user_number,post_no));

                try {
                    commentJSONObject.put("reply_contents", reply_text.getText().toString());
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

                        String url = Component.default_url.concat(getString(R.string.inquireReplies,post_no));

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


        post_like_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_like_img.setFocusable(false);
                String postLikeUrl = Component.default_url.concat(getString(R.string.postLike,post_no,user_number));

                volley.postJSONObjectString(null,postLikeUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(isLiked) {
                            isLiked = false;
                            post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like, null));
                            /*post_like_text.setText(--like_cnt);*/
                        } else {
                            isLiked = true;
                            post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like_filled, null));
                            /*post_like_text.setText(++like_cnt);*/
                        }
                    }
                }, null);
                post_like_img.setFocusable(true);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_reply);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                R.drawable.line_divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        String url = Component.default_url.concat(getString(R.string.inquireReplies,post_no));

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

        adapter = new ReplyAdapter(responseJSONArray, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_clicked_posting, menu);

        if (!writer_number.equals(user_number)) {
            MenuItem item = menu.findItem(R.id.menu_toolbar_clicked_posting_three_dots);
            item.setVisible(false);

            return true;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            case R.id.menu_toolbar_clicked_posting_three_dots :
                ClickedPostingDialog clickedPostingDialog = new ClickedPostingDialog(this);
                clickedPostingDialog.show();
                WindowManager.LayoutParams params = clickedPostingDialog.getWindow().getAttributes();
                params.width = (int) (displayMetricsForDeviceSize.widthPixels * 0.8);
                params.height = (int) (WindowManager.LayoutParams.WRAP_CONTENT * 1.1);
                clickedPostingDialog.getWindow().setAttributes(params);
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

