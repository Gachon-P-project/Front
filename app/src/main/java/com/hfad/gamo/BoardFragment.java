package com.hfad.gamo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hfad.gamo.ClickedBoard.ClickedBoardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.hfad.gamo.DataIOKt.appConstantPreferences;
import static com.hfad.gamo.DataIOKt.getSubjectSet;


public class BoardFragment extends Fragment {

    private JSONObject subject_professorJSONObject;
    private SharedPreferences prefs;
    private String dept;
    private Set<String> subjectSet;
    private RecyclerView dept_recyclerView;
    private RecyclerView subject_recyclerView;
    private RecyclerView community_recyclerView;
    private Board_RecyclerAdapter dept_adapter;
    private Board_RecyclerAdapter subject_adapter;
    private Board_RecyclerAdapter community_adapter;
    private ArrayList<String> dept_data = new ArrayList<>();
    private ArrayList<String> subject_data;
    private ArrayList<String> community_data = new ArrayList<>();
    private Set<String> noneSubject = new HashSet<>();

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = Objects.requireNonNull(this.getContext()).getSharedPreferences(appConstantPreferences, MODE_PRIVATE);

        try {
//            subject_professorJSONObject = new JSONObject(prefs.getString("subject_professorJSONObject", null));
            subject_professorJSONObject = new JSONObject(DataIOKt.getSubjectProfessorJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        dept = prefs.getString("department", null);
//        subjectSet = prefs.getStringSet("subjectSet", noneSubject);
        dept = DataIOKt.getDepartment();
        subjectSet = getSubjectSet();

        if (dept_data.size() == 0) {
            dept_data.add(dept);
        }
        subject_data = new ArrayList<String>(subjectSet);

        if (community_data.size() == 0) {
            community_data.add("자유 게시판");
            community_data.add("새내기 게시판");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        //showYourDeptAndSubject(view);


        LinearLayout dept_linearLayout = view.findViewById(R.id.dept);
        LinearLayout subject_linearLayout = view.findViewById(R.id.subject);

        if(dept == null) {
            dept_linearLayout.setVisibility(View.INVISIBLE);
        } else {
            dept_recyclerView = view.findViewById(R.id.dept_recyclerView);
            dept_recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            dept_adapter = new Board_RecyclerAdapter(dept_data);
            dept_adapter.setBoardType(StateKt.BOARD_MAJOR);
            dept_recyclerView.setAdapter(dept_adapter);
        }

//        if(subjectSet == noneSubject) {
        if(subjectSet.equals(new HashSet<String>())) {
            subject_linearLayout.setVisibility(View.INVISIBLE);
        } else {
            subject_recyclerView = view.findViewById(R.id.subject_recyclerView);
            subject_recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            subject_adapter = new Board_RecyclerAdapter(subject_data, subject_professorJSONObject);
            dept_adapter.setBoardType(StateKt.BOARD_SUBJECT);
            subject_recyclerView.setAdapter(subject_adapter);
        }

        community_recyclerView = view.findViewById(R.id.community_recyclerView);
        community_recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        community_adapter = new Board_RecyclerAdapter(community_data);
        dept_adapter.setBoardType(StateKt.BOARD_FREE);
        community_recyclerView.setAdapter(community_adapter);

        dept_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        subject_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        community_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        return view;
    }


/*private void showYourDeptAndSubject(View InflatedView) {
        showYourDept(InflatedView);
        showYourSubject(InflatedView);
    }

    private void showYourDept(View InflatedView) {
        final String Dept;
        Dept = prefs.getString("department",null);

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
        Set<String> noneSubject = new HashSet<>();
        final Set<String> subjectSet = prefs.getStringSet("subjectSet", noneSubject);

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
    }*/

    private void postIntent(Context context, String title) {
        final Intent intent = new Intent(context, ClickedBoardActivity.class);
        intent.putExtra("title", title);
        try {
            intent.putExtra("professor", subject_professorJSONObject.get(title).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }
}