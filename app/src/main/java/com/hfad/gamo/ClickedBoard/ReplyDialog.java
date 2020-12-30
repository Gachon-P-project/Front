package com.hfad.gamo.ClickedBoard;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hfad.gamo.R;

public class ReplyDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView postRereply;

    public ReplyDialog(@NonNull Context context) {
        super(context);

        this.context = context;

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
                Intent rereplyIntent = new Intent(context, RereplyActivity.class);
                context.startActivity(rereplyIntent);
                break;
            default:
                break;
        }
        dismiss();
    }

}
