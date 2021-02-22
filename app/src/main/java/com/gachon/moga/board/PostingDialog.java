package com.gachon.moga.board;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.gachon.moga.R;

import org.json.JSONObject;

public class PostingDialog extends Dialog implements View.OnClickListener {


    private final PostingActivity postingActivity;
    private final JSONObject realTimeDataForUpdatePosting;
    private int boardType = -1;

    public PostingDialog(PostingActivity postingActivity, JSONObject realTimeDataForUpdatePosting) {
        super(postingActivity);

        this.postingActivity = postingActivity;
        this.realTimeDataForUpdatePosting = realTimeDataForUpdatePosting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_clicked_posting);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        TextView updatePosting = findViewById(R.id.dialog_clicked_posting_update);
        updatePosting.setOnClickListener(this);

        TextView deletePosting = findViewById(R.id.dialog_clicked_posting_delete);
        deletePosting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_clicked_posting_update:
                Intent intentToWritingUpdateActivity = new Intent(postingActivity,WritingUpdateActivity.class);
                intentToWritingUpdateActivity.putExtra("realTimeDataForUpdatePosting", realTimeDataForUpdatePosting.toString());
                intentToWritingUpdateActivity.putExtra("boardType", boardType);
                postingActivity.startActivityForResult(intentToWritingUpdateActivity, PostingActivity.WritingUpdateActivityCode);
                break;
            case R.id.dialog_clicked_posting_delete:
                postingActivity.onDeleteClickedPostingDialog();
                break;
            default:
                break;
        }
        dismiss();
    }

    public void setBoardType(int boardType) {
        this.boardType = boardType;
    }
}
