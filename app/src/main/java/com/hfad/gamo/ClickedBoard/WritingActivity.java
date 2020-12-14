package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

public class WritingActivity extends AppCompatActivity {

    private static VolleyForHttpMethod volley;
    private static JSONObject requestJSONObject = new JSONObject();
    Intent intent;
    AppCompatEditText title_edit;
    AppCompatEditText contents_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        /*
        post_title: req.body.post_title,
        post_contents: req.body.post_contents,
        post_like: 0,
        wrt_date: new Date(),
        view_cnt: 1,
        reply_yn: req.body.reply_yn,
        major_name: req.body.major_name,
        subject_name: req.body.subject_name,
        professor_name: req.body.professor_name,
        user_id: req.body.user_id
         */
        // 제목
        title_edit = (AppCompatEditText) findViewById(R.id.board_write_title_edit);
        contents_edit = (AppCompatEditText)findViewById(R.id.board_write_contents_edit);

        title_edit.addTextChangedListener(titleWatcher);

        intent = getIntent();// major, subject, professor, user
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        // 상단바
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_clicked_board);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("글쓰기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);



        // 내용

        Log.d("test",""+title_edit.getText().toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_writing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_writing:
                writingBtnClick();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void writingBtnClick() {
        String url = "http://192.168.254.2:17394/board/insert";

        try {
            requestJSONObject.put("major_name", intent.getExtras().getString("major"));
            requestJSONObject.put("subject_name", intent.getExtras().getString("subject"));
            requestJSONObject.put("professor_name", intent.getExtras().getString("professor"));
            requestJSONObject.put("user_id", intent.getExtras().getString("user"));
            requestJSONObject.put("post_title", title_edit.getText().toString());
            requestJSONObject.put("post_contents", contents_edit.getText().toString());
            requestJSONObject.put("reply_yn", 1);
        } catch(JSONException e) {
            e.printStackTrace();
        }
//        Log.d("tag", ""+requestJSONObject);

        volley.postJSONObjectString(requestJSONObject, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("POST RESPONSE", response);
                finish();
            }
        });
    }

}