package com.gachon.moga.board;

import android.os.Parcel;
import android.os.Parcelable;

public class toClickedPosting implements Parcelable {

    private String board_title;
    private String post_no;
    private String post_title;
    private String post_contents;
    private String wrt_date;
    private String reply_cnt;
    private String reply_yn;
    private String user_no;
    private String like_cnt;
    private String like_user;




    public toClickedPosting(String board_title, String post_no, String post_title, String post_contents,
                            String wrt_date, String reply_cnt, String reply_yn, String user_no,
                            String like_cnt, String like_user) {
        this.board_title = board_title;
        this.post_no = post_no;
        this.post_title = post_title;
        this.post_contents = post_contents;
        this.wrt_date = wrt_date;
        this.reply_cnt = reply_cnt;
        this.reply_yn = reply_yn;
        this.user_no = user_no;
        this.like_cnt = like_cnt;
        this.like_user = like_user;
    }

    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }

    public void setPost_no(String post_no) {
        this.post_no = post_no;
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

    public void setReply_cnt(String reply_cnt) {
        this.reply_cnt = reply_cnt;
    }

    public void setReply_yn(String reply_yn) {
        this.reply_yn = reply_yn;
    }

    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }

    public void setLike_cnt(String like_cnt) {
        this.like_cnt = like_cnt;
    }

    public void setLike_user(String like_user) {
        this.like_user = like_user;
    }


    public String getBoard_title() {
        return board_title;
    }

    public String getPost_no() {
        return post_no;
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

    public String getReply_cnt() {
        return reply_cnt;
    }

    public String getReply_yn() {
        return reply_yn;
    }

    public String getUser_no() {
        return user_no;
    }

    public String getLike_cnt() {
        return like_cnt;
    }

    public String getLike_user() {
        return like_user;
    }


    protected toClickedPosting(Parcel in) {
        this.board_title = in.readString();
        this.post_no = in.readString();
        this.post_title = in.readString();
        this.post_contents = in.readString();
        this.wrt_date = in.readString();
        this.reply_cnt = in.readString();
        this.reply_yn = in.readString();
        this.user_no = in.readString();
        this.like_cnt = in.readString();
        this.like_user = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(board_title);
        dest.writeString(post_no);
        dest.writeString(post_title);
        dest.writeString(post_contents);
        dest.writeString(wrt_date);
        dest.writeString(reply_cnt);
        dest.writeString(reply_yn);
        dest.writeString(user_no);
        dest.writeString(like_cnt);
        dest.writeString(like_user);
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
