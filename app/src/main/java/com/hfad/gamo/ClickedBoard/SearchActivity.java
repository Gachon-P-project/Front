package com.hfad.gamo.ClickedBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hfad.gamo.R;
import com.hfad.gamo.VolleyForHttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {
    private static JSONObject requestJSONObject = new JSONObject();
    private static VolleyForHttpMethod volley;
    private static JSONArray requestJSONArray = new JSONArray();
    private static ClickedBoard_RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");
        final String board_title = intent.getExtras().getString("board_title");

        ImageView back_btn = (ImageView) findViewById(R.id.btn_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recycler_clicked_board);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final EditText search_edit = (EditText) findViewById(R.id.edit);
        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_SEARCH:
//                        Toast.makeText(getApplicationContext(), "검색"+search_edit.getText().toString(), Toast.LENGTH_LONG).show();

                        String url = "http://112.148.161.36:17394/board/select/컴퓨터구조/search/"+search_edit.getText().toString();

                        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        requestJSONObject = response.getJSONObject(i);
                                        requestJSONArray.put(requestJSONObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d("GET RESPONSE", ""+requestJSONArray);
                                adapter.notifyDataSetChanged();
                            }
                        });

                        adapter = new ClickedBoard_RecyclerAdapter(requestJSONArray, board_title);
                        recyclerView.setAdapter(adapter);
                        break;
                }
                return true;
            }
        });



    }
}