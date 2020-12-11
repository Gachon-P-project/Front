package com.hfad.gamo.ClickedBoard;

import android.content.Context;
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

    private JSONArray JSONArrayData = null; // branch merge test

    ClickedBoard_RecyclerAdapter(JSONArray list) {
        this.JSONArrayData = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView board_title;
        TextView board_contents;
        TextView board_date;
        TextView board_view_cnt;
        TextView board_reply_cnt = null;
        View view = itemView;

        ViewHolder(View itemView) {
            super(itemView);

            board_title = itemView.findViewById(R.id.board_title);
            board_contents = itemView.findViewById(R.id.board_contents);
            board_date = itemView.findViewById(R.id.board_date);
            board_view_cnt = itemView.findViewById(R.id.board_view_cnt);
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
    public void onBindViewHolder(@NonNull ClickedBoard_RecyclerAdapter.ViewHolder holder, int position) {

        JSONObject data;

        try {
            data = JSONArrayData.getJSONObject(position);
            holder.board_title.setText(data.getString("post_title"));
            holder.board_contents.setText(data.getString("post_contents"));
            holder.board_date.setText(data.getString("wrt_date"));
            holder.board_view_cnt.setText(data.getString("view_cnt"));
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
