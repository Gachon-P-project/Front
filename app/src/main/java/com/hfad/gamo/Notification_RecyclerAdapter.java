package com.hfad.gamo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.gamo.ClickedBoard.ClickedPostingActivity;
import com.hfad.gamo.ClickedBoard.toClickedPosting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Notification_RecyclerAdapter extends RecyclerView.Adapter<Notification_RecyclerAdapter.ViewHolder> {

    private JSONArray JSONArrayData = null;
    private String dept;

    Notification_RecyclerAdapter(JSONArray list, String dept) {
        this.JSONArrayData = list;
        this.dept = dept;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView notification_title;
        TextView notification_date;
        TextView notification_view_cnt;
        ImageView notification_file;
        View view;
        CardView notification_card_view;

        ViewHolder(View itemView) {
            super(itemView);

            notification_title = itemView.findViewById(R.id.notification_title);
            notification_date = itemView.findViewById(R.id.notification_date);
            notification_view_cnt = itemView.findViewById(R.id.notification_view_cnt);
            notification_file = itemView.findViewById(R.id.notification_file);
            notification_card_view = itemView.findViewById(R.id.notification_card_view);
            view = itemView;
        }
    }


    @NonNull
    @Override
    public Notification_RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.notification_recycler_item, parent, false);
        Notification_RecyclerAdapter.ViewHolder vh = new Notification_RecyclerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final Notification_RecyclerAdapter.ViewHolder holder, int position) {

        JSONObject data;
        String title = null;
        String date = null;
        String view = null;
        String board_no = null;
        int num = -1;
        int file = -1;

        try {
            data = JSONArrayData.getJSONObject(position);
            title = data.getString("title");
            date = data.getString("date");
            view = data.getString("view");
            board_no = data.getString("board_no");
            num = data.getInt("num");
            file = data.getInt("file");

            final String board_noForIntent = board_no;

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), NotificationWebViewActivity.class);
                    intent.putExtra("dept", dept);
                    intent.putExtra("board_no", board_noForIntent);
                    v.getContext().startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.notification_title.setText(title);
        holder.notification_date.setText(date);
        holder.notification_view_cnt.setText(view);
        //holder.notification_file;

        if(file == 0)
        holder.notification_file.setVisibility(View.INVISIBLE);

        if(num == 0) {
            holder.notification_card_view.setCardBackgroundColor(holder.view.getContext().getResources().getColor(R.color.indigo200, null));
        }
    }

    @Override
    public int getItemCount() {
        return JSONArrayData.length();
    }
}
