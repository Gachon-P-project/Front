package com.hfad.gamo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject1.put("1", "1");
            jsonObject1.put("2", "2");
            jsonObject2.put("1", "a");
            jsonObject2.put("2", "b");
            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);

            JSONArray jsonArray1 = new JSONArray();
            JSONObject jsonObject11 = new JSONObject();
            JSONObject jsonObject22 = new JSONObject();
            jsonObject11.put("1", "1");
            jsonObject11.put("2", "2");
            jsonObject22.put("1", "a");
            jsonObject22.put("2", "b");
            jsonArray1.put(jsonObject11);
            jsonArray1.put(jsonObject22);

            /*ArrayList<String> date = JSONArrayToArrayListOfString(jsonArray);
            for(int i = 0 ; i < date.size(); i++)
            System.out.println(date.get(i));*/

            jsonArray.put(jsonArray1);
            JSONArray jsonArray3 = (JSONArray) jsonArray.get(2);
            System.out.println(jsonArray3.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> JSONArrayToArrayListOfString(JSONArray jsonArray) throws JSONException {

        ArrayList<String> arrayList = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            arrayList.add(jsonArray.getJSONObject(0).toString());
        }

        return arrayList;
    }
}