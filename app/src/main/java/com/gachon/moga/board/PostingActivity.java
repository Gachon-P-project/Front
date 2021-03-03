package com.gachon.moga.board;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
/*import androidx.recyclerview.widget.DividerItemDecoration;*/
import android.content.Intent;
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
import com.android.volley.toolbox.Volley;
import com.gachon.moga.VolleyForHttpMethod;
import com.gachon.moga.Component;

import static com.gachon.moga.DataIOKt.getUserNo;
import static com.gachon.moga.DataIOKt.getDepartment;
import com.gachon.moga.R;
import com.gachon.moga.board.models.ToPosting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.amountPerOnePage;
import static com.gachon.moga.StateKt.BOARD_FREE;
import static com.gachon.moga.StateKt.BOARD_MAJOR;
import static com.gachon.moga.StateKt.BOARD_SUBJECT;
import static com.gachon.moga.Component.sharedPreferences;

public class PostingActivity extends AppCompatActivity implements View.OnClickListener, ReplyDialogInterface, PostingDialogInterface, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "PostingActivity";
    public static int WritingNestedReplyActivityCode = 0;
    public static int WritingUpdateActivityCode = 1;
    public static boolean called_onStart = false;

    private VolleyForHttpMethod volley = null;
    private ReplyAdapter replyAdapter = null;
    private final JSONArray jsonArrayForReplyAdapter = new JSONArray();
    private final JSONObject jsonObjectForPostReply = new JSONObject();
    private SwipeRefreshLayout activity_clicked_posting_swipe;
    private EditText postReply_et = null;
    private ImageView postReply_iv = null;
    private ImageView post_like_img = null;
    private TextView post_like_text = null;
    private MenuItem menu_toolbar_clicked_posting_three_dots = null;
    TextView title = null;
    TextView nickName = null;
    TextView date = null;
    TextView contents = null;
    TextView reply_cnt = null;

    private String urlForInquireReplies;
    private String urlForPostReply;
    private String urlForPostLike;
    private String urlForInquirePostingsOfBoard;
    private String urlForDeletePosting;
    private String urlDeleteReply;
    private String urlDeleteNestedReply;
    private String post_no;
    private String writer_number;
    private int user_number;
    private int page_number;
    private boolean isLiked;
    private JSONObject realTimeDataForPosting = null;
    private String major;
    private int boardType;
    private boolean checkInitThreeDots = false;
    private boolean checkCallInquirePostingsOfBoard = false;
    private ToPosting toPosting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        initialSetting();

        initToolBar();
        initRecyclerViewForReply();
        initUrl();
        initView();
    }

    private void initialSetting() {
        doAllFindViewById();

        Component.default_url = getString(R.string.defaultUrl);
        sharedPreferences = this.getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        setEvents();
        activity_clicked_posting_swipe.setColorSchemeResources(R.color.indigo500);

        initInitialValues();
    }

    private void setEvents() {
        activity_clicked_posting_swipe.setOnRefreshListener(this);

        postReply_iv.setOnClickListener(this);
        post_like_img.setOnClickListener(this);
    }

   private void initInitialValues() {
        Intent intent = getIntent();
        toPosting = intent.getParcelableExtra("toPosting");

        boardType = toPosting.getBoardType();
        page_number = toPosting.getPageNo();
        post_no = String.valueOf(toPosting.getPostNo());
        writer_number = String.valueOf(toPosting.getWriterNo());
        user_number = getUserNo();
        major = getDepartment();
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
        checkInitThreeDots = true;

        if (!writer_number.equals(String.valueOf(user_number))) {
            menu_toolbar_clicked_posting_three_dots.setVisible(false);
        } else {
            if(!checkCallInquirePostingsOfBoard)
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
                DisplayMetrics displayMetricsForDeviceSize = getApplicationContext().getResources().getDisplayMetrics();
                PostingDialog clickedPostingDialog = new PostingDialog(this, realTimeDataForPosting);
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
            case R.id.activity_posting_post_reply_iv:
                if(isReplyNoneText()) {
                    Toast.makeText(this, "댓글이 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    postReply_iv.setEnabled(false);
                    setRequestDataOfPostReply();
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
            case R.id.activity_posting_post_like_iv:
                post_like_img.setEnabled(false);
                Log.i("postLike", "postLike");
                volley.postJSONObjectString(getRequestValuePostLike(),urlForPostLike, new Response.Listener<String>() {
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
            case R.id.img_btn_posting_toolbar_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onDeleteReplyDialog(int depth, int reply_no) {
        Log.d(TAG, "onDeleteReplyDialog: depth : " + depth + ", reply_no : " + reply_no);
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
                    setViewText("", "","", "", "", "");
                }
                inquirePostingsOfBoard();
                showAllReplies();
            }
        }
    }

    @Override
    public void onRefresh() {
        activity_clicked_posting_swipe.setEnabled(false);
        inquirePostingsOfBoard();
        showAllReplies();
        new Handler().postDelayed(() ->
                activity_clicked_posting_swipe.setRefreshing(false),500);
        new Handler().postDelayed(() ->
                activity_clicked_posting_swipe.setEnabled(true),1500);
    }

    private void inquireReplies() {
        volley.getJSONArray(urlForInquireReplies, response ->
                processReceivedReplies(response));
    }

    private void processReceivedReplies(JSONArray response) {
        if(response.length() != 0) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject responseJSONObject = response.getJSONObject(i);
                    jsonArrayForReplyAdapter.put(responseJSONObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        replyAdapter.notifyDataSetChanged();
    }

    private void postReply() {
        volley.postJSONObjectString(jsonObjectForPostReply, urlForPostReply, response ->
                showAllReplies(), null);
    }

    private void setRequestDataOfPostReply() {
        try {
            jsonObjectForPostReply.put("user_no", user_number);
            jsonObjectForPostReply.put("post_no", post_no);
            jsonObjectForPostReply.put("reply_contents", postReply_et.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showAllReplies() {
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

        replyAdapter = new ReplyAdapter(jsonArrayForReplyAdapter, this, "PostingActivity");
        recyclerViewForReply.setAdapter(replyAdapter);
        Log.i("recycler!!!" , "initRecyclerViewForReply");
    }

    private void initToolBar() {
        Toolbar tb = findViewById(R.id.activity_posting_toolbar);
        TextView tvToolbarTitle = findViewById(R.id.tv_posting_toolbar_title);
        ImageButton imgBtnToolbarBack = findViewById(R.id.img_btn_posting_toolbar_back);
        tvToolbarTitle.setText(getToolBarTitle());
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgBtnToolbarBack.setOnClickListener(this);
    }

    private void initView() {
        inquirePostingsOfBoard();
        inquireReplies();
    }

    private String getToolBarTitle() {
        String toolBarTitle =null;
        switch (boardType) {
            case BOARD_SUBJECT:
                toolBarTitle = toPosting.getSubjectName();
                break;
            case BOARD_FREE:
                toolBarTitle = "자유게시판";
                break;
            case BOARD_MAJOR:
                toolBarTitle = major;
                break;
            default:
                break;
        }
        return toolBarTitle;
    }

    private void doAllFindViewById() {
        title = findViewById(R.id.activity_posting_title);
        nickName = findViewById(R.id.activity_posting_nickname);
        date = findViewById(R.id.activity_posting_wrt_date);
        contents = findViewById(R.id.activity_posting_contents);
        reply_cnt = findViewById(R.id.activity_posting_reply_cnt);
        post_like_text = findViewById(R.id.activity_posting_post_like_text);
        post_like_img = findViewById(R.id.activity_posting_post_like_iv);
        postReply_et = findViewById(R.id.activity_posting_post_reply_et);
        postReply_iv = findViewById(R.id.activity_posting_post_reply_iv);
        activity_clicked_posting_swipe = findViewById(R.id.activity_posting_swipe);
    }

    private void setViewText(String titleText, String nickNameText, String dateText, String contentsText, String replyCntText,
                             String postLikeText) {
        title.setText(titleText);
        nickName.setText(nickNameText);
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

    private void initUrl() {
        urlForPostLike = Component.default_url.concat(getString(R.string.postLike));
        switch (boardType){
            case BOARD_SUBJECT:
                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfSubjectBoard));
                urlForPostReply = Component.default_url.concat(getString(R.string.postReplyOfSubjectBoard));
                urlForInquireReplies = Component.default_url.concat(getString(R.string.inquireRepliesOfSubjectBoard,post_no));
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfSubjectBoard,toPosting.getSubjectName(), toPosting.getProfessorName(), user_number));
                break;
            case BOARD_FREE:
                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfFreeBoard));
                urlForPostReply = Component.default_url.concat(getString(R.string.postReplyOfFreeBoard));
                urlForInquireReplies = Component.default_url.concat(getString(R.string.inquireRepliesOfFreeBoard,post_no));
//                urlDeleteReply = Component.default_url.concat(getString(R.string.deletePostingOfFreeBoard));
//                urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReplyOfFreeBoard));
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfFreeBoard, BOARD_FREE, user_number));
                break;
            case BOARD_MAJOR:
//                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfMajorBoard));
                urlForPostReply = Component.default_url.concat(getString(R.string.postReplyOfMajorBoard));
                urlForInquireReplies = Component.default_url.concat(getString(R.string.inquireRepliesOfMajorBoard, post_no));
//                urlDeleteReply = Component.default_url.concat(getString(R.string.deleteReplyOfMajorBoard));
//                urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReplyOfMajorBoard));
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfMajorBoard, BOARD_MAJOR, user_number, major));
                break;
        }
    }

    private void deleteReply(int reply_no) {
        switch (boardType) {
            case BOARD_SUBJECT:
                urlDeleteReply = Component.default_url.concat(getString(R.string.deleteReplyOfSubjectBoard, reply_no));
                break;
            case BOARD_FREE:
                urlDeleteReply = Component.default_url.concat(getString(R.string.deleteReplyOfFreeBoard, reply_no));
                break;
            case BOARD_MAJOR:
                urlDeleteReply = Component.default_url.concat(getString(R.string.deleteReplyOfMajorBoard, reply_no));
                break;
        }

        volley.delete(null, urlDeleteReply, response ->
                showAllReplies(), null);
    }

    private void deleteNestedReply(int reply_no) {
        switch (boardType) {
            case BOARD_SUBJECT:
                urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReplyOfSubjectBoard, reply_no));
                break;
            case BOARD_FREE:
                urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReplyOfFreeBoard, reply_no));
                break;
            case BOARD_MAJOR:
                urlDeleteNestedReply = Component.default_url.concat(getString(R.string.deleteNestedReplyOfMajorBoard, reply_no));
                break;
        }
        volley.delete(null, urlDeleteNestedReply, response ->
                showAllReplies(), null);
    }

    private void deletePosting() {
        switch (boardType) {
            case BOARD_SUBJECT:
                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfSubjectBoard, post_no));
                break;
            case BOARD_FREE:
                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfFreeBoard, post_no));
                break;
            case BOARD_MAJOR:
                urlForDeletePosting = Component.default_url.concat(getString(R.string.deletePostingOfMajorBoard, post_no));
                break;
        }

        volley.delete(null, urlForDeletePosting, response -> finish(), error ->
                error.printStackTrace());
    }

    private JSONObject findCurrentPosting(JSONArray ReceivedJsonArray) {
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
        volley.getJSONArray(urlForInquirePostingsOfBoard, response -> {

            showPostingContent(response);

            /*int firstPostNum = findFirstPostNum(response);
            int lastPostNum = findLastPostNum(response);
            int currentPostNum = Integer.parseInt(post_no);

            // 삭제된 게시물로 보여주기
            // 삭제된 현재 게시물의 postNum 가 현재 page 뿐만 아니라 앞의 page 안에도 없는 경우
            if(currentPostNum > lastPostNum) {
                showPostingContent(response);
            }

            if(firstPostNum <= currentPostNum && currentPostNum <= lastPostNum) {
                showPostingContent(response);
            } else if(currentPostNum < firstPostNum) {
                page_number--;
                initUrl();
                inquirePostingsOfBoard();
            }*/
        });
    }

    private int findFirstPostNum(JSONArray response) {
        int firstPostNum = -1;
        try {
            firstPostNum = response.getJSONObject(0).getInt("post_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return firstPostNum;
    }

    private int findLastPostNum(JSONArray response) {
        int LastPostNum = -1;
        try {
            LastPostNum = response.getJSONObject(amountPerOnePage - 1).getInt("post_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return LastPostNum;
    }

    private void showPostingContent(JSONArray response) {
        realTimeDataForPosting = findCurrentPosting(response);
        String titleText = null;
        String contentsText = null;
        String replyCntText = null;
        String postLikeText = null;
        String wrt_date = null;
        String nickName = null;
        int like_user = -1;

        if(realTimeDataForPosting != null) {

            try {
                titleText = realTimeDataForPosting.getString("post_title");
                wrt_date = realTimeDataForPosting.getString("wrt_date");
                contentsText = realTimeDataForPosting.getString("post_contents");
                replyCntText = realTimeDataForPosting.getString("reply_cnt");
                postLikeText = realTimeDataForPosting.getString("like_cnt");
                like_user = realTimeDataForPosting.getInt("like_user");
                nickName = realTimeDataForPosting.getString("nickname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            wrt_date = DateProcess.processServerDateToAndroidDate(wrt_date);

            setViewText(titleText,nickName, wrt_date, contentsText, replyCntText, postLikeText );
            initPostLikeUsingUserValue(like_user);
            checkCallInquirePostingsOfBoard = true;
            if(checkInitThreeDots)
            menu_toolbar_clicked_posting_three_dots.setEnabled(true);
        } else {
            setViewText("존재하지 않는 게시물 입니다.","익명" , "방금","","0", "0" );
            post_like_img.setEnabled(false);
            postReply_et.setEnabled(false);
            postReply_iv.setEnabled(false);
        }
    }

    private JSONObject getRequestValuePostLike() {
        JSONObject requestValue = new JSONObject();

        try {
            requestValue.put("post_no", post_no);
            requestValue.put("user_no", user_number);
            requestValue.put("board_flag", boardType);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return requestValue;
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