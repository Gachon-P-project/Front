package com.hfad.gamo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClickedBoardActivity extends AppCompatActivity {

    // Test용 JSONArray
    private JSONObject jsonObject1 = new JSONObject();
    private JSONObject jsonObject2 = new JSONObject();
    private JSONObject jsonObject3 = new JSONObject();

    private static JSONObject requestJSONObject = new JSONObject();
    private static VolleyForHttpMethod volley;
    private static JSONArray requestJSONArray = new JSONArray();
    private static ClickedBoard_RecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_board);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));


        RecyclerView recyclerView = findViewById(R.id.recycler_clicked_board);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String url = "http://172.30.1.2:17394/select/피프로젝트/황희정";

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
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new ClickedBoard_RecyclerAdapter(requestJSONArray);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}