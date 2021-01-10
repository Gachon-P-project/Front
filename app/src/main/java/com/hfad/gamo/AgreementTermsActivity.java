package com.hfad.gamo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class AgreementTermsActivity extends AppCompatActivity {

    Button btnAgree;
    CheckBox chkAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_terms);

        btnAgree = findViewById(R.id.btn_agreement_terms_next);
        chkAgree = findViewById(R.id.chk_agreement_terms);

        chkAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    btnAgree.setBackgroundResource(R.drawable.btn_blue);
                else
                    btnAgree.setBackgroundResource(R.drawable.btn_lightgray);
            }
        });

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkAgree.isChecked()) {
                    Intent intent = new Intent(AgreementTermsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AgreementTermsActivity.this, "이용 약관에 동의해야만 이용이 가능합니다.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}