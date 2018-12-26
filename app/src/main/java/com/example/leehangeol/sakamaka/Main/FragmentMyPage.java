package com.example.leehangeol.sakamaka.Main;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.example.leehangeol.sakamaka.HttpConnect;
import com.example.leehangeol.sakamaka.JsonPaser;
import com.example.leehangeol.sakamaka.NetInformTransfer;
import com.example.leehangeol.sakamaka.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.util.Collections.reverse;

public class FragmentMyPage extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    MainActivity activity;

    public SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PostInfo> myTimLineData = new ArrayList<>();
    private ArrayList<PostInfo> mMyData = new ArrayList<>();
    private MyPageAdapter mAdapter = new MyPageAdapter(mMyData);

    private ImageButton postadd;
    private TextView username;
    private ImageView img;


    public  PostInfo post;
    public JsonPostTask.Postpaser postpaser;
    NetInformTransfer netInformTransfer = new NetInformTransfer();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLo);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        AQuery aQuery = new AQuery(getActivity());

        username = (TextView)view.findViewById(R.id.username);
        img = (ImageView)view.findViewById(R.id.userimg);
        postadd = (ImageButton)view.findViewById(R.id.postadd);

        String userId = getArguments().getString("name");
        String path = getArguments().getString("img");

        username.setText(userId);
        aQuery.id(img).image(path);
        img.setBackground(new ShapeDrawable(new OvalShape()));
        img.setClipToOutline(true);

        postadd.setOnClickListener(new View.OnClickListener() {
            // 요청을 보내야 하는데 메인 액티비티에 다가 메소드를 하나 만들어야 한다.
            @Override
            public void onClick(View v) {
                activity.callFragment(3);
            }
        });

        JsonPostTask PostTask = new JsonPostTask();
        PostTask.execute(netInformTransfer.getServerUrl() + "/allrepost");

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.postadd:
                activity.callFragment(3);
                break;
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                RefreshPost();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        },500);
    }

    public void  RefreshPost() {
        JsonPostTask PostTask = new JsonPostTask();
        PostTask.execute(netInformTransfer.getServerUrl() + "/allrepost");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        activity = null;
    }

    public class JsonPostTask extends HttpConnect {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            postpaser = new Postpaser(); // text 정보를 담아주는 클래스 생성
            postpaser.inputArray(result); // text 정보아이템에 담아 주는중
        }

        public class Postpaser extends JsonPaser {
            @Override
            public void inputArray(String str) {
                try {
                    //  jarray = new JSONArray(str);
                    jarray = new JSONObject(str).getJSONArray("result");
                    myTimLineData.clear();
                    for(int i=0; i < jarray.length(); i++){
                        {
                            post = new PostInfo();
                            jobject = jarray.getJSONObject(i);  // JSONObject 추출
                            post.userimgpath = jobject.getString("title");
                            post.postimgpath = jobject.getString("picture_id");
                            post.postText = jobject.getString("text");
                            post.username = jobject.getString("user_id");
                            post.like = jobject.getString("like_num");
                            post.hate = jobject.getString("hate_num");
                            post.date = jobject.getString("write_time");
                            post.postNo = jobject.getString("no");
                            myTimLineData.add(post);
                        }
                    }
                    reverse(myTimLineData);
                    mMyData.clear();
                    for(int i = 0;i < myTimLineData.size();i++)
                    {
                        if(myTimLineData.get(i).username.equals(username.getText()))
                        {
                            mMyData.add(myTimLineData.get(i));
                        }
                    }

                    mRecyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
