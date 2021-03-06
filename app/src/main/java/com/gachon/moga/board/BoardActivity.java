 package com.gachon.moga.board;

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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.gachon.moga.VolleyForHttpMethod;
import com.gachon.moga.Component;
import com.gachon.moga.R;
import com.gachon.moga.board.models.BoardInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.getDepartment;
import static com.gachon.moga.DataIOKt.amountPerOnePage;
import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.getUserNo;
import static com.gachon.moga.StateKt.BOARD_FREE;
import static com.gachon.moga.StateKt.BOARD_MAJOR;
import static com.gachon.moga.StateKt.BOARD_SUBJECT;


public class BoardActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener {

    private static final int requestCodeToWritingActivity = 0;
    private static int boardType;
    private boolean inOnRefresh = false;
    private boolean isFinalPage = false;
    private JSONObject responseJSONObject = new JSONObject();
    private VolleyForHttpMethod volley;
    private Board_RecyclerAdapter adapter;
    private final JSONArray responseJSONArray = new JSONArray();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_clicked_board;
    private String urlForInquirePostingsOfBoard;
    private String board_title, subject;
    private String professor, department;
    private int user_no;
    private Integer page_num = 0;
    private final int startIndex = 0;
    private ConstraintLayout activity_clicked_board_sleep_layout;
    private TextView textViewToolbarTitle;
    private ImageButton imageButtonToolbarBack, imageButtonSearch, imageButtonNewWriting;
    private BoardInfo boardInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        doAllFindViewById();
        initialSetting();
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

        if(v.getId() == R.id.activity_board_toolbarBack) {
            onBackPressed();
        } else if (v.getId() == R.id.activity_board_search) {
            intent = new Intent(getBaseContext(), SearchActivity.class);
            intent.putExtra("BoardInfo", boardInfo);
            startActivity(intent);
        } else if (v.getId() == R.id.activity_board_writing) {
            intent = new Intent(getBaseContext(), WritingActivity.class);
            intent.putExtra("BoardInfo", boardInfo);
            startActivityForResult(intent,requestCodeToWritingActivity);
        }
    }

    @Override
    public void onRefresh() {
        inOnRefresh = true;
        swipe_clicked_board.setEnabled(false);
        clearRecyclerData();
        inquirePostingsOfBoard();
    }

    private void doAllFindViewById() {
        activity_clicked_board_sleep_layout = findViewById(R.id.activity_board_sleep_layout);
        textViewToolbarTitle = findViewById(R.id.activity_board_toolbarTitle);
        imageButtonToolbarBack = findViewById(R.id.activity_board_toolbarBack);
        imageButtonSearch = findViewById(R.id.activity_board_search);
        imageButtonNewWriting = findViewById(R.id.activity_board_writing);
        swipe_clicked_board = (SwipeRefreshLayout) findViewById(R.id.activity_board_swipe_layout);
        recyclerView = findViewById(R.id.activity_board_recycler_view);
    }

    /*private void initByBoardType() {
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
    }*/

    private void initUrl() {
        switch(boardType) {
            case BOARD_SUBJECT:
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfSubjectBoard,board_title, professor, user_no));
                break;
            case BOARD_FREE:
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfFreeBoard,boardType, user_no));
                break;
            case BOARD_MAJOR:
                urlForInquirePostingsOfBoard = Component.default_url.concat(getString(R.string.inquirePostingsOfMajorBoard,boardType, user_no, department));
                break;
        }
    }

    private void initToolbar() {
        Toolbar tb = (Toolbar) findViewById(R.id.activity_board_toolbar);
        setSupportActionBar(tb);
        textViewToolbarTitle.setText(board_title);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Board_RecyclerAdapter(responseJSONArray, board_title, boardType, page_num);
        recyclerView.setAdapter(adapter);
    }

    private void initInitialValues() {
        Intent intent = getIntent();
        boardInfo = intent.getParcelableExtra("BoardInfo");

        board_title = boardInfo.getTitle();
        professor = boardInfo.getProfessor();
        boardType = boardInfo.getBoardType();
        user_no = getUserNo();
        department = getDepartment();
    }


    private void initialSetting() {
        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        Component.default_url = getString(R.string.defaultUrl);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        initInitialValues();
        //initByBoardType();
        initUrl();
        initToolbar();
        initRecyclerView();

        setEvents();

        swipe_clicked_board.setColorSchemeResources(R.color.indigo500);
    }

    private void changeViewIfDoNotHaveData() {
        if(!dataIsExist()) {
            swipe_clicked_board.setVisibility(View.GONE);
            activity_clicked_board_sleep_layout.setVisibility(View.VISIBLE);
        } else {
            swipe_clicked_board.setVisibility(View.VISIBLE);
            activity_clicked_board_sleep_layout.setVisibility(View.GONE);
        }
    }

    private boolean dataIsExist() {
        return responseJSONArray.length() != 0;
    }

    private void inquirePostingsOfBoard() {
        volley.getJSONArray(urlForInquirePostingsOfBoard, response -> {
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
            //whetherFinalPageOrNot(response);
            inOnRefresh();
        });
    }



    //startIndex 에러 일거임.
    private void whetherFinalPageOrNot(JSONArray response) {
        if(response.length() == amountPerOnePage) {
            adapter.notifyItemRangeChanged(startIndex, amountPerOnePage);
            page_num++;
        } else {
            adapter.notifyItemRangeChanged(startIndex, response.length());
            isFinalPage = true;
        }
    }

    private void inOnRefresh() {
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

    private void setEvents() {
        imageButtonToolbarBack.setOnClickListener(this);
        imageButtonSearch.setOnClickListener(this);
        imageButtonNewWriting.setOnClickListener(this);
        swipe_clicked_board.setOnRefreshListener(this);
        //recyclerView.addOnScrollListener(getOnScrollListener());
    }

    private RecyclerView.OnScrollListener getOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView1, int dx, int dy) {
                super.onScrolled(recyclerView1, dx, dy);
                if(dx == 0 && dy == 0) return;
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView1.getLayoutManager()).findLastVisibleItemPosition();
                int itemTotalCount = recyclerView1.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount && !isFinalPage) {
                    inquirePostingsOfBoard();
                }
            }
        };
    }

    private void clearRecyclerData() {
        int original_length = responseJSONArray.length();
        int current_length = original_length;
        for (int i = 0; i < original_length; i++) {
            responseJSONArray.remove(--current_length);
        }

        page_num = 0;
        isFinalPage = false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}