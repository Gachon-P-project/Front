package com.hfad.gamo.ClickedBoard;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static com.hfad.gamo.Component.sharedPreferences;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private JSONArray JSONArrayData = null;
    private float density;
    DisplayMetrics displayMetrics = null;


    ReplyAdapter(JSONArray list, Activity activity) {
        this.JSONArrayData = list;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        density = activity.getResources().getDisplayMetrics().density;

        displayMetrics = activity.getApplicationContext().getResources().getDisplayMetrics();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView wrt_date;
        LinearLayout linearLayout;
        ImageView item_replies_three_dots;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.item_replies_content);
            wrt_date = itemView.findViewById(R.id.item_replies_wrt_date);
            linearLayout = itemView.findViewById(R.id.item_replies_reply_layout);
            item_replies_three_dots = itemView.findViewById(R.id.item_replies_three_dots);

            view = itemView;
        }
    }

    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_replies, parent, false);
        ReplyAdapter.ViewHolder vh = new ReplyAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReplyAdapter.ViewHolder holder, int position) {

        JSONObject data;
        int depth = 0;
        String reply_contents = null;
        String wrt_date = null;
        String reply_user_no = null;
        String user_no = sharedPreferences.getString("number", null);

        try {
            data = JSONArrayData.getJSONObject(position);
            reply_contents = data.getString("reply_contents");
            wrt_date = processServerDateToAndroidDate(data.getString("wrt_date"));
            depth = data.getInt("depth");
            reply_user_no = data.getString("user_no");
        } catch(JSONException e) {
            e.printStackTrace();
            Toast.makeText(holder.view.getContext(), "json Error", Toast.LENGTH_SHORT).show();
        }

        holder.content.setText(reply_contents);
        holder.wrt_date.setText(wrt_date);

        if(depth == 1) {
            holder.linearLayout.setPadding(getPixel(35), getPixel(10),0, getPixel(10));
        } else {

        }

        assert reply_user_no != null;
        if(reply_user_no.equals(user_no)) {
            holder.item_replies_three_dots.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ReplyWriterDialog replyWriterDialog = new ReplyWriterDialog(v.getContext());
                    replyWriterDialog.show();
                    WindowManager.LayoutParams params = replyWriterDialog.getWindow().getAttributes();
                    params.width = (int) (displayMetrics.widthPixels * 0.8);
                    params.height = (int) (WindowManager.LayoutParams.WRAP_CONTENT * 1.1);
                    replyWriterDialog.getWindow().setAttributes(params);

                }
            });
        } else {
            holder.item_replies_three_dots.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ReplyDialog replyDialog = new ReplyDialog(v.getContext());
                    replyDialog.show();
                    WindowManager.LayoutParams params = replyDialog.getWindow().getAttributes();
                    params.width = (int) (displayMetrics.widthPixels * 0.8);
                    params.height = (int) (WindowManager.LayoutParams.WRAP_CONTENT * 1.1);
                    replyDialog.getWindow().setAttributes(params);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return JSONArrayData.length();
    }


    private String processServerDateToAndroidDate(String serverDate) {
        long time;
        Date date = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(timeZone);

        Calendar beforeOneHour = Calendar.getInstance();
        beforeOneHour.add(Calendar.HOUR, -1);

        Calendar beforeOneDay = Calendar.getInstance();
        beforeOneDay.add(Calendar.DATE, -1);

        DateFormat dataFormatForFinalDate = new SimpleDateFormat("yy.MM.dd", java.util.Locale.getDefault());
        try {
            assert serverDate != null;
            date = dateFormat.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
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

    private int getPixel(int dp){
        return (int)(dp * density);
    }

}
