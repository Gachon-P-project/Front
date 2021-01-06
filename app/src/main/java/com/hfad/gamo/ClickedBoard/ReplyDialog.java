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

import java.util.ArrayList;

public class ReplyDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ClickedPostingActivity clickedPostingActivity = null;
    private TextView postRereply;
    private int depth;
    private ArrayList<String> dataUsedInWritingNestedReplyActivity = null;

    public ReplyDialog(@NonNull Context context, int depth) {
        super(context);

        this.context = context;
        this.depth = depth;
    }

    public ReplyDialog(@NonNull Context context, int depth, ArrayList<String> dataUsedInWritingNestedReplyActivity,
                       ClickedPostingActivity clickedPostingActivity, String writer_number) {
        super(context);

        this.context = context;
        this.dataUsedInWritingNestedReplyActivity = dataUsedInWritingNestedReplyActivity;
        this.depth = depth;
        this.clickedPostingActivity = clickedPostingActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reply);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        postRereply = findViewById(R.id.dialogReply_postRereply);
        postRereply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogReply_postRereply:
                Intent WritingNestedReplyIntent = new Intent(context, WritingNestedReplyActivity.class);
                WritingNestedReplyIntent.putStringArrayListExtra("replyData", dataUsedInWritingNestedReplyActivity);
                clickedPostingActivity.startActivityForResult(WritingNestedReplyIntent, ClickedPostingActivity.WritingNestedReplyActivityCode);

                break;
            default:
                break;
        }
        dismiss();
    }

}
