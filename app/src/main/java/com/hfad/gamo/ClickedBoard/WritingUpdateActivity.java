package com.hfad.gamo.ClickedBoard;

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
import com.hfad.gamo.Component;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import com.hfad.gamo.Component;

import org.json.JSONException;
import org.json.JSONObject;

public class WritingUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int update = 10;

    private ImageView activity_writing_update_cancel_iv;
    private Button activity_writing_update_complete_bt;
    private EditText activity_writing_update_title_et;
    private EditText activity_writing_update_content_et;
    private VolleyForHttpMethod volley;
    private String urlForUpdatePosting;
    private String post_no = null;
    private String post_title;
    private String post_contents;
    private String reply_yn;
    private String major_name;
    private String subject_name;
    private String professor_name;
    private String user_no;
    private toClickedPosting toClickedPosting;

    //private toClickedPosting PostingData;
    private JSONObject forUpdatePosting;

    static public int getUpdateResponseCode() {
        return update;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_update);

        Intent intent = getIntent();
        toClickedPosting = intent.getExtras().getParcelable("PostingData");
        try {
            forUpdatePosting = new JSONObject(intent.getExtras().getString("forUpdatePosting"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initPostingUserData();
        initView();
        setViewEvent();
        initVolley();
        initUrl();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_writing_update_cancel_iv) {
            finish();
        } else if (v.getId() == R.id.activity_writing_update_complete_bt) {
            int titleTextLength = activity_writing_update_title_et.getText().toString().trim().length();
            int contentTextLength = activity_writing_update_content_et.getText().toString().trim().length();
            if(titleTextLength == 0 || contentTextLength == 0) {
                Toast.makeText(this, "제목이나 내용이 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {
                updatePosting();
            }
        }
    }

    private void initView() {
        activity_writing_update_cancel_iv = findViewById(R.id.activity_writing_update_cancel_iv);
        activity_writing_update_complete_bt = findViewById(R.id.activity_writing_update_complete_bt);
        activity_writing_update_title_et = findViewById(R.id.activity_writing_update_title_et);
        activity_writing_update_content_et = findViewById(R.id.activity_writing_update_content_et);

        activity_writing_update_title_et.setText(post_title);
        activity_writing_update_content_et.setText(post_contents);
    }

    private void initUrl() {
        urlForUpdatePosting = Component.default_url.concat(getString(R.string.updatePosting, post_no));
    }

    private void initVolley() {
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));
    }

    private void initPostingUserData() {
        try {
            post_no = forUpdatePosting.getString("post_no");
            post_title = toClickedPosting.getPost_title();
            post_contents = toClickedPosting.getPost_contents();
            reply_yn = forUpdatePosting.getString("reply_yn");
            major_name = forUpdatePosting.getString("major_name");
            subject_name = forUpdatePosting.getString("subject_name");
            professor_name = forUpdatePosting.getString("professor_name");
            user_no = forUpdatePosting.getString("user_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setViewEvent() {
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
            UpdatedData.put("post_title", title);
            UpdatedData.put("post_contents", contents);
            UpdatedData.put("reply_yn", reply_yn);
            UpdatedData.put("major_name", major_name);
            UpdatedData.put("subject_name", subject_name);
            UpdatedData.put("professor_name", professor_name);
            UpdatedData.put("user_no", user_no);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return UpdatedData;
    }


}