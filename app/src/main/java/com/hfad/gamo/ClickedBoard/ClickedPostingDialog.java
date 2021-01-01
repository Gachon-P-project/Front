package com.hfad.gamo.ClickedBoard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hfad.gamo.R;

public class ClickedPostingDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView updatePosting;
    private TextView deletePosting;

    public ClickedPostingDialog(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_clicked_posting);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        updatePosting = findViewById(R.id.dialog_clicked_posting_update);
        updatePosting.setOnClickListener(this);

        deletePosting = findViewById(R.id.dialog_clicked_posting_delete);
        deletePosting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_clicked_posting_update:
                break;
            case R.id.dialog_clicked_posting_delete:
                break;
            default:
                break;
        }
        dismiss();
    }

}
