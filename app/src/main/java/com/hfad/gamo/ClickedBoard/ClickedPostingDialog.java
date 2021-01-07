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

import org.json.JSONObject;

public class ClickedPostingDialog extends Dialog implements View.OnClickListener {


    private ClickedPostingActivity clickedPostingActivity;
    private ClickedPostingDialogInterface clickedPostingDialogInterface;
    private TextView updatePosting;
    private TextView deletePosting;
    private toClickedPosting PostingData;
    private String forUpdatePosting;

    public ClickedPostingDialog(ClickedPostingActivity clickedPostingActivity, toClickedPosting PostingData, String forUpdatePosting) {
        super(clickedPostingActivity);

        this.clickedPostingActivity = clickedPostingActivity;
        this.clickedPostingDialogInterface = clickedPostingActivity;
        this.PostingData = PostingData;
        this.forUpdatePosting = forUpdatePosting;
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
                Intent intentToWritingUpdateActivity = new Intent(clickedPostingActivity,WritingUpdateActivity.class);
                intentToWritingUpdateActivity.putExtra("PostingData", PostingData);
                intentToWritingUpdateActivity.putExtra("forUpdatePosting", forUpdatePosting);
                clickedPostingActivity.startActivityForResult(intentToWritingUpdateActivity, ClickedPostingActivity.WritingUpdateActivityCode);
                break;
            case R.id.dialog_clicked_posting_delete:
                clickedPostingDialogInterface.onDeleteClickedPostingDialog();
                break;
            default:
                break;
        }
        dismiss();
    }



}
