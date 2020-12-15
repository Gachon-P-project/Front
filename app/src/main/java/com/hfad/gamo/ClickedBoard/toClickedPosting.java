package com.hfad.gamo.ClickedBoard;

import android.os.Parcel;
import android.os.Parcelable;

public class toClickedPosting implements Parcelable {

    private String board_title;
    private String post_no;
    private String post_like;
    private String post_title;
    private String post_contents;
    private String wrt_date;
    private String view_cnt;
    private String reply_cnt;
    private String reply_yn;

    public toClickedPosting(String board_title, String post_no, String post_like, String post_title, String post_contents,
                            String wrt_date, String view_cnt, String reply_yn) {
        this.board_title = board_title;
        this.post_no = post_no;
        this.post_like = post_like;
        this.post_title = post_title;
        this.post_contents = post_contents;
        this.wrt_date = wrt_date;
        this.view_cnt = view_cnt;
        // this.reply_cnt = reply_cnt;
        this.reply_yn = reply_yn;
    }

    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }

    public void setPost_no(String post_no) {
        this.post_no = post_no;
    }

    public void setPost_like(String post_like) {
        this.post_like = post_like;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public void setPost_contents(String post_contents) {
        this.post_contents = post_contents;
    }

    public void setWrt_date(String wrt_date) {
        this.wrt_date = wrt_date;
    }

    public void setView_cnt(String view_cnt) {
        this.view_cnt = view_cnt;
    }

    public void setReply_cnt(String reply_cnt) {
        this.reply_cnt = reply_cnt;
    }

    public void setReply_yn(String reply_yn) {
        this.reply_yn = reply_yn;
    }

    public String getBoard_title() {
        return board_title;
    }

    public String getPost_no() {
        return post_no;
    }

    public String getPost_like() {
        return post_like;
    }

    public String getPost_title() {
        return post_title;
    }

    public String getPost_contents() {
        return post_contents;
    }

    public String getWrt_date() {
        return wrt_date;
    }

    public String getView_cnt() {
        return view_cnt;
    }

    public String getReply_cnt() {
        return reply_cnt;
    }

    public String getReply_yn() {
        return reply_yn;
    }


    protected toClickedPosting(Parcel in) {
        this.board_title = in.readString();
        this.post_no = in.readString();
        this.post_like = in.readString();
        this.post_title = in.readString();
        this.post_contents = in.readString();
        this.wrt_date = in.readString();
        this.view_cnt = in.readString();
        // this.reply_cnt = in.readString();
        this.reply_yn = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(board_title);
        dest.writeString(post_no);
        dest.writeString(post_like);
        dest.writeString(post_title);
        dest.writeString(post_contents);
        dest.writeString(wrt_date);
        dest.writeString(view_cnt);
        // dest.writeString(reply_cnt);
        dest.writeString(reply_yn);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<toClickedPosting> CREATOR = new Creator<toClickedPosting>() {
        @Override
        public toClickedPosting createFromParcel(Parcel in) {
            return new toClickedPosting(in);
        }

        @Override
        public toClickedPosting[] newArray(int size) {
            return null;
        }
    };
}
