package com.hfad.gamo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class NoticeDetailActivity extends AppCompatActivity {

    private static final String TAG = "NOTICE_DETAIL";
    private VolleyForHttpMethod volley;
    private String url;
    private SharedPreferences prefs;
    private String dept;
    private LoadingDialog loadingDialog;
    private String title, time, count, content;
    private String[] files;
    private Context context;
    private FragmentManager fm = getSupportFragmentManager();

    private String board_no;
    private TextView tvTitle, tvTime, tvCount, tvContent;
    private WebView wvContent;
    private LinearLayout llFiles;
    private androidx.appcompat.widget.Toolbar tb;
    private com.google.android.material.bottomnavigation.BottomNavigationView bottomNavNoticeDetail;
    private List<String> fileUrls;
    private String tempUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        context = getApplicationContext();

        tvTitle = findViewById(R.id.tvNoticeDetailTitle);
        tvTime = findViewById(R.id.tvNoticeDetailTime);
        tvCount = findViewById(R.id.tvNoticeDetailCount);
        tvContent = findViewById(R.id.tvNoticeDetailContent);
        llFiles = findViewById(R.id.llNoticeFile);
//        wvContent = view.findViewById(R.id.wvNoticeDetailContent);
        tb = findViewById(R.id.toolbarNoticeDetail);
        bottomNavNoticeDetail = findViewById(R.id.bottomNavNoticeDetail);


        prefs = context.getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        dept = prefs.getString("department", null);

        setSupportActionBar(tb);
        getSupportActionBar().setTitle(Html.fromHtml("<b>" + dept + "</b>", 0));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileUrls = new ArrayList<>();

        bottomNavNoticeDetail.setSelectedItemId(R.id.bottomNavigationNotice);


        Intent intent = getIntent();
        board_no = intent.getStringExtra("board_no");

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(context));
        url = Component.default_url + "/notice/posting/" + dept + "/" + board_no;

        loadingDialog = new LoadingDialog();
//        loadingDialog.start(context);
        getPostData();

        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        bottomNavNoticeDetail.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavPressed(item.getItemId());
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getPostData() {
        final String[] htmlData = new String[1];

        volley.getString(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                htmlData[0] = "<!DOCTYPE html>" + response;

                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    response = "<table>" + response.replaceAll("\t", "") + "</table>";
                    response = response.replaceAll("\n\n", "");
                    response = response.replaceAll("&npsp;", " ");


                    Log.d(TAG, "onResponse: response : " + response);

                    Document document = Jsoup.parse(response, "UTF-8");
                    Log.d(TAG, "onResponse: document : " + document);

                    Element body = document.body();
                    Element tbody = body.getElementsByTag("tbody").first();
                    Log.d(TAG, "onResponse: tbody : " + body.toString());
                    Element title = tbody.select("tr td").get(0);
                    Element count = tbody.select("tr td").get(2);
                    Element time = tbody.select("tr td").get(3);
                    Elements files = tbody.select("tr td").get(5).select("a");
                    int numOfFiles = files.size();
                    Elements content = tbody.select("p");

                    for (int i = 0 ; i < numOfFiles ; i++) {
                        TextView tv = new TextView(context);
                        tv.setText(files.get(i).text());
                        tv.setTextSize(14);
                        tv.setTextColor(Color.BLACK);

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.CENTER;
                        lp.topMargin = 10;
                        tv.setLayoutParams(lp);

                        fileUrls.add(files.get(i).attr("href"));
                        tempUrl = "https://www.gachon.ac.kr" + files.get(i).attr("href").replaceAll("amp;", "");

                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, tempUrl, Toast.LENGTH_SHORT).show();
                            }
                        });
                        llFiles.addView(tv);
                    }

                    tvTitle.setText(title.text());
                    tvCount.setText(count.text());
                    tvTime.setText(time.text());
                    tvContent.setText(Html.fromHtml(content.toString()));


                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadingDialog.finish();
            }
        });

    }

    private void bottomNavPressed(int itemId) {
        if(itemId == R.id.bottomNavigationNotice) {
            onBackPressed();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("flag", true);
            switch (itemId) {
                case R.id.bottomNavigationTimeTable :
                    intent.putExtra("itemName", "timetable");
                    break;
                case R.id.bottomNavigationBoard :
                    intent.putExtra("itemName", "board");
                    break;
                case R.id.bottomNavigationMyPage :
                    intent.putExtra("itemName", "mypage");
                    break;
            }
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            startActivity(intent);
//            finish();
//            overridePendingTransition(R.anim.fadeout, R.anim.fadein);
//            overridePendingTransition(, R.anim.fadein);
        }
    }





}