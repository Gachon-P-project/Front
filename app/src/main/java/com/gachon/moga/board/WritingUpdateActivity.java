package com.gachon.moga.board;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gachon.moga.VolleyForHttpMethod;
import com.gachon.moga.Component;
import com.gachon.moga.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.appConstantPreferences;

public class WritingUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int update = 10;
    private static final int subjectBoard = 0;
    private static final int freeBoard = 1;
    private static final int majorBoard = 2;

    private ImageView activity_writing_update_cancel_iv;
    private Button activity_writing_update_complete_bt;
    private EditText activity_writing_update_title_et;
    private EditText activity_writing_update_content_et;
    private VolleyForHttpMethod volley;
    private String urlForUpdatePosting;
    private String post_no = null;
    private String post_title;
    private String post_contents;
    private int boardType;
    private JSONObject realTimeDataForUpdatePosting;

    static public int getUpdateResponseCode() {
        return update;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_update);

        initialSetting();
    }

    private void initialSetting() {
        doAllFindViewById();
        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        Component.default_url = getString(R.string.defaultUrl);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        initInitialValues();
        setEvents();
        initUrl();

        setPreviousText();
    }

    private void initInitialValues() {
        Intent intent = getIntent();
        boardType = intent.getIntExtra("boardType", -1);
        try {
            realTimeDataForUpdatePosting = new JSONObject(intent.getExtras().getString("realTimeDataForUpdatePosting"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initPostingData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_writing_update_cancel_iv) {
            finish();
        } else if (v.getId() == R.id.activity_writing_update_complete_bt) {
            if(lengthIsZero()) {
                Toast.makeText(this, "제목이나 내용이 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {
                updatePosting();
            }
        }
    }

    private boolean lengthIsZero() {
        int titleTextLength = activity_writing_update_title_et.getText().toString().trim().length();
        int contentTextLength = activity_writing_update_content_et.getText().toString().trim().length();
        return titleTextLength == 0 || contentTextLength == 0;
    }

    private void setPreviousText() {
        activity_writing_update_title_et.setText(post_title);
        activity_writing_update_content_et.setText(post_contents);
    }

    private void doAllFindViewById() {
        activity_writing_update_cancel_iv = findViewById(R.id.activity_writing_update_cancel_iv);
        activity_writing_update_complete_bt = findViewById(R.id.activity_writing_update_complete_bt);
        activity_writing_update_title_et = findViewById(R.id.activity_writing_update_title_et);
        activity_writing_update_content_et = findViewById(R.id.activity_writing_update_content_et);
    }

    private void initUrl() {
        switch (boardType) {
            case subjectBoard:
                urlForUpdatePosting = Component.default_url.concat(getString(R.string.updatePostingOfSubjectBoard));
                break;
            case freeBoard:
                urlForUpdatePosting = Component.default_url.concat(getString(R.string.updatePostingOfFreeBoard));
                break;
            case majorBoard:
                urlForUpdatePosting = Component.default_url.concat(getString(R.string.updatePostingOfMajorBoard));
                break;
            default:
                break;
        }
    }

    private void initPostingData() {
        try {
            post_no = realTimeDataForUpdatePosting.getString("post_no");
            post_title = realTimeDataForUpdatePosting.getString("post_title");
            post_contents = realTimeDataForUpdatePosting.getString("post_contents");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setEvents() {
        activity_writing_update_cancel_iv.setOnClickListener(this);
        activity_writing_update_complete_bt.setOnClickListener(this);
    }

    private void updatePosting() {
        volley.putJSONObjectString(getUpdatedData(), urlForUpdatePosting, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onResponseUpdatePosting();
            }
        }, null);
    }

    private void onResponseUpdatePosting() {
        setResult(update);
        finish();
    }

    private JSONObject getUpdatedData() {
        String title = activity_writing_update_title_et.getText().toString();
        String contents = activity_writing_update_content_et.getText().toString();

        JSONObject UpdatedData = new JSONObject();
        try {
            UpdatedData.put("post_no", post_no);
            UpdatedData.put("post_title", title);
            UpdatedData.put("post_contents", contents);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return UpdatedData;
    }
}