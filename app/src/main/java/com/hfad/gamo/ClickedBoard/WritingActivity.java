package com.hfad.gamo.ClickedBoard;

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
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.Component;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

public class WritingActivity extends AppCompatActivity {

    public static final int completeCode = 10;
    private static final String TAG = "WritingActivity";
    private static VolleyForHttpMethod volley;
    private static final JSONObject requestJSONObject = new JSONObject();
    private static int boardType;
    private String url;
    Intent intent;
    EditText title_edit;
    EditText contents_edit;
    ImageView btn_cancel;
    Button btn_complete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("WritingActivityLog", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

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
        try {
            requestJSONObject.put("post_title", title_edit.getText().toString());
            requestJSONObject.put("post_contents", contents_edit.getText().toString());
            requestJSONObject.put("major_name", intent.getExtras().getString("major"));
            requestJSONObject.put("subject_name", intent.getExtras().getString("subject"));
            requestJSONObject.put("professor_name", intent.getExtras().getString("professor"));
            requestJSONObject.put("user_no", intent.getExtras().getString("user_no"));
            requestJSONObject.put("board_flag", boardType);
            if(boardType == 1)
                requestJSONObject.put("reply_yn", 1);
        } catch(JSONException e) {
            e.printStackTrace();
        }
//        Log.d("tag", ""+requestJSONObject);

        volley.postJSONObjectString(requestJSONObject, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("WritingActivityLog", "onResponse");
                setResult(completeCode);
                finish();
            }
        }, null);
    }

    private void classifyBoard() {
        switch (boardType) {
            case 0:         // 수업게시판
                url = Component.default_url.concat(getString(R.string.postSubjectWriting, 1));
                break;
            case 1:         // 자유게시판
                url = Component.default_url.concat(getString(R.string.postFreeWriting));
                break;
            case 2:         // 학과게시판
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
}