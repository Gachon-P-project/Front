package com.gachon.moga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gachon.moga.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import org.w3c.dom.Document;

import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.appConstantPreferences;

public class NoticeDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "NOTICE_DETAIL";
    private VolleyForHttpMethod volley;
    private SharedPreferences prefs;
    private String dept;
    private LoadingDialog loadingDialog;
    private Context context;
    private String noticeUrl;

    private String board_no;
    private TextView tvTitle, tvTime, tvCount, textViewToolbarTitle;
    private ImageButton imageButtonToolbarBack, imageButtonOpenInBrowser;
    private WebView wvContent;
    private LinearLayout llFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        Component.default_url = getString(R.string.defaultUrl);
        sharedPreferences = getSharedPreferences(appConstantPreferences, MODE_PRIVATE);

        context = getApplicationContext();

        tvTitle = findViewById(R.id.tvNoticeDetailTitle);
        tvTime = findViewById(R.id.tvNoticeDetailTime);
        tvCount = findViewById(R.id.tvNoticeDetailCount);
        llFiles = findViewById(R.id.llNoticeFile);
        wvContent = findViewById(R.id.wvNoticeDetailContent);
        textViewToolbarTitle = findViewById(R.id.textView_noticeDetail_toolbarTitle);
        imageButtonToolbarBack = findViewById(R.id.imageButton_noticeDetail_toolbarBack);
        imageButtonOpenInBrowser = findViewById(R.id.imageButton_noticeDetail_openInBrowser);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarNoticeDetail);


        prefs = context.getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        dept = prefs.getString("department", null);

        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(Html.fromHtml("<b>" + dept + "</b>", 0));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);
        textViewToolbarTitle.setText(dept);
        imageButtonToolbarBack.setOnClickListener(this);
        imageButtonOpenInBrowser.setOnClickListener(this);

        Intent intent = getIntent();
        board_no = intent.getStringExtra("board_no");

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(context));

        loadingDialog = new LoadingDialog();
        loadingDialog.start(this);
        getPostData();
        getNoticeUrl();

//        하단 네비바
//        bottomNavNoticeDetail.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                bottomNavPressed(item.getItemId());
//                return true;
//            }
//        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getPostData() {
        final String[] htmlData = new String[1];

        String url = Component.default_url + getString(R.string.noticeDetail, dept, board_no);
        volley.getString(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                htmlData[0] = "<!DOCTYPE html>" + response;

                try {

                    response = "<table>" + response.replaceAll("\t", "") + "</table>";
                    response = response.replaceAll("\n\n", "");
                    response = response.replaceAll("&npsp;", "");


                    Document document = Jsoup.parse(response, "UTF-8");

                    Element body = document.body();
                    Element tbody = body.getElementsByTag("tbody").first();
                    Elements eContent = tbody.select("tr td").get(6).children();
                    Element title = tbody.select("tr td").get(0);
                    Element count = tbody.select("tr td").get(2);
                    Element time = tbody.select("tr td").get(3);
                    Elements files = tbody.select("tr td").get(5).select("a");
                    int numOfFiles = files.size();
//                    Log.d(TAG, "onResponse: tbody : " + tbody.toString());

                    for (int i = 0 ; i < numOfFiles ; i++) {
                        TextView tv = new TextView(context);
                        tv.setText("   " + files.get(i).text());
                        tv.setTextSize(12);
                        tv.setTextColor(Color.BLUE);

                        String filename = tv.getText().toString();
                        String extension = filename.substring(filename.indexOf(".")+1);
                        while(extension.contains(".")) {
                            extension = extension.substring(extension.indexOf(".")+1);
                        }
                        switch (extension) {
                            case "hwp" :
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_hwp, 0, 0, 0);
                                break;
                            case "pdf":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pdf, 0, 0, 0);
                                break;
                            case "doc":
                            case "docx":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_doc, 0, 0, 0);
                                break;
                            case "jpg":
                            case "jpeg":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_jpg, 0, 0, 0);
                                break;
                            case "png":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_png, 0, 0, 0);
                                break;
                            case "mp4":
                            case "mov":
                            case "wmv":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mp4, 0, 0, 0);
                                break;
                            case "ppt":
                            case "pptx":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_ppt, 0, 0, 0);
                                break;
                            case "txt":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_txt, 0, 0, 0);
                                break;
                            case "xls":
                            case "xlsx":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_xls, 0, 0, 0);
                                break;
                            case "zip":
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_zip, 0, 0, 0);
                                break;
                            default:
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_etc, 0, 0, 0);
                        }

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.CENTER;
                        lp.topMargin = 10;
                        tv.setLayoutParams(lp);
                        final String fileUrl = getString(R.string.gachonBaseUrl) + files.get(i).attr("href").replaceAll("amp;", "");
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(fileUrl));
                                startActivity(intent);
                            }
                        });
                        llFiles.addView(tv);
                    }

                    tvTitle.setText(title.text());
                    tvCount.setText(count.text());
                    tvTime.setText(time.text());

                    initWebView(wvContent);
                    wvContent.setWebViewClient(new WebViewClient());
                    WebSettings webSettings = wvContent.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setSupportZoom(false);
                    webSettings.setBuiltInZoomControls(false);
                    webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

                    String css = "";
                    if(!eContent.select("table").isEmpty()) {
                        css =
                                "        <style>\n" +
                                "            table {\n" +
                                "                border-top : gray 1px solid;\n" +
                                "                border-bottom: gray 1px solid;\n" +
                                "                text-align: center;\n" +
                                "            }\n" +
                                "            tbody tr:nth-of-type(1) td {\n" +
                                "                background-color:lightsteelblue;\n" +
                                "            }\n" +
                                "            tbody tr {\n" +
                                "                border-top : gray 1px solid;\n" +
                                "            }\n" +
                                "        </style>";
                    }

                    Log.d(TAG, "onResponse: eContent : " + eContent.toString());
                    String htmlContent = "<html><head>" + css + "</head><body>" + eContent.toString() + "<br/></body></html>";
                    wvContent.loadDataWithBaseURL(null, htmlContent, "text/html; charset=utf-8", "UTF-8", null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadingDialog.finish();
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButton_noticeDetail_toolbarBack:
                onBackPressed();
                break;
            case R.id.imageButton_noticeDetail_openInBrowser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(noticeUrl));
                startActivity(intent);
                break;
        }
    }

    public void initWebView(WebView wView) {
        // 1. 웹뷰클라이언트 연결 (로딩 시작/끝 받아오기)
        wView.setWebViewClient(new WebViewClient() {
            @Override                                   // 1) 로딩 시작
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                pBar.setVisibility(View.VISIBLE);       // 로딩이 시작되면 로딩바 보이기
            }

            @Override                                   // 2) 로딩 끝
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                pBar.setVisibility(View.GONE);          // 로딩이 끝나면 로딩바 없애기
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_toolbar_open_in_browser, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//            case android.R.id.home :
//                onBackPressed();
//                return true;
//            case R.id.action_open_in_browser :
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(noticeUrl));
//                startActivity(intent);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void getNoticeUrl() {
//        String url = Component.default_url + "/notice/url/" + dept + "/" + board_no;
        String url = Component.default_url + getString(R.string.noticeUrl, dept, board_no);
        volley.getString(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                noticeUrl = response;
            }
        });
    }
}