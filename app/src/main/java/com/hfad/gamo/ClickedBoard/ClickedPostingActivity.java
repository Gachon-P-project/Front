package com.hfad.gamo.ClickedBoard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.Component;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class ClickedPostingActivity extends AppCompatActivity implements View.OnClickListener, ReplyDialogInterface, ClickedPostingDialogInterface, SwipeRefreshLayout.OnRefreshListener {

    public static int WritingNestedReplyActivityCode = 0;
    public static int WritingUpdateActivityCode = 1;
    public static boolean called_onStart = false;

    private VolleyForHttpMethod volley = null;
    private toClickedPosting toClickedPosting = null;
    private ReplyAdapter replyAdapter = null;
    private JSONArray jsonArrayForReplyAdapter = new JSONArray();
    private JSONObject jsonObjectForPostReply = new JSONObject();
    private SwipeRefreshLayout activity_clicked_posting_swipe;
    private EditText postReply_et = null;
    private ImageView postReply_iv = null;
    private ImageView post_like_img = null;
    private TextView post_like_text = null;
    private TextView tvToolbarTitle = null;
    private ImageButton imgBtnToolbarBack =  null;
    private MenuItem menu_toolbar_clicked_posting_three_dots = null;
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
    private String urlForDeletePosting;
    private String urlDeleteReply;
    private String urlDeleteNestedReply;
    private String subject_name;
    private String professor_name;
    private String post_no;
    private String writer_number;
    private String user_number;
    private boolean isLiked;
    private String forUpdatePosting = null;
    private JSONObject dataForUpdatePosting = null;
    private JSONObject realTimeDataForUpdatePosting = null;
    private int boardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_posting);

        Component.default_url = getString(R.string.defaultUrl);

        Intent intent = getIntent();
        toClickedPosting = intent.getParcelableExtra("toClickedPosting");
        forUpdatePosting = intent.getExtras().getString("forUpdatePosting");
        boardType = intent.getIntExtra("boardType", -1);
        initDataForInquirePostingsOfBoard();

        post_no = toClickedPosting.getPost_no();
        writer_number = toClickedPosting.getUser_no();

        prefs = this.getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        user_number = prefs.getString("number", null);

        initVolley();
        initToolBar();
        initRecyclerViewForReply();
        initUrl();
        initView();

        activity_clicked_posting_swipe.setColorSchemeResources(R.color.indigo500);
        activity_clicked_posting_swipe.setOnRefreshListener(this);

        postReply_iv.setOnClickListener(this);
        post_like_img.setOnClickListener(this);

        inquireReplies();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        called_onStart = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        called_onStart = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_clicked_posting, menu);
        menu_toolbar_clicked_posting_three_dots = menu.findItem(R.id.menu_toolbar_clicked_posting_three_dots);
        if (!writer_number.equals(user_number)) {
            menu_toolbar_clicked_posting_three_dots.setVisible(false);
        } else {
            menu_toolbar_clicked_posting_three_dots.setEnabled(false);
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
                ClickedPostingDialog clickedPostingDialog = new ClickedPostingDialog(this, toClickedPosting, forUpdatePosting, realTimeDataForUpdatePosting);
                clickedPostingDialog.setBoardType(boardType);
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
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_clicked_posting_post_reply_iv:
                if(isReplyNoneText()) {
                    Toast.makeText(this, "댓글이 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    postReply_iv.setEnabled(false);
                    putReplyIntoJSONObject();
                    postReply();
                    postReply_et.setText(null);
                    postReply_et.clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(postReply_et.getWindowToken(), 0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            postReply_iv.setEnabled(true);
                        }
                    }, 1000);
                }
                break;
            case R.id.activity_clicked_posting_post_like_iv:
                post_like_img.setEnabled(false);
                Log.i("postLike", "postLike");
                volley.postJSONObjectString(null,urlForPostLike, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int currentPostLikeText =  Integer.parseInt(post_like_text.getText().toString());
                        if(isLiked) {
                            isLiked = false;
                            post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like, null));
                            post_like_text.setText(String.valueOf(--currentPostLikeText));
                        } else {
                            isLiked = true;
                            post_like_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_like_filled, null));
                            post_like_text.setText(String.valueOf(++currentPostLikeText));
                        }
                    }
                }, null);
                post_like_img.setEnabled(true);
                break;
            case R.id.imgBtnToolbarBackClickedPosting:
                onBackPressed();
                break;
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
        deletePosting();
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
                if(called_onStart) {
                    setViewText("", "", "", "", "");
                }
                showUpdatedPosting();
                showAllReplies();
            }
        }
    }

    @Override
    public void onRefresh() {
        activity_clicked_posting_swipe.setEnabled(false);
        showUpdatedPosting();
        showAllReplies();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity_clicked_posting_swipe.setRefreshing(false);
            }
        },500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity_clicked_posting_swipe.setEnabled(true);
            }
        },1500);
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
        clearReplyJSONArray();
        inquireReplies();
    }


    private void clearReplyJSONArray() {
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
        tvToolbarTitle = findViewById(R.id.tvToolbarTitleClickedPosting);
        imgBtnToolbarBack = findViewById(R.id.imgBtnToolbarBackClickedPosting);
        tvToolbarTitle.setText(toClickedPosting.getBoard_title());
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle(toClickedPosting.getBoard_title());
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);

        imgBtnToolbarBack.setOnClickListener(this);
    }

    private void initView() {
        setFindViewById();
        showUpdatedPosting();
    }

    private void setFindViewById() {
        title = findViewById(R.id.activity_clicked_posting_title);
        nickName = findViewById(R.id.activity_clicked_posting_nickname);
        date = findViewById(R.id.activity_clicked_posting_wrt_date);
        contents = findViewById(R.id.activity_clicked_posting_contents);
        reply_cnt = findViewById(R.id.activity_clicked_posting_reply_cnt);
        post_like_text = findViewById(R.id.activity_clicked_posting_post_like_text);
        post_like_img = findViewById(R.id.activity_clicked_posting_post_like_iv);
        postReply_et = findViewById(R.id.activity_clicked_posting_post_reply_et);
        postReply_iv = findViewById(R.id.activity_clicked_posting_post_reply_iv);
        activity_clicked_posting_swipe = findViewById(R.id.activity_clicked_posting_swipe);
    }

    private void setViewText(String titleText, String dateText, String contentsText, String replyCntText,
                             String postLikeText) {
        title.setText(titleText);
        nickName.setText("익명");
        date.setText(dateText);
        contents.setText(contentsText);
        reply_cnt.setText(replyCntText);
        post_like_text.setText(postLikeText);
    }


    private void initPostLikeUsingUserValue(int like_user) {
        if(like_user == 0) {
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
        urlForPostLike = Component.default_url.concat(getString(R.string.postLike));
        switch (boardType){
            case 0:         // 수업게시판
                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfSubjectBoard));
                urlForPostReply = Component.default_url.concat(getString(R.string.postReplyOfSubjectBoard));
                urlForInquireReplies = Component.default_url.concat(getString(R.string.inquireRepliesOfSubjectBoard,post_no));
                urlDeleteReply = Component.default_url.concat(getString(R.string.deleteReplyOfSubjectBoard));
                urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReplyOfSubjectBoard));
                break;
            case 1:         // 자유게시판
                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfFreeBoard));
                urlForPostReply = Component.default_url.concat(getString(R.string.postReplyOfFreeBoard));
                urlForInquireReplies = Component.default_url.concat(getString(R.string.inquireRepliesOfFreeBoard,post_no));
                urlDeleteReply = Component.default_url.concat(getString(R.string.deletePostingOfFreeBoard));
                urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReplyOfFreeBoard));
                break;
            case 2:         // 학과게시판
                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfMajorBoard));
                urlForPostReply = Component.default_url.concat(getString(R.string.postReplyOfMajorBoard));
                urlForInquireReplies = Component.default_url.concat(getString(R.string.inquireRepliesOfMajorBoard,post_no));
                urlDeleteReply = Component.default_url.concat(getString(R.string.deleteReplyOfMajorBoard));
                urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReplyOfMajorBoard));
                break;

        }
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

        JSONObject obj = new JSONObject();
        try {
            obj.put("bundle_id", reply_no);                  // 일단 확인 필요
        } catch (JSONException e) {
            e.printStackTrace();
        }

        volley.delete(null, urlDeleteReply, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showAllReplies();
            }
        }, null);
    }

    private void deleteNestedReply(int reply_no) {
        volley.delete(null, urlDeleteNestedReply, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showAllReplies();
            }
        }, null);
    }

    private void deletePosting() {
        JSONObject deleteJsonObj = null;
        switch (boardType) {
            case 0:
                deleteJsonObj = new JSONObject();
                try {
                    deleteJsonObj.put("post_no", post_no);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                break;
            case 2:
                break;
        }
        volley.delete(deleteJsonObj, urlForDeletePosting, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
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
        realTimeDataForUpdatePosting = findUpdatedPosting(response);
        String titleText = null;
        String contentsText = null;
        String replyCntText = null;
        String postLikeText = null;
        String wrt_date = null;
        int like_user = -1;

        if(realTimeDataForUpdatePosting != null) {

            try {
                titleText = realTimeDataForUpdatePosting.getString("post_title");
                wrt_date = realTimeDataForUpdatePosting.getString("wrt_date");
                contentsText = realTimeDataForUpdatePosting.getString("post_contents");
                replyCntText = realTimeDataForUpdatePosting.getString("reply_cnt");
                postLikeText = realTimeDataForUpdatePosting.getString("like_cnt");
                like_user = realTimeDataForUpdatePosting.getInt("like_user");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            wrt_date = processServerDateToAndroidDate(wrt_date);
            setViewText(titleText, wrt_date, contentsText, replyCntText, postLikeText );
            initPostLikeUsingUserValue(like_user);
            menu_toolbar_clicked_posting_three_dots.setEnabled(true);
        } else {
            setViewText("존재하지 않는 게시물 입니다.", "방금","","0", "0" );
            post_like_img.setEnabled(false);
            postReply_et.setEnabled(false);
            postReply_iv.setEnabled(false);
        }




        //updateToClickedPosting(titleText, contentsText);
    }

    private String processServerDateToAndroidDate(String serverDate) {
        long time;
        Date date = null;

        DateFormat dateFormat_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        DateFormat dateFormat_2 = new SimpleDateFormat("yyyy.MM.dd. a hh:mm:ss", Locale.KOREA);
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat_1.setTimeZone(timeZone);
        dateFormat_2.setTimeZone(timeZone);

        Calendar beforeOneHour = Calendar.getInstance();
        beforeOneHour.add(Calendar.HOUR, -1);

        Calendar beforeOneDay = Calendar.getInstance();
        beforeOneDay.add(Calendar.DATE, -1);

        DateFormat dataFormatForFinalDate = new SimpleDateFormat("yy.MM.dd", java.util.Locale.getDefault());
        try {
            assert serverDate != null;
            date = dateFormat_1.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (date == null) {
                    assert serverDate != null;
                    date = dateFormat_2.parse(serverDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String AndroidDate;
        assert date != null;
        if(date.after(beforeOneHour.getTime())) {
            time =  date.getTime() - beforeOneHour.getTimeInMillis();
            if(time > (59 * 60000) && time <= (60 * 600000)) {
                AndroidDate = "방금 전";
            } else {
                long minutesAgo = (60*60*1000 - time) / (60*1000);
                AndroidDate = String.valueOf((minutesAgo)).concat("분 전");
            }
        } else if(date.after(beforeOneDay.getTime())) {
            time =  date.getTime() - beforeOneDay.getTimeInMillis();
            long timesAgo = (24*60*60*1000 - time) / (60*60*1000);
            AndroidDate = String.valueOf(timesAgo).concat("시간 전");
        } else {
            AndroidDate = dataFormatForFinalDate.format(date);
        }

        return AndroidDate;
    }

    private boolean isReplyNoneText() {
        return postReply_et.getText().toString().trim().length() == 0;
    }

    public String getWriter_number() {
        return writer_number;
    }

    public int getBoardType() {
        return boardType;
    }


}

