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

public class ReplyDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView postRereply;
    private int reply_no;
    private int post_no;

    public ReplyDialog(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    public ReplyDialog(@NonNull Context context, int reply_no, int post_no) {
        super(context);

        this.context = context;
        this.reply_no = reply_no;
        this.post_no = post_no;
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
                Intent rereplyIntent = new Intent(context, NestedReplyActivity.class);
                context.startActivity(rereplyIntent);
                break;
            default:
                break;
        }
        dismiss();
    }

}
