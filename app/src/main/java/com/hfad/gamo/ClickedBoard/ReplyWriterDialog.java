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

public class ReplyWriterDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView postRereply;
    private TextView deleteReply;
    private int depth;
    private ArrayList<String> dataUsedInWritingNestedReplyActivity = null;

    public ReplyWriterDialog(@NonNull Context context, int depth) {
        super(context);
        this.context = context;
        this.depth = depth;
    }

    public ReplyWriterDialog(@NonNull Context context, int depth, ArrayList<String> dataUsedInWritingNestedReplyActivity) {
        super(context);
        this.context = context;
        this.depth = depth;
        this.dataUsedInWritingNestedReplyActivity = dataUsedInWritingNestedReplyActivity;
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
        switch (v.getId()) {
            case R.id.dialogReplyWriter_postRereply:
                Intent nestedReplyIntent = new Intent(context, WritingNestedReplyActivity.class);
                nestedReplyIntent.putStringArrayListExtra("replyData", dataUsedInWritingNestedReplyActivity);
                context.startActivity(nestedReplyIntent);
                break;
            case R.id.dialogReplyWriter_deleteReply:
                break;
            default:
                break;
        }
        dismiss();
    }

}
