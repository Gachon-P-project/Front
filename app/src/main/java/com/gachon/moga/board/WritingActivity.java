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
import com.gachon.moga.board.models.BoardInfo;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.getDepartment;
import static com.gachon.moga.DataIOKt.getUserNo;

public class WritingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int completeCode = 10;
    private static final String TAG = "WritingActivity";
    private static final JSONObject requestJSONObject = new JSONObject();
    private static final int subjectBoard = 0;
    private static final int freeBoard = 1;
    private static final int majorBoard = 2;
    private static VolleyForHttpMethod volley;
    private static int boardType;

    private String url;
    private EditText title_edit;
    private EditText contents_edit;
    private ImageView btn_cancel;
    private Button btn_complete;
    private String subject;
    private String professor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("WritingActivityLog", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        initialSetting();
    }

    private void initialSetting() {
        doAllFindViewById();

        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        Component.default_url = getString(R.string.defaultUrl);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        initInitialValues();
        setEvents();


    }

    private void doAllFindViewById() {
        title_edit = findViewById(R.id.board_write_title_edit);
        contents_edit = findViewById(R.id.board_write_contents_edit);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_complete = findViewById(R.id.btn_complete);
    }

    private void initInitialValues() {
        Intent intent = getIntent();
        BoardInfo boardInfo = intent.getParcelableExtra("BoardInfo");

        boardType = boardInfo.getBoardType();
        subject = boardInfo.getTitle();
        professor = boardInfo.getProfessor();
    }

    private void setEvents() {
        btn_cancel.setOnClickListener(this);
        btn_complete.setOnClickListener(this);
        title_edit.addTextChangedListener(getTitleWatcher());
    }



    private TextWatcher getTitleWatcher() {
        return new TextWatcher() {
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
    }


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
            requestJSONObject.put("major_name", getDepartment());
            requestJSONObject.put("subject_name", subject);
            requestJSONObject.put("professor_name", professor);
            requestJSONObject.put("user_no", getUserNo());
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    void setRequestValueOfDeptBoardAndFreeBoard() {
        try {
            requestJSONObject.put("post_title", title_edit.getText().toString());
            requestJSONObject.put("post_contents", contents_edit.getText().toString());
            requestJSONObject.put("major_name", getDepartment());
            requestJSONObject.put("user_no", getUserNo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            finish();
        } else if (v.getId() == R.id.btn_complete) {
            if(isNoneText()) {
                Toast.makeText(v.getContext(), "제목이나 내용이 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {
                writingBtnClick();
            }
        }
    }
}