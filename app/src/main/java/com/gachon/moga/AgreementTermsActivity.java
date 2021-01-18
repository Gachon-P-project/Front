package com.gachon.moga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class AgreementTermsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    Button btnAgree;
    CheckBox chkAgreeService, chkAgreePersonal, chkAgreeAll;
    TextView tvAgreeService, tvAgreePersonal, tvAgreeAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_terms);

        btnAgree = findViewById(R.id.btn_agreement_terms_next);
        chkAgreeService = findViewById(R.id.chk_agreement_terms_service);
        chkAgreePersonal = findViewById(R.id.chk_agreement_terms_personal);
        chkAgreeAll = findViewById(R.id.chk_agreement_terms_all);
        tvAgreeService = findViewById(R.id.tv_agreement_terms_service);
        tvAgreePersonal = findViewById(R.id.tv_agreement_terms_personal);
        tvAgreeAll = findViewById(R.id.tv_agreement_terms_all);

//        chkAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked)
//                    btnAgree.setBackgroundResource(R.drawable.btn_blue);
//                else
//                    btnAgree.setBackgroundResource(R.drawable.btn_lightgray);
//            }
//        });
        chkAgreeService.setOnCheckedChangeListener(this);
        chkAgreePersonal.setOnCheckedChangeListener(this);
        chkAgreeAll.setOnCheckedChangeListener(this);

        btnAgree.setOnClickListener(this);
        tvAgreeService.setOnClickListener(this);
        tvAgreePersonal.setOnClickListener(this);
        tvAgreeAll.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chk_agreement_terms_personal:
            case R.id.chk_agreement_terms_service:
                if(chkAgreeService.isChecked() && chkAgreePersonal.isChecked()){
                    chkAgreeAll.setChecked(true);
                    btnAgree.setBackgroundResource(R.drawable.btn_blue);
                } else{
                    btnAgree.setBackgroundResource(R.drawable.btn_lightgray);
                    chkAgreeAll.setChecked(false);
                }
                break;
            case R.id.chk_agreement_terms_all:
                if (isChecked) {
                    chkAgreeService.setChecked(true);
                    chkAgreePersonal.setChecked(true);
                    btnAgree.setBackgroundResource(R.drawable.btn_blue);
                } else {
                    chkAgreeService.setChecked(false);
                    chkAgreePersonal.setChecked(false);
                    btnAgree.setBackgroundResource(R.drawable.btn_lightgray);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_agreement_terms_service:
                chkAgreeService.setChecked(!chkAgreeService.isChecked());
                break;
            case R.id.tv_agreement_terms_personal:
                chkAgreePersonal.setChecked(!chkAgreePersonal.isChecked());
                break;
            case R.id.tv_agreement_terms_all:
                chkAgreeAll.setChecked(!chkAgreeAll.isChecked());
                break;
            case R.id.btn_agreement_terms_next:
                if (chkAgreeAll.isChecked()) {
                    Intent intent = new Intent(AgreementTermsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AgreementTermsActivity.this, "모든 약관에 동의해야만 이용이 가능합니다.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}