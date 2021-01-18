package com.gachon.moga;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gachon.moga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static com.gachon.moga.Component.default_url;
import static com.gachon.moga.Component.sharedPreferences;
import static com.gachon.moga.DataIOKt.appConstantPreferences;
import static com.gachon.moga.DataIOKt.getNotificationSetting;
import static com.gachon.moga.DataIOKt.setNotificationSetting;

public class NoticeFragment extends Fragment {

    private static final String TAG = "NOTICE_FRAGMENT";
    private String dept;
    private Notice_RecyclerAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private JSONArray tempJSONArray = new JSONArray();
    private JSONObject responseJSONObject = new JSONObject();
    private JSONObject loadingJsonObject;
    private ImageView cancel_button_notice;
    private EditText edtSearchNotice;
    private TextView tvToolbarTitle;

    private RecyclerView recyclerView = null;
    private SwipeRefreshLayout swipeContainer;
    private ConstraintLayout clNoData;
    private ImageButton imgBtnSetNotification;
    private VolleyForHttpMethod volley;
    private String url;
    private boolean isSearching = false;
    private String search_word = "";
    InputMethodManager imm;
    private boolean isResult = true;

    private int page = 0;


    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        sharedPreferences = getActivity().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        dept = DataIOKt.getDepartment();

//        키보드 제어 변수
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(this.getContext()));
        page = 0;
        getFirstNoticeList(new VolleyCallback() {
            @Override
            public void onSuccess() {
                adapter.dataUpdate();
            }
        });
        try {
            loadingJsonObject = new JSONObject("{\"title\" : \"loading..\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        loadingDialog = new LoadingDialog();
        loadingDialog.start(getContext());

        responseJSONArray = new JSONArray();

        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        clNoData = view.findViewById(R.id.clNoDataNotice);
        cancel_button_notice = view.findViewById(R.id.cancel_button_notice);
        edtSearchNotice = view.findViewById(R.id.edtSearchNotice);
        recyclerView = view.findViewById(R.id.recycler_notice);
        imgBtnSetNotification = view.findViewById(R.id.imgBtn_noticeFragment_setNotification);
        tvToolbarTitle = view.findViewById(R.id.tv_notice_toolbarTitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        tvToolbarTitle.setText(getString(R.string.notice_title, dept));

        adapter = new Notice_RecyclerAdapter(responseJSONArray, dept);
        adapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        loadPost();

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_notice);

//        swipe refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("FRAGMENT ::", "onRefresh: Searching : " + isSearching);
                adapter.setIsLoading(true);
                page = 0;
                responseJSONArray = new JSONArray();

                if(!isSearching) {
                    getFirstNoticeList(new VolleyCallback() {
                        @Override
                        public void onSuccess() {
                            initRecyclerView();

                            Log.d("FRAGMENT ::", "onRefresh: finish");
                            swipeContainer.setRefreshing(false);
                        }
                    });
                } else {
                    getFirstSearchedNotice(new VolleyCallback() {
                        @Override
                        public void onSuccess() {
                            initRecyclerView();
                            Log.d("FRAGMENT ::", "onRefresh: finish");
                            swipeContainer.setRefreshing(false);
                        }
                    });
                }
                loadPost();
            }
        });

        if (getNotificationSetting()) {
            imgBtnSetNotification.setImageResource(R.drawable.ic_notifications_active);
        } else {
//            Drawable icon_disable = getResources().getDrawable(R.drawable.ic_notifications_disabled);
//            menu.getItem(0).setIcon(icon_disable);
            imgBtnSetNotification.setImageResource(R.drawable.ic_notifications_disabled);
        }
        imgBtnSetNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getNotificationSetting()) {
                    setNotificationSetting(false);
                    imgBtnSetNotification.setImageResource(R.drawable.ic_notifications_disabled);
                    Toast.makeText(getContext(), "알림 비활성", Toast.LENGTH_SHORT).show();
                } else {
                    setNotificationSetting(true);
                    imgBtnSetNotification.setImageResource(R.drawable.ic_notifications_active);
                    Toast.makeText(getContext(), "알림 활성", Toast.LENGTH_SHORT).show();
                }
            }
        });

        swipeContainer.setColorSchemeResources(R.color.indigo500);

        final ImageView search_button = view.findViewById(R.id.search_button);
        search_button.setClickable(true);

