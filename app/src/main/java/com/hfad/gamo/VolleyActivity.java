package com.hfad.gamo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VolleyActivity extends AppCompatActivity {

    private RequestQueue queue = null;

    private static JSONObject jsonObject = new JSONObject();
    private static JSONArray jsonArray = new JSONArray();
    private static JSONArray final_jsonArray = new JSONArray();
    private static JSONObject final_jsonObject = new JSONObject();
    private static JSONObject result_Json_Object;
    private static JSONArray result_Json_Array;
    private static String result_String;


    private static ArrayList<String> detectedIngredient = new ArrayList<>();

    private static int result_int;
    private VolleyForHttpMethod volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(getApplicationContext()));

        try {
            jsonObject.put("user_id" , "jy11290");
            jsonObject.put("user_major" , "컴퓨터공학과");
            jsonObject.put("auth_level" , "0");
            jsonArray.put(jsonObject);
        } catch (JSONException e) {

        }

        volley.getJSONArray("http://172.30.1.2:17394/select/피프로젝트/황희정",null);
    }
}