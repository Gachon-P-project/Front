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

public class ClickedBoard_RecyclerAdapter extends RecyclerView.Adapter<ClickedBoard_RecyclerAdapter.ViewHolder> {

    private JSONArray JSONArrayData = null;
    private String board_title;

    ClickedBoard_RecyclerAdapter(JSONArray list, String board_title) {
        this.JSONArrayData = list;
        this.board_title = board_title;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView board_title;
        TextView board_contents;
        TextView board_date;
        TextView board_view_cnt;
        TextView board_reply_cnt = null;
        View view;
        toClickedPosting toClickedPosting;

        ViewHolder(View itemView) {
            super(itemView);

            board_title = itemView.findViewById(R.id.board_title);
            board_contents = itemView.findViewById(R.id.board_contents);
            board_date = itemView.findViewById(R.id.board_date);
            board_view_cnt = itemView.findViewById(R.id.board_view_cnt);
            view = itemView;
        }
    }


    @NonNull
    @Override
    public ClickedBoard_RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.clicked_board_recycler_item, parent, false);
        ClickedBoard_RecyclerAdapter.ViewHolder vh = new ClickedBoard_RecyclerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ClickedBoard_RecyclerAdapter.ViewHolder holder, int position) {

        JSONObject data;

        try {
            data = JSONArrayData.getJSONObject(position);
            holder.board_title.setText(data.getString("post_title"));
            holder.board_contents.setText(data.getString("post_contents"));
            holder.board_date.setText(data.getString("wrt_date"));
            holder.board_view_cnt.setText(data.getString("view_cnt"));
            holder.toClickedPosting = new toClickedPosting(board_title, data.getString("post_no"), data.getString("post_like"),
                    data.getString("post_title"), data.getString("post_contents"), data.getString("wrt_date"),
                    data.getString("view_cnt"), data.getString("reply_yn"));
        } catch(JSONException e) {
            e.printStackTrace();
            Toast.makeText(holder.view.getContext(), "json Error", Toast.LENGTH_SHORT).show();
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ClickedPostingActivity.class);
                intent.putExtra("toClickedPosting", holder.toClickedPosting);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return JSONArrayData.length();
    }
}
