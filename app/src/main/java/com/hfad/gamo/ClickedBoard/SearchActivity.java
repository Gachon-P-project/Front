package com.hfad.gamo.ClickedBoard;

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
import com.hfad.gamo.Component;
import com.hfad.gamo.LoadingDialog;
import com.hfad.gamo.NoticeFragment;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static JSONObject requestJSONObject = new JSONObject();
    private static VolleyForHttpMethod volley;
    private static JSONArray requestJSONArray = new JSONArray();
    private static ClickedBoard_RecyclerAdapter adapter;
    private String user_no, professor, subject;
    private LinearLayout llSearchDescription, llSearchNoResult;
    private TextView tvSearchBoardDescription;
    private LoadingDialog loadingDialog;
    private String url;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        professor = intent.getStringExtra("professor");
        subject = intent.getStringExtra("board_title");
        user_no = intent.getStringExtra("user_no");

        loadingDialog = new LoadingDialog();
        llSearchDescription = findViewById(R.id.llSearchBoardDescription);
        llSearchNoResult = findViewById(R.id.llSearchBoardNoResult);
        tvSearchBoardDescription = findViewById(R.id.tvSearchBoardDescription);
        String description = subject + tvSearchBoardDescription.getText().toString().substring(3);
        tvSearchBoardDescription.setText(description);

        ImageView back_btn = (ImageView) findViewById(R.id.btn_back);
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
        recyclerView = findViewById(R.id.recycler_clicked_board);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final EditText search_edit = (EditText) findViewById(R.id.edtSearch);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( i == EditorInfo.IME_ACTION_SEARCH || i == KEYCODE_ENTER) {
                        loadingDialog.start(SearchActivity.this);
                        String word = search_edit.getText().toString();
                        url = Component.default_url.concat(getString(R.string.searchWantedPostingsOfBoard,subject, professor, user_no, word));
                        Log.d(TAG, "onEditorAction: url : " + url);
                        search(new NoticeFragment.VolleyCallback() {
                            @Override
                            public void onSuccess() {
                                adapter.notifyDataSetChanged();
                                initRecyclerView();

                            }
                        });
                        adapter = new ClickedBoard_RecyclerAdapter(requestJSONArray, subject);
                        recyclerView.setAdapter(adapter);
                }
                return true;
            }
        });
    }

    private void initRecyclerView() {

        Log.d(TAG, "initRecyclerView: ");
        adapter = new ClickedBoard_RecyclerAdapter(requestJSONArray, subject);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    private void search(final NoticeFragment.VolleyCallback callback) {
        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
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
}