//        검색 EditText Filter
        edtSearchNotice.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(""))
                    return source;
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-하-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025\\u00B7\\uFE55]+$");
                if (pattern.matcher(source).matches()) {
                    return source;
                }
                return "";
            }
        }});

//        검색 (키보드 돋보기 클릭 시)
        edtSearchNotice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search_button.performClick();
                return true;
            }
        });
//        검색 (돋보기 이미지버튼 클릭 시)
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.start(getContext());
                adapter.setIsLoading(true);
                page = 0;
                search_word = edtSearchNotice.getText().toString();
                getFirstSearchedNotice(new VolleyCallback() {
                    @Override
                    public void onSuccess() {
                        initRecyclerView();
                        loadingDialog.finish();
                        edtSearchNotice.clearFocus();
                        Log.d(TAG, "Search :: onSuccess: finish");
                    }
                });
//                키보드 숨김
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });


//        검색 취소
        cancel_button_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.start(getContext());
                swipeContainer.setVisibility(View.VISIBLE);
                clNoData.setVisibility(View.GONE);
                search_word = "";
                page = 0;
                responseJSONArray = new JSONArray();
                getFirstNoticeList(new VolleyCallback() {
                    @Override
                    public void onSuccess() {
                        initRecyclerView();
                        loadingDialog.finish();
                        edtSearchNotice.clearFocus();
                        Log.d(TAG, "cancel_search: onSuccess");
                    }
                });
                isSearching = false;
                cancel_button_notice.setVisibility(View.GONE);
                edtSearchNotice.getText().clear();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.toolbar_notice);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<b>" + dept + " 공지사항</b>", 0));


    }

    @Override
    public void onResume() {
        super.onResume();
        edtSearchNotice.setText("");

    }

//    infinite scroll
    private void loadPost() {
        adapter.setOnLoadMoreListener(new Notice_RecyclerAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                adapter.setIsLoading(true);
                page++;
                if(responseJSONArray.length() <= 1000 && isResult) {
                    responseJSONArray.put(loadingJsonObject);        // loadingJsonObject를 삽입하여 리사이클뷰에서 뷰타입을 로딩타입으로 인식
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(responseJSONArray.length() - 1);
                            Log.d(TAG, "onLoadMore: responseJSONArray : loadingItemInserted!!");
                        }
                    });
                    if(!isSearching) {
                        getMoreNoticeList();
                    } else {
                        getMoreSearchedNotice();
                    }
                } else if (responseJSONArray.length() > 1000){          // recyclerview 아이템 최대 1000개.
                    Toast.makeText(getActivity(), "검색 완료", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //    전체 조회. 첫번째에만 사용됨. 데이터 가져오는 함수.
    private void getFirstNoticeList(final VolleyCallback callback) {
        url = default_url + getString(R.string.noticeList, page, dept);
        Log.d(TAG, "getAllNoti: url : " + url);

        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        responseJSONObject = response.getJSONObject(i);
                        responseJSONArray.put(responseJSONObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "getFirstNotiList: onResponse: " + responseJSONArray.toString());
                adapter.setIsLoading(false);

                callback.onSuccess();
                loadingDialog.finish();

            }
        });
    }

    //    두번째부터 사용됨. 데이터 추가로 가져올때 사용.
    private void getMoreNoticeList() {
        url = default_url + getString(R.string.noticeList, page, dept);

        tempJSONArray = new JSONArray();
        Log.d("GET_MORE_NOTICE", "url : " + url);
        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

//                로딩 아이템 제거
                responseJSONArray.remove(responseJSONArray.length() - 1);
                adapter.notifyItemRemoved(responseJSONArray.length());
                Log.d(TAG, "onResponse: tempItemRemoved!!");

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        tempJSONArray.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "getFirstNotiList: onResponse: temp : " + tempJSONArray.toString());
                try {
                    for (int i = 0 ; i < tempJSONArray.length() ; i++) {
                        responseJSONArray.put(tempJSONArray.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.dataUpdate();
                adapter.setIsLoading(false);
                Log.d("FRAGMENT", "responseJSONArray: " + responseJSONArray);
            }
        });
    }

