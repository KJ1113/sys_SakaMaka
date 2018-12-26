package com.example.leehangeol.sakamaka.Main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.leehangeol.sakamaka.HttpConnect;
import com.example.leehangeol.sakamaka.JsonPaser;
import com.example.leehangeol.sakamaka.NetInformTransfer;
import com.example.leehangeol.sakamaka.PostImagePaser;
import com.example.leehangeol.sakamaka.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.util.Collections.reverse;

public class FragmentTimeline extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PostInfo> mMyData;
    private TimelineAdapter mAdapter;

    public  PostInfo post;
    public JsonPostTask.Postpaser postpaser;
    public JsonLikeTask.Likepaser likepaser;
    public JsonHateTask.Hatepaser hatepaser;
    NetInformTransfer netInformTransfer = new NetInformTransfer();

    private boolean[] likecheck;
    private boolean[] hatecheck;

    String currentuser;
    public int count =0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLo);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        hatecheck = new boolean[50000];
        likecheck = new boolean[50000];
        mMyData = new ArrayList<>();
        currentuser = getArguments().getString("name");
        mAdapter = new TimelineAdapter(mMyData, currentuser,likecheck, hatecheck);//new TimelineAdapter.OnItemClickListener() {
        JsonPostTask PostTask = new JsonPostTask();
        PostTask.execute(netInformTransfer.getServerUrl() + "/allrepost");
        JsonLikeTask likeTask = new JsonLikeTask();
        likeTask.execute(netInformTransfer.getServerUrl()+"/alllikeuser");
        JsonHateTask hateTask = new JsonHateTask();
        hateTask.execute(netInformTransfer.getServerUrl()+"/allhateuser");

        return view;
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
        JsonLikeTask likeTask = new JsonLikeTask();
        likeTask.execute(netInformTransfer.getServerUrl()+"/alllikeuser");
        JsonHateTask hateTask = new JsonHateTask();
        hateTask.execute(netInformTransfer.getServerUrl()+"/allhateuser");
    }

    public class JsonPostTask extends HttpConnect {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            postpaser =new Postpaser(); // text 정보를 담아주는 클래스 생성
            postpaser.inputArray(result); // text 정보아이템에 담아 주는중
        }

        public class Postpaser extends JsonPaser {
            @Override
            public void inputArray(String str) {
                try {
                  //  jarray = new JSONArray(str);
                    jarray = new JSONObject(str).getJSONArray("result");
                    mMyData.clear();
                    for(int i=0; i < jarray.length(); i++){
                        post = new PostInfo();
                        jobject = jarray.getJSONObject(i);  // JSONObject 추출
                        post.userimgpath = jobject.getString("title");
                        post.postimgpath = jobject.getString("picture_id");
                        post.postText = jobject.getString("text");
                        post.username = jobject.getString("user_id");
                        post.like = jobject.getString("like_num");
                        post.hate = jobject.getString("hate_num");
                        post.postNo = jobject.getString("no");
                        post.date = jobject.getString("write_time");
                        mMyData.add(post);
                    }
                    reverse(mMyData);
                    mRecyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class JsonLikeTask extends HttpConnect {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            likepaser =new Likepaser(); // text 정보를 담아주는 클래스 생성
            likepaser.inputArray(result); // text 정보아이템에 담아 주는중
        }

        public class Likepaser extends JsonPaser {
            @Override
            public void inputArray(String str) {
                try {
                    jarray = new JSONObject(str).getJSONArray("result");
                   // jarray = new JSONArray(str);
                    for(int i=0; i < jarray.length(); i++){
                        jobject = jarray.getJSONObject(i);  // JSONObject 추출
                        if(jobject.getString("like_user_id").equals(currentuser)) {
                            likecheck[Integer.parseInt(jobject.getString("post_id"))] = true;
                        }
                    }
                    mRecyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class JsonHateTask extends HttpConnect {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hatepaser = new Hatepaser(); // text 정보를 담아주는 클래스 생성
            hatepaser.inputArray(result); // text 정보아이템에 담아 주는중
        }

        public class Hatepaser extends JsonPaser {
            @Override
            public void inputArray(String str) {
                try {
                    jarray = new JSONObject(str).getJSONArray("result");
                    for(int i=0; i < jarray.length(); i++){
                        jobject = jarray.getJSONObject(i);  // JSONObject 추출
                        if(jobject.getString("hate_user_id").equals(currentuser)) {
                            hatecheck[Integer.parseInt(jobject.getString("post_id"))] = true;
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