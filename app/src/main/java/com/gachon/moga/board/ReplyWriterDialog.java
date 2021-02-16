package com.gachon.moga.board;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gachon.moga.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReplyWriterDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PostingActivity clickedPostingActivity = null;
    private TextView postRereply;
    private TextView deleteReply;
    private int depth;
    private int reply_no;
    private String writer_number = null;
    private ArrayList<String> dataUsedInWritingNestedReplyActivity = null;
    private ReplyDialogInterface replyDialogInterface = null;

    /*public ReplyWriterDialog(@NonNull Context context, int depth, PostingActivity clickedPostingActivity) {
        super(context);
        this.context = context;
        this.depth = depth;
        this.clickedPostingActivity = clickedPostingActivity;
        this.replyDialogInterface = clickedPostingActivity;
    }*/

    public ReplyWriterDialog(@NonNull Context context, PostingActivity clickedPostingActivity, JSONObject DataForReply, String writer_number) {
        super(context);
        this.context = context;
        this.clickedPostingActivity = clickedPostingActivity;
        this.replyDialogInterface = clickedPostingActivity;
        this.writer_number = writer_number;
        try {
            this.depth = DataForReply.getInt("depth");
            this.reply_no = DataForReply.getInt("reply_no");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.d("replyWriterDialog", "onClick: reply_no:" + reply_no);
    }

    /*public ReplyWriterDialog(@NonNull Context context, int depth, ArrayList<String> dataUsedInWritingNestedReplyActivity,
                             PostingActivity clickedPostingActivity) {
        super(context);
        this.context = context;
        this.depth = depth;
        this.dataUsedInWritingNestedReplyActivity = dataUsedInWritingNestedReplyActivity;
        this.clickedPostingActivity = clickedPostingActivity;
    }*/

    public ReplyWriterDialog(@NonNull Context context, PostingActivity clickedPostingActivity,
                             ArrayList<String> dataUsedInWritingNestedReplyActivity, JSONObject DataForReply, String writer_number) {
        super(context);
        this.context = context;
        this.dataUsedInWritingNestedReplyActivity = dataUsedInWritingNestedReplyActivity;
        this.clickedPostingActivity = clickedPostingActivity;
        this.replyDialogInterface = clickedPostingActivity;
        this.writer_number = writer_number;
        try {
            this.depth = DataForReply.getInt("depth");
            this.reply_no = DataForReply.getInt("reply_no");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.d("replyWriterDialog", "onClick: reply_no:" + reply_no);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reply_writer);
        setCancelable(true);
        setCanceledOnTouchOutside(true);


        postRereply = findViewById(R.id.dialogReplyWriter_postRereply);
        if(depth == 1) {
            postRereply.setVisibility(View.GONE);
        } else {
            postRereply.setOnClickListener(this);
        }

        deleteReply = findViewById(R.id.dialogReplyWriter_deleteReply);
        deleteReply.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.dialogReplyWriter_postRereply) {
            Intent WritingNestedReplyIntent = new Intent(clickedPostingActivity, WritingNestedReplyActivity.class);
            WritingNestedReplyIntent.putStringArrayListExtra("replyData", dataUsedInWritingNestedReplyActivity);
            WritingNestedReplyIntent.putExtra("writerNumber", writer_number);
            WritingNestedReplyIntent.putExtra("boardType",clickedPostingActivity.getBoardType());
            clickedPostingActivity.startActivityForResult(WritingNestedReplyIntent, PostingActivity.WritingNestedReplyActivityCode);
        } else if (v.getId() == R.id.dialogReplyWriter_deleteReply) {
                replyDialogInterface.onDeleteReplyDialog(depth, reply_no);
        }
        dismiss();
    }

}
