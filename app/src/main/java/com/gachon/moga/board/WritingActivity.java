package com.gachon.moga.board;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gachon.moga.VolleyForHttpMethod;
import com.gachon.moga.Component;
import com.gachon.moga.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.appConstantPreferences;

public class WritingActivity extends AppCompatActivity {

    public static final int completeCode = 10;
    private static final String TAG = "WritingActivity";
    private static final JSONObject requestJSONObject = new JSONObject();
    private static final int subjectBoard = 0;
    private static final int freeBoard = 1;
    private static final int majorBoard = 2;
    private static VolleyForHttpMethod volley;
    private static int boardType;

    private String url;
    private Intent intent;
    private EditText title_edit;
    private EditText contents_edit;
    private ImageView btn_cancel;
    private Button btn_complete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("WritingActivityLog", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        Component.default_url = getString(R.string.defaultUrl);
        // 제목
        title_edit = findViewById(R.id.board_write_title_edit);
        contents_edit = findViewById(R.id.board_write_contents_edit);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_complete = findViewById(R.id.btn_complete);

        title_edit.addTextChangedListener(titleWatcher);

        intent = getIntent();// major, subject, professor, user_no
        boardType = intent.getIntExtra("boardType", -1);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNoneText()) {
                    Toast.makeText(view.getContext(), "제목이나 내용이 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                } else {
                    writingBtnClick();
                }
            }
        });

        if(boardType == -1) {
            Toast.makeText(this, "BOARD ERROR!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    TextWatcher titleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable ed) {
            if(ed.length() > 60){
                Toast.makeText(WritingActivity.this, "제목은 60자를 넘을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean isNoneText() {
        return isTitleNoneText() || isContentsNoneText();
    }

    private boolean isTitleNoneText() {
        return title_edit.getText().toString().trim().length() == 0;
    }

    private boolean isContentsNoneText() {
        return contents_edit.getText().toString().trim().length() == 0;
    }



    private void writingBtnClick() {

        classifyBoard();
        setRequestValue();

        Log.d(TAG, "writingBtnClick: writing data: " + requestJSONObject.toString() );
        volley.postJSONObjectString(requestJSONObject, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("WritingActivityLog", "onResponse");
                setResult(completeCode);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    private void classifyBoard() {
        switch (boardType) {
            case subjectBoard:
                url = Component.default_url.concat(getString(R.string.postSubjectWriting, 1));
                break;
            case freeBoard:
                url = Component.default_url.concat(getString(R.string.postFreeWriting, 1));
                break;
            case majorBoard:
                url = Component.default_url.concat(getString(R.string.postMajorWriting,1));
                break;
        }
    }

    @Override
    protected void onStart() {
        Log.i("WritingActivityLog", "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("WritingActivityLog", "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("WritingActivityLog", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("WritingActivityLog", "onStop");
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        Log.i("WritingActivityLog", "onDestroy");
        super.onDestroy();
    }

    void setRequestValue() {
        if(boardType == subjectBoard) {
            setRequestValueOfSubject();
        } else {
            setRequestValueOfDeptBoardAndFreeBoard();
        }
    }

    void setRequestValueOfSubject() {
        try {
            requestJSONObject.put("post_title", title_edit.getText().toString());
            requestJSONObject.put("post_contents", contents_edit.getText().toString());
            requestJSONObject.put("major_name", intent.getExtras().getString("major"));
            requestJSONObject.put("subject_name", intent.getExtras().getString("subject"));
            requestJSONObject.put("professor_name", intent.getExtras().getString("professor"));
            requestJSONObject.put("user_no", intent.getExtras().getString("user_no"));
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    void setRequestValueOfDeptBoardAndFreeBoard() {
        try {
            requestJSONObject.put("post_title", title_edit.getText().toString());
            requestJSONObject.put("post_contents", contents_edit.getText().toString());
            requestJSONObject.put("major_name", intent.getExtras().getString("major"));
            requestJSONObject.put("user_no", intent.getExtras().getString("user_no"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}