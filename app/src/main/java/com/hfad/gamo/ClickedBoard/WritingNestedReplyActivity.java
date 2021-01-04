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

import java.util.ArrayList;

import static com.hfad.gamo.Component.sharedPreferences;

public class WritingNestedReplyActivity extends AppCompatActivity {

    private VolleyForHttpMethod volley;
    private ReplyAdapter adapter;
    private JSONObject commentJSONObject = new JSONObject();
    private JSONArray receivedJSONArray = null;
    private int reply_no;
    private String post_no;
    private String user_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_reply);

        user_number = sharedPreferences.getString("number", null);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        Intent receivedIntent = getIntent();
        ArrayList<String> receivedData = receivedIntent.getStringArrayListExtra("replyData");
        receivedJSONArray = new JSONArray(receivedData);


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
                String url = Component.default_url.concat(getString(R.string.postReply,user_number,post_no));

                try {
                    commentJSONObject.put("reply_contents", edit_text_nested_reply.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                volley.postJSONObjectString(commentJSONObject, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        finish();
                    }
                }, null);
            }
        });
    }
}
