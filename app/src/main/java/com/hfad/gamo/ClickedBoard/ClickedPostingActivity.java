package com.hfad.gamo.ClickedBoard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/*import androidx.recyclerview.widget.DividerItemDecoration;*/
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.Component;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class ClickedPostingActivity extends AppCompatActivity implements View.OnClickListener, ReplyDialogInterface, ClickedPostingDialogInterface {

    public static int WritingNestedReplyActivityCode = 0;
    public static int WritingUpdateActivityCode = 1;

    private VolleyForHttpMethod volley = null;
    private toClickedPosting toClickedPosting = null;
    private ReplyAdapter replyAdapter = null;
    private JSONArray jsonArrayForReplyAdapter = new JSONArray();
    private JSONObject jsonObjectForPostReply = new JSONObject();
    private JSONObject updatedPostingData;
    private EditText postReply_et = null;
    private ImageView postReply_iv = null;
    private ImageView post_like_img = null;
    private TextView post_like_text = null;
    TextView title = null;
    TextView nickName = null;
    TextView date = null;
    TextView contents = null;
    TextView reply_cnt = null;
    private SharedPreferences prefs = null;
    private DisplayMetrics displayMetricsForDeviceSize = null;

    private String urlForInquireReplies;
    private String urlForPostReply;
    private String urlForPostLike;
    private String urlForInquirePostingsOfBoard;
    private String subject_name;
    private String professor_name;
    private String post_no;
    private String writer_number;
    private String user_number;
    private boolean isLiked;
    private int like_cnt;
    private String forUpdatePosting = null;
    private JSONObject dataForUpdatePosting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_posting);

        Intent intent = getIntent();
        toClickedPosting = intent.getParcelableExtra("toClickedPosting");
        forUpdatePosting = intent.getExtras().getString("forUpdatePosting");
        initDataForInquirePostingsOfBoard();

        post_no = toClickedPosting.getPost_no();
        writer_number = toClickedPosting.getUser_no();
        like_cnt = Integer.parseInt(toClickedPosting.getLike_cnt());

        prefs = this.getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        user_number = prefs.getString("number", null);

        initVolley();
        initToolBar();
        initRecyclerViewForReply();
        initView();
        initPostLikeUsingUserValue();
        initUrl();


        postReply_iv.setOnClickListener(this);

        post_like_img.setOnClickListener(this);

        inquireReplies();
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
                displayMetricsForDeviceSize = getApplicationContext().getResources().getDisplayMetrics();
                ClickedPostingDialog clickedPostingDialog = new ClickedPostingDialog(this, toClickedPosting, forUpdatePosting);
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.activity_clicked_posting_post_reply_iv) {
            postReply_et.setFocusable(false);
            putReplyIntoJSONObject();
            postReply();
            postReply_et.setText(null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    postReply_et.setFocusable(true);
                }
            }, 500);
        } else if (v.getId() == R.id.activity_clicked_posting_post_like_iv) {
            post_like_img.setFocusable(false);

            volley.postJSONObjectString(null,urlForPostLike, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(isLiked) {
                        isLiked = false;
                        post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like, null));
                        post_like_text.setText(String.valueOf(--like_cnt));
                    } else {
                        isLiked = true;
                        post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like_filled, null));
                        post_like_text.setText(String.valueOf(++like_cnt));
                    }
                }
            }, null);
            post_like_img.setFocusable(true);
        }
    }

    @Override
    public void onDeleteReplyDialog(int depth, int reply_no) {
        if(depth == 0) {
            deleteReply(reply_no);
        } else {
            deleteNestedReply(reply_no);
        }
    }

    @Override
    public void onDeleteClickedPostingDialog() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == WritingNestedReplyActivityCode) {
            if(resultCode == RESULT_OK) {
                showAllReplies();
            }
        } else if (requestCode == WritingUpdateActivityCode) {
            if(resultCode == WritingUpdateActivity.getUpdateResponseCode()) {
                showUpdatedPosting();
            }
        }
    }

    private void inquireReplies() {
        volley.getJSONArray(urlForInquireReplies, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                processReceivedReplies(response);
            }
        });
    }

    private void processReceivedReplies(JSONArray response) {
        if(response.length() == 0)
            return;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject responseJSONObject = response.getJSONObject(i);
                jsonArrayForReplyAdapter.put(responseJSONObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        replyAdapter.notifyDataSetChanged();
    }

    private void postReply() {
        volley.postJSONObjectString(jsonObjectForPostReply, urlForPostReply, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showAllReplies();
            }
        }, null);
    }

    private void putReplyIntoJSONObject() {
        try {
            jsonObjectForPostReply.put("reply_contents", postReply_et.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showAllReplies() {
        //Toast.makeText(getApplicationContext(), "댓글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
        clearJSONArray();
        inquireReplies();
    }


    private void clearJSONArray() {
        int original_length = jsonArrayForReplyAdapter.length();
        int current_length = original_length;
        for(int i = 0; i < original_length; i++) {
            jsonArrayForReplyAdapter.remove(--current_length);
        }
    }

    private void initRecyclerViewForReply() {
        RecyclerView recyclerViewForReply = findViewById(R.id.recycler_reply);
        recyclerViewForReply.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                R.drawable.line_divider);
        recyclerViewForReply.addItemDecoration(dividerItemDecoration);

        replyAdapter = new ReplyAdapter(jsonArrayForReplyAdapter, this, "ClickedPostingActivity");
        recyclerViewForReply.setAdapter(replyAdapter);
        Log.i("recycler!!!" , "initRecyclerViewForReply");
    }

    private void initToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.activity_clicked_posting_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(toClickedPosting.getBoard_title());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);
    }

    private void initView() {
        title = findViewById(R.id.activity_clicked_posting_title);
        nickName = findViewById(R.id.activity_clicked_posting_nickname);
        date = findViewById(R.id.activity_clicked_posting_wrt_date);
        contents = findViewById(R.id.activity_clicked_posting_contents);
        reply_cnt = findViewById(R.id.activity_clicked_posting_reply_cnt);
        post_like_text = findViewById(R.id.activity_clicked_posting_post_like_text);

        post_like_img = findViewById(R.id.activity_clicked_posting_post_like_iv);
        postReply_et = findViewById(R.id.activity_clicked_posting_post_reply_et);
        postReply_iv = findViewById(R.id.activity_clicked_posting_post_reply_iv);

        title.setText(toClickedPosting.getPost_title());
        nickName.setText("익명");
        date.setText(toClickedPosting.getWrt_date());
        contents.setText(toClickedPosting.getPost_contents());
        reply_cnt.setText(toClickedPosting.getReply_cnt());
        post_like_text.setText(toClickedPosting.getLike_cnt());
    }

    private void initPostLikeUsingUserValue() {
        if(toClickedPosting.getLike_user().equals("0")) {
            isLiked = false;
            post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like, null));
        } else {
            isLiked = true;
            post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like_filled, null));
        }
    }

    private void initVolley() {
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));
    }

    private void initUrl() {
        urlForPostReply = Component.default_url.concat(getString(R.string.postReply,user_number,post_no));
        urlForInquireReplies = Component.default_url.concat(getString(R.string.inquireReplies,post_no));
        urlForPostLike = Component.default_url.concat(getString(R.string.postLike,post_no,user_number));
        urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfBoard, subject_name, professor_name, user_number));
    }

    private void initDataForInquirePostingsOfBoard() {
        try {
            dataForUpdatePosting = new JSONObject(forUpdatePosting);
            subject_name = dataForUpdatePosting.getString("subject_name");
            professor_name = dataForUpdatePosting.getString("professor_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void deleteReply(int reply_no) {
        final String urlDeleteReply = Component.default_url.concat(getString(R.string.deleteReply, String.valueOf(reply_no)));
        volley.delete(null, urlDeleteReply, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showAllReplies();
            }
        }, null);
    }

    private void deleteNestedReply(int reply_no) {
        final String urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReply, String.valueOf(reply_no)));
        volley.delete(null, urlDeleteNestedReply, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showAllReplies();
            }
        }, null);
    }

    private void showUpdatedPosting() {
        inquirePostingsOfBoard();
    }

    private JSONObject findUpdatedPosting(JSONArray ReceivedJsonArray) {
        JSONObject jsonObject;
        int comparisonPostNo;
        for(int i = 0 ; i < ReceivedJsonArray.length(); i++) {
            try {
                jsonObject = ReceivedJsonArray.getJSONObject(i);
                comparisonPostNo = jsonObject.getInt("post_no");
                if (comparisonPostNo == Integer.parseInt(post_no)) {
                    return jsonObject;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void inquirePostingsOfBoard() {
        volley.getJSONArray(urlForInquirePostingsOfBoard, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                onResponseInquirePostingsOfBoard(response);
            }
        });
    }

    private void updateToClickedPosting(String title, String board) {
        toClickedPosting.setPost_title(title);
        toClickedPosting.setPost_contents(board);
    }

    private void onResponseInquirePostingsOfBoard(JSONArray response) {
        dataForUpdatePosting = findUpdatedPosting(response);
        String titleText = null;
        String contentsText = null;
        String replyCntText = null;
        String postLikeText = null;


        try {
            titleText = dataForUpdatePosting.getString("post_title");
            contentsText = dataForUpdatePosting.getString("post_contents");
            replyCntText = dataForUpdatePosting.getString("reply_cnt");
            postLikeText = dataForUpdatePosting.getString("like_cnt");
        } catch(JSONException e) {
            e.printStackTrace();
        }

        title.setText(titleText);
        nickName.setText("익명");
        contents.setText(contentsText);
        reply_cnt.setText(replyCntText);
        post_like_text.setText(postLikeText);

        updateToClickedPosting(titleText, contentsText);
    }

    public String getWriter_number() {
        return writer_number;
    }


}

