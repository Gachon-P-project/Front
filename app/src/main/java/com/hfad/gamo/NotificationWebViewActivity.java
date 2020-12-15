package com.hfad.gamo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NotificationWebViewActivity extends AppCompatActivity {

    private WebView wView;
    private ProgressBar pBar;
    private WebSettings wSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_web_view);

        wView = findViewById(R.id.wView);
        pBar = findViewById(R.id.pBar);

        initWebView(wView);

        wSettings = wView.getSettings();
        wSettings.setJavaScriptEnabled(true);
        wSettings.setSupportZoom(true);
        wSettings.setBuiltInZoomControls(true);


        // Later 수정.
        wView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Name of your downloadble file goes here, example: Mathematics II ");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();

            }
        });

        wView.loadUrl("https://www.gachon.ac.kr/major/bbs.jsp?mode=view&boardType_seq=159&board_no=1956&approve=&secret=&answer=&branch=&searchopt=&searchword=&pageSize=10&pageNum=0");

    }

    public void initWebView(WebView wView) {
        // 1. 웹뷰클라이언트 연결 (로딩 시작/끝 받아오기)
        wView.setWebViewClient(new WebViewClient() {
            @Override                                   // 1) 로딩 시작
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pBar.setVisibility(View.VISIBLE);       // 로딩이 시작되면 로딩바 보이기
            }

            @Override                                   // 2) 로딩 끝
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pBar.setVisibility(View.GONE);          // 로딩이 끝나면 로딩바 없애기
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (wView.canGoBack()) {
            wView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}