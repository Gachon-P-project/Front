package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.Component;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.hfad.gamo.Component.sharedPreferences;
import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class WritingNestedReplyActivity extends AppCompatActivity {

    private VolleyForHttpMethod volley;
    private ReplyAdapter adapter;
    private JSONObject commentJSONObject = new JSONObject();
    private JSONArray receivedJSONArray = new JSONArray();
    private SharedPreferences pref;
    private String reply_no;
    private String post_no;
    private String user_number;
    private String writer_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_reply);

        Component.default_url = getString(R.string.defaultUrl);

        pref = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        user_number = pref.getString("number", null);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        Intent receivedIntent = getIntent();
        ArrayList<String> receivedData = receivedIntent.getStringArrayListExtra("replyData");
        writer_number = receivedIntent.getExtras().getString("writerNumber");

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


        RecyclerView recyclerView = findViewById(R.id.activity_nested_reply_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                R.drawable.line_divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new ReplyAdapter(receivedJSONArray, this, "WritingNestedReplyActivity");
        recyclerView.setAdapter(adapter);


        ImageView post_nested_reply = findViewById(R.id.activity_nested_reply_post_button);
        final EditText edit_text_nested_reply = findViewById(R.id.activity_nested_reply_edit);

        post_nested_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlPostNestedReply = Component.default_url.concat(getString(R.string.postNestedReply,user_number,post_no,reply_no));

                try {
                    commentJSONObject.put("reply_contents", edit_text_nested_reply.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                volley.postJSONObjectString(commentJSONObject, urlPostNestedReply, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }, null);
            }
        });
    }

    public String getWriter_number() {
        return writer_number;
    }
}
