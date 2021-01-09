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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ClickedBoard_RecyclerAdapter extends RecyclerView.Adapter<ClickedBoard_RecyclerAdapter.ViewHolder> {

    private JSONArray JSONArrayData = null;
    private final String board_title;
    private final int boardType;


    ClickedBoard_RecyclerAdapter(JSONArray list, String board_title, int boardType) {
        this.JSONArrayData = list;
        this.board_title = board_title;
        this.boardType = boardType;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView board_title;
        TextView board_contents;
        TextView board_date;
        TextView board_post_like;
        TextView board_reply_cnt;
        View view;
        toClickedPosting toClickedPosting;
        JSONObject forUpdatePosting;



        ViewHolder(View itemView) {
            super(itemView);

            board_title = itemView.findViewById(R.id.board_title);
            board_contents = itemView.findViewById(R.id.board_contents);
            board_date = itemView.findViewById(R.id.board_date);
            board_post_like = itemView.findViewById(R.id.clicked_board_recycler_item_post_like_text);
            board_reply_cnt = itemView.findViewById(R.id.clicked_board_recycler_item_reply_cnt);
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

        JSONObject data = null;
        String wrt_date = null;

        try {
            data = JSONArrayData.getJSONObject(position);
            holder.forUpdatePosting = JSONArrayData.getJSONObject(position);
            holder.board_title.setText(data.getString("post_title"));
            holder.board_contents.setText(data.getString("post_contents"));
            wrt_date = data.getString("wrt_date");
            holder.board_reply_cnt.setText(data.getString("reply_cnt"));
            holder.board_post_like.setText(data.getString("like_cnt"));
        } catch(JSONException e) {
            e.printStackTrace();
            Toast.makeText(holder.view.getContext(), "json Error", Toast.LENGTH_SHORT).show();
        }

        String androidDate = processServerDateToAndroidDate(wrt_date);

        holder.board_date.setText(androidDate);


        try {
            assert data != null;
            holder.toClickedPosting = new toClickedPosting(board_title, data.getString("post_no"),
                    data.getString("post_title"), data.getString("post_contents"), androidDate,
                    data.getString("reply_cnt"), data.getString("reply_yn"), data.getString("user_no"),
                    data.getString("like_cnt"), data.getString("like_user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ClickedPostingActivity.class);
                intent.putExtra("toClickedPosting", holder.toClickedPosting);
                intent.putExtra("forUpdatePosting", holder.forUpdatePosting.toString());
                intent.putExtra("boardType", boardType);
                v.getContext().startActivity(intent);
            }
        });
    }

    private String processServerDateToAndroidDate(String serverDate) {
        long time;
        Date date = null;

        DateFormat dateFormat_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        DateFormat dateFormat_2 = new SimpleDateFormat("yyyy.MM.dd. a hh:mm:ss", Locale.KOREA);
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat_1.setTimeZone(timeZone);
        dateFormat_2.setTimeZone(timeZone);

        Calendar beforeOneHour = Calendar.getInstance();
        beforeOneHour.add(Calendar.HOUR, -1);

        Calendar beforeOneDay = Calendar.getInstance();
        beforeOneDay.add(Calendar.DATE, -1);

        DateFormat dataFormatForFinalDate = new SimpleDateFormat("yy.MM.dd", java.util.Locale.getDefault());
        try {
            assert serverDate != null;
            date = dateFormat_1.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (date == null) {
                    assert serverDate != null;
                    date = dateFormat_2.parse(serverDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String AndroidDate;
        assert date != null;
        if(date.after(beforeOneHour.getTime())) {
            time =  date.getTime() - beforeOneHour.getTimeInMillis();
            if(time > (59 * 60000) && time <= (60 * 600000)) {
                AndroidDate = "방금 전";
            } else {
                long minutesAgo = (60*60*1000 - time) / (60*1000);
                AndroidDate = String.valueOf((minutesAgo)).concat("분 전");
            }
        } else if(date.after(beforeOneDay.getTime())) {
            time =  date.getTime() - beforeOneDay.getTimeInMillis();
            long timesAgo = (24*60*60*1000 - time) / (60*60*1000);
            AndroidDate = String.valueOf(timesAgo).concat("시간 전");
        } else {
            AndroidDate = dataFormatForFinalDate.format(date);
        }

        return AndroidDate;
    }

    private ArrayList<String> transformData(JSONArray jsonArray) throws JSONException {

        ArrayList<String> arrayList = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            arrayList.add(jsonArray.getJSONObject(i).toString());
        }

        return arrayList;
    }

    @Override
    public int getItemCount() {
        return JSONArrayData.length();
    }
}
