package com.gachon.moga.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gachon.moga.Component;
import com.gachon.moga.LoadingDialog;
import com.gachon.moga.NoticeFragment;
import com.gachon.moga.R;
import com.gachon.moga.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.gachon.moga.Component.sharedPreferences;

import static android.view.KeyEvent.KEYCODE_ENTER;
import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.getDepartment;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private static final String TAG = "SearchActivity";
    private static final int subjectBoard = 0;
    private static final int freeBoard = 1;
    private static final int majorBoard = 2;
    private static JSONObject requestJSONObject = new JSONObject();
    private static VolleyForHttpMethod volley;
    private static JSONArray requestJSONArray = new JSONArray();
    private static ClickedBoard_RecyclerAdapter adapter;
    private String description = null;
    private int boardType;
    private String user_no, professor, subject, department;
    private LinearLayout llSearchDescription, llSearchNoResult;
    private TextView tvSearchBoardDescription;
    private LoadingDialog loadingDialog;
    private String urlSearchPostings;
    private RecyclerView recyclerView;
    private ImageView back_btn;
    private EditText search_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Component.default_url = getString(R.string.defaultUrl);

        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        initSharedPreferencesOfComponent();
        initVolley();

        Intent intent = getIntent();
        professor = intent.getStringExtra("professor");
        subject = intent.getStringExtra("subject");
        user_no = intent.getStringExtra("user_no");
        boardType = intent.getIntExtra("boardType", -1);
        department = getDepartment();

        loadingDialog = new LoadingDialog();

        doAllFindViewById();
        setBoardDescription();
        tvSearchBoardDescription.setText(description);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestJSONArray = new JSONArray();
//                adapter.notifyDataSetChanged();
                initRecyclerView();
                finish();
            }
        });

        requestJSONArray = new JSONArray();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        search_edit.setOnEditorActionListener(this);
    }

    @Override
    public void onBackPressed() {
        requestJSONArray = new JSONArray();
        initRecyclerView();
        super.onBackPressed();
    }

    private void initRecyclerView() {

        Log.d(TAG, "initRecyclerView: ");
        adapter = new ClickedBoard_RecyclerAdapter(requestJSONArray, subject, boardType);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initUrl(String word) {
        switch (boardType) {
            case subjectBoard:
                urlSearchPostings = Component.default_url.concat(getString(R.string.searchPostingsOfSubjectBoard,subject, professor, user_no, word));
                break;
            case freeBoard:
                urlSearchPostings = Component.default_url.concat(getString(R.string.searchPostingsOfFreeBoard,boardType, user_no, word));
                break;
            case majorBoard:
                urlSearchPostings = Component.default_url.concat(getString(R.string.searchPostingsOfMajorBoard,boardType, user_no, department, word));
                break;
            default:
                break;
        }
    }

    private void initVolley() {
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));
    }

    private void setBoardDescription() {
        switch (boardType) {
            case subjectBoard:
                description = subject.concat(tvSearchBoardDescription.getText().toString().substring(2));
                break;
            case freeBoard:
                description = "자유".concat(tvSearchBoardDescription.getText().toString().substring(2));
                break;
            case majorBoard:
                description = department.concat(tvSearchBoardDescription.getText().toString().substring(2));
                break;
            default:
                break;
        }
    }

    private void doAllFindViewById() {
        llSearchDescription = findViewById(R.id.llSearchBoardDescription);
        llSearchNoResult = findViewById(R.id.llSearchBoardNoResult);
        tvSearchBoardDescription = findViewById(R.id.tvSearchBoardDescription);
        back_btn = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.recycler_clicked_board);
        search_edit = (EditText) findViewById(R.id.edtSearch);
    }

    private void initSharedPreferencesOfComponent() {
        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
    }


    private void search(final NoticeFragment.VolleyCallback callback) {
        volley.getJSONArray(urlSearchPostings, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse: results : " + requestJSONArray);
                if(response.length() <= 0) {
                    requestJSONArray = new JSONArray();
                    adapter.notifyDataSetChanged();
                    recyclerView.removeAllViews();
                    llSearchDescription.setVisibility(View.GONE);
                    llSearchNoResult.setVisibility(View.VISIBLE);
                } else {
                    try {
                        requestJSONArray = response;
                        llSearchDescription.setVisibility(View.GONE);
                        llSearchNoResult.setVisibility(View.GONE);
                        Log.d(TAG, "onResponse: results : " + requestJSONArray);
//                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess();
                loadingDialog.finish();
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if( actionId == EditorInfo.IME_ACTION_SEARCH || actionId == KEYCODE_ENTER) {
            loadingDialog.start(SearchActivity.this);
            String word = search_edit.getText().toString();
            initUrl(word);
            search(new NoticeFragment.VolleyCallback() {
                @Override
                public void onSuccess() {
                    adapter.notifyDataSetChanged();
                    initRecyclerView();
                }
            });
            adapter = new ClickedBoard_RecyclerAdapter(requestJSONArray, subject, boardType);
            recyclerView.setAdapter(adapter);
        }
        return true;
    }
}