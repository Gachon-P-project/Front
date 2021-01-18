package com.gachon.moga;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
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
import android.widget.TextView;

import com.gachon.moga.R;

import java.net.URL;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.resetSharedPreference;

public class MyPageFragment extends Fragment {
    private SharedPreferences prefs;

    private static int deviceWidth, deviceHeight;
    private NickNameDialog nickNameDialog;
    private NotificationSettingDialog notificationSettingDialog;
//    private LinearLayout llAppInfo;
    private TextView tvUsername, tvNickname, tvMajor, tvStudentId, tvChangeNickname, tvLogout, llNotificationSettings, tvAppVersion, tvInquiry, tvTermsServiceInfo, tvOpenSource, tvTermsPersonaInfo;
    private ImageView imgMyPhoto;
    private String username, major, nickname, studnetId, myPhotoUrl;

    public MyPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
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
        tvTermsServiceInfo = view.findViewById(R.id.tv_mypage_terms_service);
        tvTermsPersonaInfo = view.findViewById(R.id.tv_mypage_terms_personal);
        tvOpenSource = view.findViewById(R.id.open_source);

        imgMyPhoto = view.findViewById(R.id.iv_mypage_photo);

        nickname = prefs.getString("nickname", "-");
        tvUsername.setText(username);
        tvMajor.setText(major);
        tvStudentId.setText(studnetId);
        tvNickname.setText(nickname);

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        deviceWidth = dm.widthPixels;
        deviceHeight = dm.heightPixels;

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
                notificationSettingDialog = new NotificationSettingDialog(getActivity());
                notificationSettingDialog.show();
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                resetSharedPreference();
//                startActivity(intent);
                LogoutDialog dialog = new LogoutDialog(Objects.requireNonNull(getActivity()));
                dialog.setTitle("");
                dialog.setContents("로그아웃을 하시겠습니까?");
                dialog.setPositiveButtonBackground(R.drawable.dialog_button_red);
                dialog.setPositiveButtonText("로그아웃");
                dialog.setTitleVisibility(View.GONE);
                dialog.show();
            }
        });

        tvAppVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity());
                dialog.setTvInfo(getString(R.string.version));
                dialog.setTvTitle("앱 버전");
                dialog.setDialogHeight(0.3);
                dialog.setTvInfoAlignment(View.TEXT_ALIGNMENT_CENTER);
                dialog.show();
            }
        });

        tvInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity());
                dialog.setTvInfo(getString(R.string.inquiry));
                dialog.setTvTitle("문의하기");
                dialog.setDialogHeight(0.3);
                dialog.setTvInfoAlignment(View.TEXT_ALIGNMENT_CENTER);
                dialog.show();
            }
        });

        tvTermsServiceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity());
                dialog.setTvInfo(getString(R.string.terms_service));
                dialog.setTvTitle("서비스 이용약관");
                dialog.show();
            }
        });

        tvTermsPersonaInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity());
                dialog.setTvInfo(getString(R.string.terms_personal));
                dialog.setTvTitle("개인정보 처리방침");
                dialog.show();
            }
        });

        tvOpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPageFragment.InfoDialog dialog = new MyPageFragment.InfoDialog(getActivity());
                dialog.setTvInfo(getString(R.string.opensource_license));
                dialog.setTvTitle("오픈소스 라이센스");
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

    private class LogoutDialog extends Dialog {

        private TextView tvTitle, tvContents;
        private Button btnPositive, btnNegative;
        private Context context;

        public LogoutDialog(@NonNull Context context) {
            super(context);
            this.context = context;
            setContentView(R.layout.dialog_default);
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            tvTitle = findViewById(R.id.tv_dialog_default_title);
            tvContents = findViewById(R.id.tv_dialog_default_contents);
            btnPositive = findViewById(R.id.button_dialog_default_positive);
            btnNegative = findViewById(R.id.button_dialog_default_negative);

            WindowManager.LayoutParams params = this.getWindow().getAttributes();
            params.width = (int) (deviceWidth * 0.8);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            this.onWindowAttributesChanged(params);

            btnPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    resetSharedPreference();
                    startActivity(intent);
                }
            });
            btnNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        @Override
        public void create() {
            super.create();
        }
        public void setTitle(String s) {
            tvTitle.setText(s);
        }
        public void setContents(String s) {
            tvContents.setText(s);
        }
        public void setPositiveButtonText(String s) {
            btnPositive.setText(s);
        }
        public void setNegativeButtonText(String s) {
            btnNegative.setText(s);
        }
        public void setPositiveButtonBackground(@DrawableRes int resourceId) {
            btnPositive.setBackgroundResource(resourceId);
        }
        public void setNegativeButtonBackground(@DrawableRes int resourceId) {
            btnNegative.setBackgroundResource(resourceId);
        }
        public void setTitleVisibility(int visibility){
            tvTitle.setVisibility(visibility);
        }
    }

    private class InfoDialog extends Dialog {

        TextView tvInfo, tvTitle;
        Context context;
//        TextView btnDialogPositive;
        Button btnDialogPositive;
        private WindowManager.LayoutParams params = this.getWindow().getAttributes();

        public InfoDialog(@NonNull Context context) {
            super(context);
            this.context = context;
            setContentView(R.layout.dialog_info);

            Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            tvInfo = findViewById(R.id.tv_dialog_info_content);
            tvTitle = findViewById(R.id.tv_dialog_info_title);
//            btnDialogPositive = findViewById(R.id.tvButtonDialogPositive);
            btnDialogPositive = findViewById(R.id.buttonDialogPositive);
            btnDialogPositive.setClickable(true);
            btnDialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            params.width = (int) (deviceWidth * 0.8);
            params.height = (int)(deviceHeight * 0.7);
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

        public void setTvTitle(String s) {
            this.tvTitle.setText(s);
        }

        public void setDialogHeight(double d) {
            params.height = (int)(deviceHeight * d);
            this.onWindowAttributesChanged(params);
        }
        public void setTvInfoAlignment(int text_alignment) {
            tvInfo.setTextAlignment(text_alignment);
        }
    }



    private class NotificationSettingDialog extends Dialog{

        Context context;
        SwitchCompat schSettingNotice;
        Button btnDialogPositive;
        boolean settingNotice = DataIOKt.getNotificationSetting();

        public NotificationSettingDialog(@NonNull Context context) {
            super(context);
            this.context = context;
            setContentView(R.layout.dialog_notification_setting);
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            params.width = (int)(deviceWidth * 0.8);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            this.onWindowAttributesChanged(params);


        }

        @Override
        public void create() {
            super.create();
        }

    }
}