//    검색 기능 (처음)
    private void getFirstSearchedNotice(final VolleyCallback callback) {
        url = default_url + getString(R.string.noticeSearchedList, page, dept, search_word);
        Log.d("FRAGMENT::", "SEARCH :: URL : " + url);

        responseJSONArray = new JSONArray();
        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if(response.length() == 0) {
                    swipeContainer.setVisibility(View.GONE);
                    clNoData.setVisibility(View.VISIBLE);
                    isResult = false;
                } else {
                    Log.d("FRAGMENT::", "SEARCH :: RESULT : " + response);
                    swipeContainer.setVisibility(View.VISIBLE);
                    clNoData.setVisibility(View.GONE);
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            responseJSONObject = response.getJSONObject(i);
                            responseJSONArray.put(responseJSONObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                isSearching = true;
                adapter.setIsLoading(false);
                cancel_button_notice.setVisibility(View.VISIBLE);
                callback.onSuccess();
            }
        });
    }

    //    검색 기능 (추가)
    private void getMoreSearchedNotice() {
        url = default_url + getString(R.string.noticeSearchedList, page, dept, search_word);
        Log.d("FRAGMENT::", "SEARCH More :: URL : " + url);

        tempJSONArray = new JSONArray();
        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("FRAGMENT::", "SEARCH More :: RESULT : " + response);

//                로딩 아이템 제거
                responseJSONArray.remove(responseJSONArray.length() - 1);
                adapter.notifyItemRemoved(responseJSONArray.length());

                if(response.length() == 0) {
                    Toast.makeText(getActivity(), "마지막 결과입니다.", Toast.LENGTH_SHORT).show();
                    isResult = false;
                } else {

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            responseJSONObject = response.getJSONObject(i);
                            tempJSONArray.put(responseJSONObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        for (int i = 0; i < tempJSONArray.length(); i++) {
                            responseJSONArray.put(tempJSONArray.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                isSearching = true;
                adapter.notifyDataSetChanged();
                adapter.setIsLoading(false);
                page++;
                Log.d("FRAGMENT::", "SEARCH More :: isLoading : " + adapter.getIsLoading());

                cancel_button_notice.setVisibility(View.VISIBLE);
            }
        });
    }

    public interface VolleyCallback {
        void onSuccess();
    }

//    리사이클뷰 내용 전체 변경 시 사용
    private void initRecyclerView() {
        isResult = true;
        recyclerView.removeAllViews();
        recyclerView.setLayoutManager(new LinearLayoutManager(NoticeFragment.this.getContext()));
        adapter = new Notice_RecyclerAdapter(responseJSONArray, dept);
        recyclerView.clearOnScrollListeners();
        adapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        loadPost();
//        adapter.dataUpdate();
        adapter.notifyDataSetChanged();
    }

//    @SuppressLint("ResourceAsColor")
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_toolbar_set_notification, menu);
//        if (getNotificationSetting()) {
//            Drawable icon_active = getResources().getDrawable(R.drawable.ic_notifications_active);
//            menu.getItem(0).setIcon(icon_active);
//        } else {
//            Drawable icon_disable = getResources().getDrawable(R.drawable.ic_notifications_disabled);
//            menu.getItem(0).setIcon(icon_disable);
//        }
//    }
//
//    @SuppressLint("ResourceAsColor")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.action_set_notification) {
//            if (getNotificationSetting()) {
//                setNotificationSetting(false);
//                Drawable icon_disable = getResources().getDrawable(R.drawable.ic_notifications_disabled);
//                item.setIcon(icon_disable);
//                Toast.makeText(getContext(), "알림 비활성", Toast.LENGTH_SHORT).show();
//            } else {
//                setNotificationSetting(true);
//                Drawable icon_active = getResources().getDrawable(R.drawable.ic_notifications_active);
//                item.setIcon(icon_active);
//                Toast.makeText(getContext(), "알림 활성", Toast.LENGTH_SHORT).show();
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
}