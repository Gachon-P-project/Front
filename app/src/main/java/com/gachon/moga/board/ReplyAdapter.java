package com.gachon.moga.board;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.moga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static com.gachon.moga.Component.sharedPreferences;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private static final String TAG = "ReplyAdapter";
    private JSONArray JSONArrayData = null;
    private PostingActivity clickedPostingActivity = null;
    private HashMap<Integer, JSONArray> toWritingNestedReplyActivity = null;
    private float density;
    private DisplayMetrics displayMetrics = null;
    private final String TRUE = "1";
    private final String FALSE = "0";
    private String usingLocation = null;
    private JSONObject data = null;
    private int depth = 0;
    private String reply_contents = null;
    private String wrt_date = null;
    private String reply_user_no = null;
    private String user_no = sharedPreferences.getString("number", null);
    private String is_deleted = null;
    private String writer_number = null;
    private int reply_no = -1;
    private int post_no = -1;
    private int bundle_id = -1;
    private String nickname = null;

    ReplyAdapter(JSONArray list, Activity activity, String usingLocation) {
        this.JSONArrayData = list;
        this.usingLocation = usingLocation;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        density = activity.getResources().getDisplayMetrics().density;

        displayMetrics = activity.getApplicationContext().getResources().getDisplayMetrics();

        if(usingLocation.equals("PostingActivity")) {
            this.toWritingNestedReplyActivity = new HashMap<>();
            this.clickedPostingActivity = (PostingActivity) activity;
            this.writer_number = clickedPostingActivity.getWriter_number();
        } else {
            this.writer_number = ((WritingNestedReplyActivity) activity).getWriter_number();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_replies_nickname;
        TextView item_replies_content;
        TextView item_replies_wrt_date;
        LinearLayout item_replies_reply_layout;
        ImageView item_replies_three_dots;
        ImageView item_replies_user_img;
        ImageView item_replies_is_writer;

        View view;

        int bundle_id;
        JSONObject replyForData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_replies_nickname = itemView.findViewById(R.id.item_replies_nickname);
            item_replies_content = itemView.findViewById(R.id.item_replies_content);
            item_replies_wrt_date = itemView.findViewById(R.id.item_replies_wrt_date);
            item_replies_reply_layout = itemView.findViewById(R.id.item_replies_reply_layout);
            item_replies_three_dots = itemView.findViewById(R.id.item_replies_three_dots);
            item_replies_user_img = itemView.findViewById(R.id.item_replies_user_img);
            item_replies_is_writer = itemView.findViewById(R.id.imgReplyIsWriter);

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

        Log.i("recycler!!!" , "onBindViewHolder");

        setViewInitialValue(holder);

        try {
            holder.replyForData = JSONArrayData.getJSONObject(position);
            initData(holder.replyForData);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        if(usingLocation.equals("PostingActivity")) {
            saveDataForWritingNestedReplyActivity(holder.replyForData, depth, reply_no, bundle_id);
        }

        if(is_deleted.equals(TRUE)) {
            setViewOfDeletedReply(holder);
            return;
        }

        try {
            setViewOfUnDeletedReply(holder, usingLocation);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return JSONArrayData.length();
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

    private int getPixel(int dp){
        return (int)(dp * density);
    }

    private void saveDataForWritingNestedReplyActivity(JSONObject reply_data, int depth,int reply_no, int bundle_id) {
        try {
            if (depth == 0) {
                JSONArray data = new JSONArray();
                data.put(reply_data);
                toWritingNestedReplyActivity.put(reply_no, data);
            } else {

                JSONArray data = toWritingNestedReplyActivity.get(bundle_id);
                data.put(reply_data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> transformData(JSONArray jsonArray) throws JSONException {

        ArrayList<String> arrayList = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            arrayList.add(jsonArray.getJSONObject(i).toString());
        }

        return arrayList;
    }


    private void initData(JSONObject data) throws JSONException{
        reply_contents = data.getString("reply_contents");
        wrt_date = processServerDateToAndroidDate(data.getString("wrt_date"));
        depth = data.getInt("depth");
        reply_user_no = data.getString("user_no");
        is_deleted = data.getString("is_deleted");
        reply_no = data.getInt("reply_no");
        post_no = data.getInt("post_no");
        bundle_id = data.getInt("bundle_id");
        nickname = data.getString("nickname");
    }

    private void setViewOfDeletedReply(ReplyAdapter.ViewHolder holder) {
        holder.item_replies_user_img.setVisibility(View.GONE);
        holder.item_replies_wrt_date.setVisibility(View.GONE);
        holder.item_replies_three_dots.setVisibility(View.GONE);
        holder.item_replies_nickname.setVisibility(View.GONE);
        holder.item_replies_is_writer.setVisibility(View.GONE);
        holder.item_replies_content.setText("삭제된 댓글입니다.");
    }

    private void setViewOfUnDeletedReply(final ReplyAdapter.ViewHolder holder, String usingLocation) throws JSONException {
        holder.item_replies_content.setText(reply_contents);
        holder.item_replies_wrt_date.setText(wrt_date);
        holder.item_replies_nickname.setText(nickname);

        if(writer_number.equals(reply_user_no))
            holder.item_replies_is_writer.setVisibility(View.VISIBLE);
        else
            holder.item_replies_is_writer.setVisibility(View.GONE);


        final int bundle_id = holder.replyForData.getInt("bundle_id");
        final int reply_no = holder.replyForData.getInt("reply_no");

        if(usingLocation.equals("PostingActivity")) {
            if(depth == 1) {
                holder.item_replies_reply_layout.setPadding(getPixel(35), getPixel(10),getPixel(5), getPixel(10));

                assert reply_user_no != null;
                if(reply_user_no.equals(user_no)) {
                    holder.item_replies_three_dots.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ReplyWriterDialog replyWriterDialog = new ReplyWriterDialog(v.getContext(), clickedPostingActivity, holder.replyForData, writer_number);
                            replyWriterDialog.show();
                            WindowManager.LayoutParams params = replyWriterDialog.getWindow().getAttributes();
                            params.width = (int) (displayMetrics.widthPixels * 0.8);
                            params.height = (int) (WindowManager.LayoutParams.WRAP_CONTENT * 1.1);
                            replyWriterDialog.getWindow().setAttributes(params);

                        }
                    });
                } else {
                    holder.item_replies_three_dots.setVisibility(View.GONE);
                }
            } else {
                assert reply_user_no != null;
                if(reply_user_no.equals(user_no)) {
                    holder.item_replies_three_dots.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> dataUsedInWritingNestedReplyActivity = null;
                            try {
                                dataUsedInWritingNestedReplyActivity = transformData(toWritingNestedReplyActivity.get(reply_no));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ReplyWriterDialog replyWriterDialog = new ReplyWriterDialog(v.getContext(),clickedPostingActivity, dataUsedInWritingNestedReplyActivity, holder.replyForData, writer_number);
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
                            ArrayList<String> dataUsedInWritingNestedReplyActivity = null;
                            try {
                                dataUsedInWritingNestedReplyActivity = transformData(toWritingNestedReplyActivity.get(reply_no));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ReplyDialog replyDialog = new ReplyDialog(v.getContext(), 0, dataUsedInWritingNestedReplyActivity, clickedPostingActivity, writer_number);
                            replyDialog.show();
                            WindowManager.LayoutParams params = replyDialog.getWindow().getAttributes();
                            params.width = (int) (displayMetrics.widthPixels * 0.8);
                            params.height = (int) (WindowManager.LayoutParams.WRAP_CONTENT * 1.1);
                            replyDialog.getWindow().setAttributes(params);
                        }
                    });
                }
            }
        } else {
            if(depth == 1) {
                holder.item_replies_reply_layout.setPadding(getPixel(35), getPixel(10),0, getPixel(10));
            }
            holder.item_replies_three_dots.setVisibility(View.INVISIBLE);
        }
    }

    private void setViewInitialValue(ReplyAdapter.ViewHolder holder) {
        holder.item_replies_reply_layout.setPadding(getPixel(5), getPixel(10),getPixel(5), getPixel(10));
        holder.item_replies_user_img.setVisibility(View.VISIBLE);
        holder.item_replies_wrt_date.setVisibility(View.VISIBLE);
        holder.item_replies_three_dots.setVisibility(View.VISIBLE);
        holder.item_replies_nickname.setVisibility(View.VISIBLE);
        holder.item_replies_is_writer.setVisibility(View.VISIBLE);
    }

}
