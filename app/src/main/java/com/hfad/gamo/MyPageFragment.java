package com.hfad.gamo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
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
    private NotificationSettingDialog notificationSettingDialog;
//    private LinearLayout llAppInfo;
    private TextView tvUsername, tvNickname, tvMajor, tvStudentId, tvChangeNickname, tvLogout, llNotificationSettings, tvAppVersion, tvInquiry, tvServiceInfo, tvOpenSource;
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

        tvChangeNickname = view.findViewById(R.id.change_nickname);
        llNotificationSettings = view.findViewById(R.id.notification_settings);
//        llAppInfo = view.findViewById(R.id.information_use);
        tvLogout = view.findViewById(R.id.logout);
        tvChangeNickname.setClickable(true);
        llNotificationSettings.setClickable(true);
//        llAppInfo.setClickable(true);
        tvLogout.setClickable(true);
        tvUsername = view.findViewById(R.id.user_name);
        tvMajor = view.findViewById(R.id.user_major);
        tvStudentId = view.findViewById(R.id.user_no);
        tvNickname = view.findViewById(R.id.tv_mypage_nickname);

        //
        tvAppVersion = view.findViewById(R.id.app_version);
        tvInquiry = view.findViewById(R.id.inquiry);
        tvServiceInfo = view.findViewById(R.id.service_info);
        tvOpenSource = view.findViewById(R.id.open_source);

        imgMyPhoto = view.findViewById(R.id.iv_mypage_photo);

        nickname = prefs.getString("nickname", "-");
        tvUsername.setText(username);
        tvMajor.setText(major);
        tvStudentId.setText(studnetId);
        tvNickname.setText(nickname);

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        final int deviceWidth = dm.widthPixels;

        tvChangeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickNameDialog = new NickNameDialog(getActivity());
                nickNameDialog.initDialog(deviceWidth);
                nickNameDialog.updateNickname();
            }
        });

        llNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationSettingDialog = new NotificationSettingDialog(getActivity(), deviceWidth);
                notificationSettingDialog.show();
            }
        });

//        llAppInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), InformationActivity.class);
//                startActivity(intent);
//            }
//        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resetSharedPreference();
                startActivity(intent);
            }
        });

        tvAppVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity(), deviceWidth);
                dialog.setTvInfo(getString(R.string.version));
                dialog.show();

            }
        });

        tvInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity(), deviceWidth);
                dialog.setTvInfo(getString(R.string.inquiry));
                dialog.show();
            }
        });

        tvServiceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity(), deviceWidth);
                dialog.setTvInfo("서비스 이용약관");
                dialog.show();
            }
        });

        tvOpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity(), deviceWidth);
                dialog.setTvInfo("오픈소스 라이선스");
                dialog.show();
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

    private class InfoDialog extends Dialog {

        TextView tvInfo;
        Context context;
        Button btnDialogPositive;

        public InfoDialog(@NonNull Context context, int deviceWidth) {
            super(context);
            this.context = context;
            setContentView(R.layout.dialog_info);
            tvInfo = findViewById(R.id.tvDialogInfo);
            btnDialogPositive = findViewById(R.id.btnDialogPositive);


            btnDialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            WindowManager.LayoutParams params = this.getWindow().getAttributes();
            params.width = (int) (deviceWidth * 0.95);
            this.onWindowAttributesChanged(params);
        }
        @Override
        public void create() {
            super.create();
        }

        public void setTvInfo(String s) {
            this.tvInfo.setText(s);
        }

        public void setTvInfoTextSize(int size) {
            this.tvInfo.setTextSize(size);
        }
    }



    private class NotificationSettingDialog extends Dialog{

        Context context;
        SwitchCompat schSettingNotice;
        Button btnDialogPositive;
        boolean settingNotice = DataIOKt.getNotificationSetting();

        public NotificationSettingDialog(@NonNull Context context, int deviceWidth) {
            super(context);
            this.context = context;
            setContentView(R.layout.dialog_notification_setting);
            schSettingNotice = findViewById(R.id.schNotificationSettingNotice);
            btnDialogPositive = findViewById(R.id.btnNotificationSettingExit);

            schSettingNotice.setChecked(settingNotice);
            schSettingNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        DataIOKt.setNotificationSetting(true);
                    else
                        DataIOKt.setNotificationSetting(false);
                }
            });

            btnDialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            WindowManager.LayoutParams params = this.getWindow().getAttributes();
            params.width = (int)(deviceWidth * 0.95);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            this.onWindowAttributesChanged(params);


        }

        @Override
        public void create() {
            super.create();
        }

    }
}