package com.gachon.moga.ClickedBoard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gachon.moga.VolleyForHttpMethod;
import com.gachon.moga.Component;
import com.gachon.moga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.getDepartment;
import static com.gachon.moga.DataIOKt.amountPerOnePage;
import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.getUserNo;
import static com.gachon.moga.StateKt.BOARD_FREE;
import static com.gachon.moga.StateKt.BOARD_MAJOR;
import static com.gachon.moga.StateKt.BOARD_SUBJECT;


public class ClickedBoardActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

    private static final int requestCodeToWritingActivity = 0;
    private static final String TAG = "ClickedBoardActivity";
    private static int boardType;
    private boolean inOnRefresh = false;
    private boolean isFinalPage = false;
    private JSONObject responseJSONObject = new JSONObject();
    private VolleyForHttpMethod volley;
    private ClickedBoard_RecyclerAdapter adapter;
    private final JSONArray responseJSONArray = new JSONArray();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_clicked_board;
    private ArrayList<String> a = new ArrayList<>();
    private String urlForInquirePostingsOfBoard;
    private String board_title, subject;
    private String professor, department;
    private int user_no;
    private Integer page_num = 0;
    private final int startIndex = 0;
    private ConstraintLayout activity_clicked_board_sleep_layout;
    private TextView activity_clicked_board_sleep_tv, textViewToolbarTitle;
    private ImageButton imageButtonToolbarBack, imageButtonSearch, imageButtonNewWriting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_board);

        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        initDefaultUrlOfComponent();
        initSharedPreferencesOfComponent();
        doAllFindViewById();
        initInitialValues();
        initBoardType();
        initUrl();
        initToolbar();
        initVolley();
        initRecyclerView();

        setEvents();

        swipe_clicked_board.setColorSchemeResources(R.color.indigo500);

        inquirePostingsOfBoard();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == requestCodeToWritingActivity) {
            if(resultCode == WritingActivity.completeCode) {
                swipe_clicked_board.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_clicked_board.setEnabled(true);
                    }
                }, 1000);
                clearRecyclerData();
                inquirePostingsOfBoard();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.imageButton_clickedBoard_toolbarBack:
                onBackPressed();
                break;
            case R.id.imageButton_clickedBoard_search:
                intent = new Intent(getBaseContext(), SearchActivity.class);
                intent.putExtra("professor", professor);
                intent.putExtra("subject", subject);
                intent.putExtra("user_no", user_no);
                intent.putExtra("boardType", boardType);
                startActivity(intent);
                break;
            case R.id.imageButton_clickedBoard_newWriting:
                intent = new Intent(getBaseContext(), WritingActivity.class);
                intent.putExtra("major", department);
                intent.putExtra("subject", subject);
                intent.putExtra("professor", professor);
                intent.putExtra("user_no", user_no);
                intent.putExtra("boardType", boardType);
                startActivityForResult(intent,requestCodeToWritingActivity);
                break;
        }
    }

    @Override
    public void onRefresh() {
        inOnRefresh = true;
        swipe_clicked_board.setEnabled(false);
        clearRecyclerData();
        inquirePostingsOfBoard();

        /*volley.getJSONArray(urlForInquirePostingsOfBoard, new Response.Listener<JSONArray>() {
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
                changeViewIfDoNotHaveData();
                adapter.notifyDataSetChanged();
                swipe_clicked_board.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_clicked_board.setEnabled(true);
                    }
                }, 1000);
            }
        });*/
    }

    private void doAllFindViewById() {
        activity_clicked_board_sleep_layout = findViewById(R.id.activity_clicked_board_sleep_layout);
        activity_clicked_board_sleep_tv = findViewById(R.id.activity_clicked_board_sleep_tv);
        textViewToolbarTitle = findViewById(R.id.textView_clickedBoard_toolbarTitle);
        imageButtonToolbarBack = findViewById(R.id.imageButton_clickedBoard_toolbarBack);
        imageButtonSearch = findViewById(R.id.imageButton_clickedBoard_search);
        imageButtonNewWriting = findViewById(R.id.imageButton_clickedBoard_newWriting);
        swipe_clicked_board = (SwipeRefreshLayout) findViewById(R.id.swipe_clicked_board);
        recyclerView = findViewById(R.id.activity_clicked_board_recycler_view);
    }

    private void initBoardType() {
        switch (boardType) {
            case BOARD_SUBJECT:
                subject = board_title;
                break;
            case BOARD_FREE:
            case BOARD_MAJOR:
                subject = null;
                break;
            default:        // ERROR
                Toast.makeText(this, "BOARD ERROR!", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
        }
    }

    private void initUrl() {
        switch(boardType) {
            case BOARD_SUBJECT:
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfSubjectBoard,subject, professor, user_no, page_num));
                break;
            case BOARD_FREE:
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfFreeBoard,boardType, user_no, page_num));
                break;
            case BOARD_MAJOR:
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfMajorBoard,boardType, user_no, department, page_num));
                break;
        }
    }

    private void initToolbar() {
        Toolbar tb = (Toolbar) findViewById(R.id.activity_clicked_board_toolbar);
        setSupportActionBar(tb);
        textViewToolbarTitle.setText(board_title);
    }

    private void initVolley() {
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClickedBoard_RecyclerAdapter(responseJSONArray, board_title, boardType, page_num);
        recyclerView.setAdapter(adapter);
    }

    private void initInitialValues() {
        Intent intent = getIntent();

        boardType = intent.getIntExtra("boardType", -1);
        board_title = intent.getExtras().getString("title");
        professor = intent.getExtras().getString("professor", "");
        user_no = Integer.parseInt(getUserNo());
        department = getDepartment();
    }

    private void initSharedPreferencesOfComponent() {
        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
    }

    private void initDefaultUrlOfComponent() {
        Component.default_url = getString(R.string.defaultUrl);
    }

    private void changeViewIfDoNotHaveData() {
        if(!doDataExist()) {
            swipe_clicked_board.setVisibility(View.GONE);
            activity_clicked_board_sleep_layout.setVisibility(View.VISIBLE);
        } else {
            swipe_clicked_board.setVisibility(View.VISIBLE);
            activity_clicked_board_sleep_layout.setVisibility(View.GONE);
        }
    }

    private boolean doDataExist() {
        return responseJSONArray.length() != 0;
    }

    private void inquirePostingsOfBoard() {
        volley.getJSONArray(urlForInquirePostingsOfBoard, new Response.Listener<JSONArray>() {
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

                changeViewIfDoNotHaveData();
                /*adapter.notifyDataSetChanged();*/

                if(response.length() == amountPerOnePage) {
                    adapter.notifyItemRangeChanged(startIndex, amountPerOnePage);
                    page_num++;
                } else {
                    adapter.notifyItemRangeChanged(startIndex, response.length());
                    isFinalPage = true;
                }

                if(inOnRefresh) {
                    swipe_clicked_board.setRefreshing(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipe_clicked_board.setEnabled(true);
                        }
                    }, 1000);
                    inOnRefresh = false;
                }
            }
        });
    }

    private void setEvents() {
        imageButtonToolbarBack.setOnClickListener(this);
        imageButtonSearch.setOnClickListener(this);
        imageButtonNewWriting.setOnClickListener(this);
        swipe_clicked_board.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(getOnScrollListener());
    }

    private RecyclerView.OnScrollListener getOnScrollListener() {
        RecyclerView.OnScrollListener result = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount && !isFinalPage) {
                    inquirePostingsOfBoard();
                }
            }
        };
        return result;
    }



    private void clearRecyclerData() {
        int original_length = responseJSONArray.length();
        int current_length = original_length;
        for (int i = 0; i < original_length; i++) {
            responseJSONArray.remove(--current_length);
        }

        page_num = 0;
        isFinalPage = false;
        /* if(!isReLoadOfPostings) {
            int original_length = responseJSONArray.length();
            int current_length = original_length;
            for (int i = 0; i < original_length; i++) {
                responseJSONArray.remove(--current_length);
            }
        } else {
            startIndexForEndlessScroll = page_num * 10;
            int current_length = responseJSONArray.length();
            do {
                responseJSONArray.remove(--current_length);
            } while (startIndexForEndlessScroll != current_length);
        }*/
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}