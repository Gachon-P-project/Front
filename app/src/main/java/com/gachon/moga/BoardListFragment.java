package com.gachon.moga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gachon.moga.board.BoardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.getSubjectSet;
import static com.gachon.moga.DataIOKt.getDepartment;
import static com.gachon.moga.Component.sharedPreferences;


public class BoardListFragment extends Fragment {

    private JSONObject subject_professorJSONObject;
    private SharedPreferences prefs;
    private String dept;
    private Set<String> subjectSet;
    private RecyclerView dept_recyclerView;
    private RecyclerView subject_recyclerView;
    private final ArrayList<String> dept_data = new ArrayList<>();
    private ArrayList<String> subject_data;
    private final ArrayList<String> community_data = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = this.requireContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);

        try {
//            subject_professorJSONObject = new JSONObject(prefs.getString("subject_professorJSONObject", null));
            subject_professorJSONObject = new JSONObject(DataIOKt.getSubjectProfessorJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        dept = prefs.getString("department", null);
//        subjectSet = prefs.getStringSet("subjectSet", noneSubject);
        dept = getDepartment();
        subjectSet = getSubjectSet();

        if (dept_data.size() == 0) {
            dept_data.add(dept);
        }
        subject_data = new ArrayList<String>(subjectSet);

        if (community_data.size() == 0) {
            community_data.add("자유 게시판");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_list, container, false);

        LinearLayout dept_linearLayout = view.findViewById(R.id.dept);
        LinearLayout subject_linearLayout = view.findViewById(R.id.subject);

        if(dept == null) {
            dept_linearLayout.setVisibility(View.INVISIBLE);
        } else {
            dept_recyclerView = view.findViewById(R.id.dept_recyclerView);
            dept_recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            BoardList_RecyclerAdapter dept_adapter = new BoardList_RecyclerAdapter(dept_data);
            dept_adapter.setBoardType(StateKt.BOARD_MAJOR);
            dept_recyclerView.setAdapter(dept_adapter);
        }


        if(subjectSet.equals(new HashSet<String>())) {
            subject_linearLayout.setVisibility(View.INVISIBLE);
        } else {
            subject_recyclerView = view.findViewById(R.id.subject_recyclerView);
            subject_recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            BoardList_RecyclerAdapter subject_adapter = new BoardList_RecyclerAdapter(subject_data, subject_professorJSONObject);
            subject_adapter.setBoardType(StateKt.BOARD_SUBJECT);
            subject_recyclerView.setAdapter(subject_adapter);
        }

        RecyclerView community_recyclerView = view.findViewById(R.id.community_recyclerView);
        community_recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        BoardList_RecyclerAdapter community_adapter = new BoardList_RecyclerAdapter(community_data);
        community_adapter.setBoardType(StateKt.BOARD_FREE);
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

}