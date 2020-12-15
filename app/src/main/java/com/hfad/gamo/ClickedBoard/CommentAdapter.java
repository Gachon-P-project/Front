package com.hfad.gamo.ClickedBoard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.gamo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private JSONArray JSONArrayData = null;

    CommentAdapter(JSONArray list) {
        this.JSONArrayData = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView wrt_date;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.comment_content);
            wrt_date = itemView.findViewById(R.id.comment_time);

            view = itemView;
        }
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_message, parent, false);
        CommentAdapter.ViewHolder vh = new CommentAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, int position) {

        JSONObject data;

        try {
            data = JSONArrayData.getJSONObject(position);
            holder.content.setText(data.getString("reply_contents"));
            holder.wrt_date.setText(data.getString("wrt_date"));
        } catch(JSONException e) {
            e.printStackTrace();
            Toast.makeText(holder.view.getContext(), "json Error", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return JSONArrayData.length();
    }

}
