package com.hfad.gamo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {
    Button btnAppVersion, btnInquiry, btnDonate, btnOpenSource;
    androidx.appcompat.widget.Toolbar toolbar;
    int deviceWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        btnAppVersion = findViewById(R.id.btnAppVersion);
        btnInquiry = findViewById(R.id.btnInquiry);
        btnDonate = findViewById(R.id.btnDonate);
        btnOpenSource = findViewById(R.id.btnOpenSource);
        toolbar = findViewById(R.id.toolbarInformation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<b>이용안내</b>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);


        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        deviceWidth = dm.widthPixels;

        btnAppVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoDialog dialog = new InfoDialog(InformationActivity.this, deviceWidth);
                dialog.setTvInfo(getString(R.string.version));
                dialog.show();

            }
        });

        btnInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InfoDialog dialog = new InfoDialog(InformationActivity.this, deviceWidth);
                dialog.setTvInfo(getString(R.string.inquiry));
                dialog.show();

            }
        });

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InfoDialog dialog = new InfoDialog(InformationActivity.this, deviceWidth);
                dialog.setTvInfo(getString(R.string.donate));
                dialog.show();
            }
        });

        btnOpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                InfoDialog dialog = new InfoDialog(InformationActivity.this, deviceWidth);
                dialog.setTvInfo(getString(R.string.openSource));
                dialog.setTvInfoTextSize(14);
                dialog.show();

            }
        });



    }

    private class InfoDialog extends Dialog{

        TextView tvInfo;
        Context context;
        Button btnDialogPositive;

        public InfoDialog(@NonNull Context context, int deviceWidth) {
            super(context);
            this.context = context;
            setContentView(R.layout.dialog_info);
            tvInfo = findViewById(R.id.tvDialogInfo);
            btnDialogPositive = findViewById(R.id.btnDialogPositive);


            btnDialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            WindowManager.LayoutParams params = this.getWindow().getAttributes();
            params.width = (int)(deviceWidth * 0.95);
            this.onWindowAttributesChanged(params);


        }

        @Override
        public void create() {
            super.create();
        }

        public void setTvInfo(String s) {
            this.tvInfo.setText(s);
        }

        public void setTvInfoTextSize(int size) {
            this.tvInfo.setTextSize(size);
        }
    }
}