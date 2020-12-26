package com.hfad.gamo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.hfad.gamo.Component.default_url;
import static com.hfad.gamo.DataIOKt.appConstantPreferences;

public class NoticeFragment extends Fragment {

    private static final String TAG = "NOTI_FRAGMENT";
    private SharedPreferences prefs;
    private String dept;
    private Notice_RecyclerAdapter adapter;
    private JSONArray responseJSONArray = new JSONArray();
    private JSONArray tempJSONArray = new JSONArray();
    private JSONObject responseJSONObject = new JSONObject();
    private JSONObject loadingJsonObject;
    private ImageView cancel_button_notice;
    private EditText editText;

    private RecyclerView recyclerView = null;
    private SwipeRefreshLayout swipeContainer;
    private VolleyForHttpMethod volley;
    private String url;
    private boolean isSearching = false;
    private String search_word = "";
    InputMethodManager imm;
    private boolean isResult = true;
    private static Context context;

    private int page = 0;


    private LoginDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        prefs = this.getContext().getSharedPreferences(appConstantPreferences, MODE_PRIVATE);
        dept = prefs.getString("department", null);

//        키보드 제어 변수
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        volley = new VolleyForHttpMethod(Volley.newRequestQueue(this.getContext()));
        page = 0;
        getFirstNotiList(new VolleyCallback() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        loadingDialog = new LoginDialog();
        loadingDialog.start(getContext());

        responseJSONArray = new JSONArray();

        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        cancel_button_notice = view.findViewById(R.id.cancel_button_notice);
        editText = view.findViewById(R.id.edit);

        recyclerView = view.findViewById(R.id.recycler_notice);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

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
                    getFirstNotiList(new VolleyCallback() {
                        @Override
                        public void onSuccess() {
                            initRecyclerView();

                            Log.d("FRAGMENT ::", "onRefresh: finish");
                            swipeContainer.setRefreshing(false);
                        }
                    });
                } else {
                    getFirstSearchedNoti(new VolleyCallback() {
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

        swipeContainer.setColorSchemeResources(R.color.indigo500);

        ImageView search_button = view.findViewById(R.id.search_button);
        search_button.setClickable(true);


//        검색 (키보드 돋보기 클릭 시)
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                adapter.setIsLoading(true);
                page = 0;
                responseJSONArray = new JSONArray();
                search_word = editText.getText().toString();
                getFirstSearchedNoti(new VolleyCallback() {
                    @Override
                    public void onSuccess() {
                        initRecyclerView();
                        Log.d(TAG, "Search :: onSuccess: finish");
                    }
                });
//                키보드 숨김
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
        });
//        검색 (돋보기 이미지버튼 클릭 시)
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 0;
                search_word = editText.getText().toString();
                getFirstSearchedNoti(new VolleyCallback() {
                    @Override
                    public void onSuccess() {
                        initRecyclerView();
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
                search_word = "";
                page = 0;
                responseJSONArray = new JSONArray();
                getFirstNotiList(new VolleyCallback() {
                    @Override
                    public void onSuccess() {
                        initRecyclerView();
                        Log.d(TAG, "cancel_search: onSuccess");
                    }
                });
                isSearching = false;
                cancel_button_notice.setVisibility(View.GONE);
                editText.getText().clear();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.toolbar_clicked_board);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<b>" + dept + "</b>", 0));

    }


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
                            Log.d(TAG, "onLoadMore: responseJSONArray : tempItemInserted!!");
                        }
                    });
                    if(!isSearching) {
                        getMoreNotiList();
                    } else {
                        getMoreSearchedNoti();
                    }
                } else  {
                    Toast.makeText(getActivity(), "검색 완료", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //    전체 조회. 첫번째에만 사용됨. 데이터 가져오는 함수.
    private void getFirstNotiList(final VolleyCallback callback) {
        url = default_url + "/notice/read/" + page + "/" + dept;
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
    private void getMoreNotiList() {
        url = default_url + "/notice/read/" + page + "/" + dept;

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
    private void getFirstSearchedNoti(final VolleyCallback callback) {
        url = default_url + "/notice/read/" + page + "/" + dept + "/" + search_word;
        Log.d("FRAGMENT::", "SEARCH :: URL : " + url);

        responseJSONArray = new JSONArray();
        volley.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if(response.length() == 0) {
                    Toast.makeText(getActivity(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    isResult = false;
                } else {
                    Log.d("FRAGMENT::", "SEARCH :: RESULT : " + response);
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
    private void getMoreSearchedNoti() {
        url = default_url + "/notice/read/" + page + "/" + dept + "/" + search_word;
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
        adapter.dataUpdate();
    }

   /* public static void showDetail(String board_no) {
        ((MainActivity) context).replaceFragment(new NoticeDetailFragment());

    }*/
}