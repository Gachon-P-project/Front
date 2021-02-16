package com.gachon.moga.board;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gachon.moga.VolleyForHttpMethod;
import com.gachon.moga.Component;
import com.gachon.moga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.appConstantPreferences;

public class WritingNestedReplyActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int subjectBoard = 0;
    private static final int freeBoard = 1;
    private static final int majorBoard = 2;
    private VolleyForHttpMethod volley;
    private ReplyAdapter adapter;
    private JSONObject commentJSONObject = new JSONObject();
    private JSONArray receivedJSONArray = new JSONArray();
    private SharedPreferences pref;
    private String reply_no;
    private String post_no;
    private String user_number;
    private String writer_number;
    private EditText edit_text_nested_reply;
    private ImageView post_nested_reply;
    private ImageButton imgBtnToolbarBack;
    private RecyclerView recyclerView;
    private String urlPostNestedReply;
    private int boardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_reply);

        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        initDefaultUrlOfComponent();
        doAllFindViewById();
        initVolley();
        initSharedPreference();

        user_number = pref.getString("number", null);

        Intent receivedIntent = getIntent();
        ArrayList<String> receivedData = receivedIntent.getStringArrayListExtra("replyData");
        writer_number = receivedIntent.getExtras().getString("writerNumber");
        boardType = receivedIntent.getExtras().getInt("boardType");
        
        // boardType 필요
        initUrl();
        
        for(int i = 0; i < receivedData.size(); i++) {
            try {
                receivedJSONArray.put(new JSONObject(receivedData.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            post_no = receivedJSONArray.getJSONObject(0).getString("post_no");
            reply_no = receivedJSONArray.getJSONObject(0).getString("reply_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                R.drawable.line_divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new ReplyAdapter(receivedJSONArray, this, "WritingNestedReplyActivity");
        recyclerView.setAdapter(adapter);

        post_nested_reply.setOnClickListener(this);
        imgBtnToolbarBack.setOnClickListener(this);
    }

    private void doAllFindViewById() {
        post_nested_reply = findViewById(R.id.activity_nested_reply_post_button);
        edit_text_nested_reply = findViewById(R.id.activity_nested_reply_edit);
        recyclerView = findViewById(R.id.activity_nested_reply_recycler);
        post_nested_reply = findViewById(R.id.activity_nested_reply_post_button);
        edit_text_nested_reply = findViewById(R.id.activity_nested_reply_edit);
        imgBtnToolbarBack = findViewById(R.id.img_btn_nested_reply_toolbar_back);
    }

    private void initDefaultUrlOfComponent() {
            Component.default_url = getString(R.string.defaultUrl);
    }

    private void initVolley() {
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));
    }

    private void initSharedPreference() {
        pref = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
    }

    private void initUrl() {
        switch (boardType) {
            case subjectBoard:
                urlPostNestedReply = Component.default_url.concat(getString(R.string.postNestedReplyOfSubjectBoard));
                break;
            case freeBoard:
                urlPostNestedReply = Component.default_url.concat(getString(R.string.postNestedReplyOfFreeBoard));
                break;
            case majorBoard:
                urlPostNestedReply = Component.default_url.concat(getString(R.string.postNestedReplyOfMajorBoard));
                break;
            default:
                break;
        }
    }

    private void setRequestValue() {
        try {
            commentJSONObject.put("user_no", user_number);
            commentJSONObject.put("post_no", post_no);
            commentJSONObject.put("reply_no", reply_no);
            commentJSONObject.put("reply_contents", edit_text_nested_reply.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public String getWriter_number() {
        return writer_number;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_nested_reply_post_button:
                setRequestValue();
                volley.postJSONObjectString(commentJSONObject, urlPostNestedReply, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }, null);
                break;
            case R.id.img_btn_nested_reply_toolbar_back:
                finish();
                break;
        }
    }
}
