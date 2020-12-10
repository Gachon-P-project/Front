package com.hfad.gamo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hfad.gamo.ClickedBoard.ClickedBoardActivity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.hfad.gamo.DataIOKt.appConstantPreferences;


public class BoardFragment extends Fragment {

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_board, container, false);
        showYourDeptAndSubject(view);

        return view;
    }

    private void showYourDeptAndSubject(View InflatedView) {
        showYourDept(InflatedView);
        showYourSubject(InflatedView);
    }

    private void showYourDept(View InflatedView) {
        final String Dept;

        SharedPreferences pref = this.getContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        Dept = pref.getString("department",null);

        if(Dept == null)
            return;
        else {
            LinearLayout linearLayout = InflatedView.findViewById(R.id.dept);

            ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final TextView textView = new TextView(this.getContext());
            textView.setLayoutParams(lparams);
            textView.setText(Dept);
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    postIntent(v.getContext(), textView.getText().toString());
                }
            });
            linearLayout.addView(textView);
        }
    }

    private void showYourSubject(View InflatedView) {

        SharedPreferences pref = this.getContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);

        Set<String> noneSubject = new HashSet<>();
        final Set<String> subjectSet = pref.getStringSet("subjectSet", noneSubject);

        if(subjectSet == noneSubject)
            return;
        else {
            LinearLayout linearLayout = InflatedView.findViewById(R.id.subject);
            ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            Iterator<String> iterator = subjectSet.iterator();

            while (iterator.hasNext()) {
                final TextView textView = new TextView(this.getContext());
                textView.setLayoutParams(lparams);
                textView.setText(iterator.next());
                textView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        postIntent(v.getContext(), textView.getText().toString());
                    }
                });
                linearLayout.addView(textView);
            }
        }
    }

    private void postIntent(Context context, String title) {
        final Intent intent = new Intent(context, ClickedBoardActivity.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}