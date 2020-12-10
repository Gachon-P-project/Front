package com.hfad.gamo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.hfad.gamo.DataIOKt.appConstantPreferences;


public class BoardFragment extends Fragment {

    public BoardFragment() {
        // Required empty public constructor
    }

    final Context context = this.getContext();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_board, container, false);

        /*LinearLayout linearLayout = view.findViewById(R.id.dept);

        ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(this.getContext());
        tv.setLayoutParams(lparams);
        tv.setText("컴퓨터 공학과");
        linearLayout.addView(tv);*/

        showYourDeptAndSubject(view);

        return view;
    }

    void showYourDeptAndSubject(View InflatedView) {
        showYourDept(InflatedView);
        showYourSubject(InflatedView);
    }

    void showYourDept(View InflatedView) {
        String Dept;

        SharedPreferences pref = this.getContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        Dept = pref.getString("department",null);

        if(Dept == null)
            return;
        else {
            LinearLayout linearLayout = InflatedView.findViewById(R.id.dept);

            ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(this.getContext());
            textView.setLayoutParams(lparams);
            textView.setText(Dept);
            linearLayout.addView(textView);
        }
    }

    void showYourSubject(View InflatedView) {


        SharedPreferences pref = this.getContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);

        Set<String> noneSubject = new HashSet<>();
        final Set<String> subjectSet = pref.getStringSet("subjectSet", noneSubject);


        if(subjectSet == noneSubject)
            return;
        else {


            /*getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout linearLayout = InflatedView.findViewById(R.id.dept);
                    ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    TextView textView = new TextView(context);
                    textView.setLayoutParams(lparams);

                    Iterator<String> iterator = subjectSet.iterator();
                    while (iterator.hasNext()) {
                        textView.setText(iterator.next());
                        linearLayout.addView(textView);
                    }
                }
            });*/

            LinearLayout linearLayout = InflatedView.findViewById(R.id.subject);
            ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            Iterator<String> iterator = subjectSet.iterator();
            while (iterator.hasNext()) {
                TextView textView = new TextView(this.getContext());
                textView.setLayoutParams(lparams);
                textView.setText(iterator.next());
                linearLayout.addView(textView);
            }
        }
    }

}