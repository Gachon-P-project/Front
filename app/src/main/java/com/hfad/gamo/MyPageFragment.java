package com.hfad.gamo;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import static android.content.Context.MODE_PRIVATE;
import static com.hfad.gamo.DataIOKt.appConstantPreferences;
import static com.hfad.gamo.DataIOKt.resetSharedPreference;

public class MyPageFragment extends Fragment {
    private SharedPreferences prefs;

    private NickNameDialog nickNameDialog;
    private LinearLayout llChangeNickname, llNotificationSettings, llAppInfo, llLogout;
    private TextView tvUsername, tvNickname, tvMajor, tvStudentId;
    private ImageView imgMyPhoto;
    private String username, major, nickname, studnetId, myPhotoUrl;

    public MyPageFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        prefs = getActivity().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        username = prefs.getString("name", null);
        major = prefs.getString("department", null);
        nickname = prefs.getString("nickname", "-");
        studnetId = prefs.getString("number", null);
        myPhotoUrl = prefs.getString("image", null);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        llChangeNickname = view.findViewById(R.id.change_nickname);
        llNotificationSettings = view.findViewById(R.id.notification_settings);
        llAppInfo = view.findViewById(R.id.information_use);
        llLogout = view.findViewById(R.id.logout);
        llChangeNickname.setClickable(true);
        llNotificationSettings.setClickable(true);
        llAppInfo.setClickable(true);
        llLogout.setClickable(true);
        tvUsername = view.findViewById(R.id.user_name);
        tvMajor = view.findViewById(R.id.user_major);
        tvStudentId = view.findViewById(R.id.user_no);
        tvNickname = view.findViewById(R.id.tv_mypage_nickname);
        imgMyPhoto = view.findViewById(R.id.iv_mypage_photo);

        nickname = prefs.getString("nickname", "-");
        tvUsername.setText(username);
        tvMajor.setText(major);
        tvStudentId.setText(studnetId);
        tvNickname.setText(nickname);


//        getMyPhoto getMyPhoto = new getMyPhoto();
//        getMyPhoto.execute();

        llChangeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "닉네임 변경", Toast.LENGTH_SHORT).show();

                DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
                int deviceWidth = dm.widthPixels;

                nickNameDialog = new NickNameDialog(getActivity());
                nickNameDialog.initDialog(deviceWidth);
                nickNameDialog.updateNickname();
            }
        });

        llNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "알림 설정", Toast.LENGTH_SHORT).show();
            }
        });

        llAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InformationActivity.class);
                startActivity(intent);
            }
        });

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resetSharedPreference();
                startActivity(intent);
            }
        });




        return view;
    }


    public void onRefresh() {
        ((MainActivity)getActivity()).refreshFragment();
    }



    private class getMyPhoto extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(myPhotoUrl);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgMyPhoto.setImageBitmap(bitmap);
            imgMyPhoto.invalidate();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }



}