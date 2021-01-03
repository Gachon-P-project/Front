package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.Component;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.hfad.gamo.Component.sharedPreferences;

public class NestedReplyActivity extends AppCompatActivity {

    private JSONObject responseJSONObject = new JSONObject();
    private VolleyForHttpMethod volley;
    private ReplyAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private JSONObject commentJSONObject = new JSONObject();
    private int reply_no;
    private String post_no;
    private String user_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_reply);


        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        Intent receivedIntent = getIntent();
        reply_no = receivedIntent.getExtras().getInt("reply_no");
        post_no = receivedIntent.getExtras().getString("post_no");

        user_number = sharedPreferences.getString("number", null);

        RecyclerView recyclerView = findViewById(R.id.activity_nested_reply_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                R.drawable.line_divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        String url = Component.default_url.concat(getString(R.string.inquireReplies,post_no));

        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        responseJSONObject = response.getJSONObject(i);
                        responseJSONArray.put(responseJSONObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new ReplyAdapter(responseJSONArray, this, "NestedReplyActivity");
        recyclerView.setAdapter(adapter);


        ImageView post_nested_reply = findViewById(R.id.activity_nested_reply_post_button);
        final EditText edit_text_nested_reply = findViewById(R.id.activity_nested_reply_edit);

        post_nested_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String url = "http://172.30.1.2:17394/reply/insert/" + "jy11290" + "/" + toClickedPosting.getPost_no();

                String url = Component.default_url.concat(getString(R.string.postReply,user_number,post_no));

                try {
                    commentJSONObject.put("reply_contents", edit_text_nested_reply.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                volley.postJSONObjectString(commentJSONObject, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "댓글이 작성되었습니다.", Toast.LENGTH_SHORT).show();

                        int original_length = responseJSONArray.length();
                        int current_length = original_length;
                        for(int i = 0; i < original_length; i++) {
                            responseJSONArray.remove(--current_length);
                        }

                        String url = Component.default_url.concat(getString(R.string.inquireReplies,post_no));

                        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        responseJSONObject = response.getJSONObject(i);
                                        responseJSONArray.put(responseJSONObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }, null);
            }
        });
    }
}
