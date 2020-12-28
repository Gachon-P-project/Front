package com.hfad.gamo.ClickedBoard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ClickedBoard_RecyclerAdapter extends RecyclerView.Adapter<ClickedBoard_RecyclerAdapter.ViewHolder> {

    private JSONArray JSONArrayData = null;
    private final String board_title;
    private Date date;
    private final DateFormat dateFormat;

    ClickedBoard_RecyclerAdapter(JSONArray list, String board_title) {
        this.JSONArrayData = list;
        this.board_title = board_title;

        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(timeZone);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView board_title;
        TextView board_contents;
        TextView board_date;
        TextView board_reply_cnt;
        View view;
        toClickedPosting toClickedPosting;

        ViewHolder(View itemView) {
            super(itemView);

            board_title = itemView.findViewById(R.id.board_title);
            board_contents = itemView.findViewById(R.id.board_contents);
            board_date = itemView.findViewById(R.id.board_date);
            board_reply_cnt = itemView.findViewById(R.id.board_reply_cnt);
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
        String wrt_date = null;
        long time;

        Calendar beforeOneHour = Calendar.getInstance();
        beforeOneHour.add(Calendar.HOUR, -1);

        Calendar beforeOneDay = Calendar.getInstance();
        beforeOneDay.add(Calendar.DATE, -1);

        try {
            data = JSONArrayData.getJSONObject(position);
            holder.board_title.setText(data.getString("post_title"));
            holder.board_contents.setText(data.getString("post_contents"));
            wrt_date = data.getString("wrt_date");
            //holder.board_date.setText(data.getString("wrt_date"));
            holder.board_reply_cnt.setText(data.getString("reply_cnt"));
            holder.toClickedPosting = new toClickedPosting(board_title, data.getString("post_no"), data.getString("post_like"),
                    data.getString("post_title"), data.getString("post_contents"), wrt_date,
                    data.getString("reply_cnt"), data.getString("reply_yn"));
        } catch(JSONException e) {
            e.printStackTrace();
            Toast.makeText(holder.view.getContext(), "json Error", Toast.LENGTH_SHORT).show();
        }

        DateFormat dataFormatForFinalDate = new SimpleDateFormat("yy.MM.dd", java.util.Locale.getDefault());
        try {
            assert wrt_date != null;
            date = dateFormat.parse(wrt_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String final_wrt_date = null;
        assert date != null;
        if(date.after(beforeOneHour.getTime())) {
            time =  date.getTime() - beforeOneHour.getTimeInMillis();
            if(time > (59 * 60000) && time <= (60 * 600000)) {
                final_wrt_date = "방금 전";
                holder.board_date.setText(final_wrt_date);
            } else {
                long minutesAgo = (60*60*1000 - time) / (60*1000);
                final_wrt_date = String.valueOf((minutesAgo)).concat("분 전");
                holder.board_date.setText(final_wrt_date);
                Log.i("time", String.valueOf(minutesAgo));
            }
        } else if(date.after(beforeOneDay.getTime())) {
            time =  date.getTime() - beforeOneDay.getTimeInMillis();
            long timesAgo = (24*60*60*1000 - time) / (60*60*1000);
            final_wrt_date = String.valueOf(timesAgo).concat("시간 전");
            holder.board_date.setText(final_wrt_date);
            Log.i("time", String.valueOf(timesAgo));
        } else {
            final_wrt_date = dataFormatForFinalDate.format(date);
            holder.board_date.setText(final_wrt_date);
            Log.i("time", String.valueOf(final_wrt_date));
        }

        holder.toClickedPosting.setWrt_date(final_wrt_date);

        Log.i("date", dateFormat.format(date));



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
