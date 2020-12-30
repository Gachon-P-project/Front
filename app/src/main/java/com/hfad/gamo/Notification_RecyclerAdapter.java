package com.hfad.gamo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Notification_RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private final String TAG = "Notice_Adapter";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private JSONArray dataArray;

    private TextView tvTitle, tvContent, tvTime;
    private ImageView imgIcon;
    private androidx.cardview.widget.CardView cardView;

    private OnReadSetBadge onReadSetBadge;
    private int unread = DataIOKt.getUnread();


    public Notification_RecyclerAdapter(JSONArray dataArray) {
        super();
        this.dataArray = dataArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);

        tvTitle = view.findViewById(R.id.tvNotificationTitle);
        tvContent = view.findViewById(R.id.tvNotificationContent);
        tvTime = view.findViewById(R.id.tvNotificationTime);
        imgIcon = view.findViewById(R.id.imgNotificationIcon);
        cardView = view.findViewById(R.id.cardviewNotification);

        RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(view) {
            @Override
            public String toString() {
                return super.toString();
            }
        };

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject data;
        final String title, content, type, board_no;
        Date date = null;
        boolean isRead;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(timeZone);

        try {
            data = dataArray.getJSONObject(position);
            title = data.getString("title");
            content = data.getString("content");
//            type = data.getString("type");
//            board_no = data.getString("baord_no");
            date = dateFormat.parse(data.getString("time"));
            String sTime = new SimpleDateFormat("MM-dd HH:mm").format(date);
            isRead = data.getBoolean("isRead");
            tvTitle.setText(title);
            tvContent.setText(content);
            tvTime.setText(sTime);
//            switch (type) {
//                case "notice_new" :
//                    break;
//                case "board_imply" :
//                    break;
//                case "board_reply" :
//                    break;
//                case "board_like" :
//                    break;
//                case "board_newPost_bookmark" :
//                    break;
//                default:
//                    imgIcon.setImageResource(R.drawable.ic_pencil);
//                    break;
//            }
            if(!isRead) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.jinColor));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
//                    switch (type) {
//                        case "notice_new" :
//                            break;
//                        case "board_imply" :
//                            break;
//                        case "board_reply" :
//                            break;
//                        case "board_like" :
//                            break;
//                        case "board_newPost_bookmark" :
//                            break;
//                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if(dataArray == null)
            return 0;
        else
            return dataArray.length();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();

    }


    private class viewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvContent, tvTime;
        public ImageView imgIcon;
        public androidx.cardview.widget.CardView cardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvContent = itemView.findViewById(R.id.tvNotificationContent);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            imgIcon = itemView.findViewById(R.id.imgNotificationIcon);
            cardView = itemView.findViewById(R.id.cardviewNotification);
        }
    }

    public boolean setRead(int position, boolean isRead ) {
        try {
            JSONObject obj = dataArray.getJSONObject(position);
            obj.put("isRead", isRead);
//            obj.a("unread", isRead);
            dataArray.put(position, obj);
            this.notifyDataSetChanged();
            unread = isRead ? (unread-1) : (unread+1);

            onReadSetBadge.setBadge(unread);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface OnReadSetBadge {
        void setBadge(int count);
    }

    public void setOnReadSetBadge(OnReadSetBadge mOnReadSetBadge){
        this.onReadSetBadge = mOnReadSetBadge;
    }





}
