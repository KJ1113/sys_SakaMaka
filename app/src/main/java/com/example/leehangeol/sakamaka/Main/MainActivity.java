package com.example.leehangeol.sakamaka.Main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.leehangeol.sakamaka.ARactivity;
import com.example.leehangeol.sakamaka.HttpConnect;
import com.example.leehangeol.sakamaka.JsonPaser;
import com.example.leehangeol.sakamaka.NetInformTransfer;
import com.example.leehangeol.sakamaka.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.util.Collections.reverse;

public class MainActivity extends AppCompatActivity {


    private Button timeline;
    private Button mypage;
    private Button arView;

    private TextView username;

//    private ArrayList<PostInfo> mMyData = new ArrayList<>();
//    public  PostInfo post;
//    public JsonPostTask.Postpaser postpaser;
//    NetInformTransfer netInformTransfer = new NetInformTransfer();



    private final int TIMELINE = 1;
    private final int MYPAGE = 2;
    private final int POST_ADD = 3;

    FragmentTimeline fTimeLine;
    FragmentMyPage fMyPage;
    FragmentPostWrite fPostAdd;
    FragmentPostUpdate fPostUpdate;

    String currentPostText;
    String currentPostImg;
    String currentPostNo;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeline = (Button)findViewById(R.id.timeline);
        mypage = (Button)findViewById(R.id.mypage);
        arView = (Button)findViewById(R.id.arView);

        Intent intent = getIntent();

        callFragment(TIMELINE);

        timeline.setOnClickListener(new View.OnClickListener() {
            // 요청을 보내야 하는데 메인 액티비티에 다가 메소드를 하나 만들어야 한다.
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                timeline.setBackground(getDrawable(R.drawable.timeline1));
                mypage.setBackground(getDrawable(R.drawable.mypage1));
                callFragment(TIMELINE);
            }
        });

        mypage.setOnClickListener(new View.OnClickListener() {
            // 요청을 보내야 하는데 메인 액티비티에 다가 메소드를 하나 만들어야 한다.
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                timeline.setBackground(getDrawable(R.drawable.timeline2));
                mypage.setBackground(getDrawable(R.drawable.mypage2));
                callFragment(MYPAGE);
            }
        });

        arView.setOnClickListener(new View.OnClickListener() {
            // 요청을 보내야 하는데 메인 액티비티에 다가 메소드를 하나 만들어야 한다.
            @Override
            public void onClick(View v) {
                Intent arIntent = new Intent( getApplicationContext(), ARactivity.class);
                startActivity(arIntent);
            }
        });

       // JsonPostTask PostTask = new JsonPostTask();
       // PostTask.execute(netInformTransfer.getServerUrl() + "/allrepost");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void callFragment(int frament_no) {
        // 프래그먼트 사용을 위해
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (frament_no) {
            case 1:
                if(fTimeLine ==null) {
                    Intent intent = getIntent();
                    fTimeLine = new FragmentTimeline();
                    Bundle bundle = new Bundle(1);
                    bundle.putString("name", intent.getStringExtra("name")); // key , value
                    fTimeLine.setArguments(bundle);
                    transaction.replace(R.id.fragment_container, fTimeLine);
                    transaction.commit();
                }
                else {
                    transaction.replace(R.id.fragment_container, fTimeLine);
                    transaction.commit();
                }
                // '프래그먼트1' 호출
                break;

            case 2:
                if(fMyPage ==null) {
                    Intent intent = getIntent();
                    fMyPage = new FragmentMyPage();
                    Bundle bundle = new Bundle(2); // 파라미터는 전달할 데이터 개수
                    bundle.putString("name", intent.getStringExtra("name")); // key , value
                    bundle.putString("img", intent.getStringExtra("img"));
                    fMyPage.setArguments(bundle);

                    transaction.replace(R.id.fragment_container, fMyPage);
                    transaction.commit();
                }
                else {
                    transaction.replace(R.id.fragment_container, fMyPage);
                    transaction.commit();
                }
                break;
            case 3:
                if(fPostAdd ==null) {

                    Intent intent = getIntent();
                    fPostAdd = new FragmentPostWrite();

                    Bundle bundle = new Bundle(2); // 파라미터는 전달할 데이터 개수
                    bundle.putString("name", intent.getStringExtra("name")); // key , value
                    bundle.putString("img", intent.getStringExtra("img"));
                    fPostAdd.setArguments(bundle);

                    transaction.replace(R.id.fragment_container, fPostAdd);
                    transaction.commit();
                }
                else {
                    transaction.replace(R.id.fragment_container, fPostAdd);
                    transaction.commit();
                }
                break;
        }
    }
    public void updatePost()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Intent intent = getIntent();
        fPostUpdate = new FragmentPostUpdate();

        Bundle bundle = new Bundle(5); // 파라미터는 전달할 데이터 개수
        bundle.putString("name", intent.getStringExtra("name")); // key , value
        bundle.putString("img", intent.getStringExtra("img"));
        bundle.putString("cPostText", currentPostText);
        bundle.putString("cPostImg", currentPostImg);
        bundle.putString("cPostNo", currentPostNo);
        fPostUpdate.setArguments(bundle);

        transaction.replace(R.id.fragment_container, fPostUpdate);
        transaction.commit();
    }

    public void repflahFragment2()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Intent intent = getIntent();
        fMyPage = new FragmentMyPage();
        Bundle bundle = new Bundle(2); // 파라미터는 전달할 데이터 개수
        bundle.putString("name", intent.getStringExtra("name")); // key , value
        bundle.putString("img", intent.getStringExtra("img"));
        fMyPage.setArguments(bundle);

        transaction.replace(R.id.fragment_container, fMyPage);
        transaction.commit();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void update(String postNo, String currentPostText, String currentPostImg)
    {
        this.currentPostText = currentPostText;
        this.currentPostImg = currentPostImg;
        currentPostNo = postNo;
        updatePost();
    }
//    public class JsonPostTask extends HttpConnect {
//        public String result2;
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            postpaser = new JsonPostTask.Postpaser(); // text 정보를 담아주는 클래스 생성
//            postpaser.inputArray(result); // text 정보아이템에 담아 주는중
//            result2 = postpaser.getName();
//        }
//
//        public class Postpaser extends JsonPaser {
//            public String name;
//            @Override
//            public void inputArray(String str) {
//                try {
//                    //  jarray = new JSONArray(str);
//                    jarray = new JSONObject(str).getJSONArray("result");
//                    mMyData.clear();
//                    for(int i=0; i < jarray.length(); i++){
//                        post = new PostInfo(0,0,null);
//                        jobject = jarray.getJSONObject(i);  // JSONObject 추출
//                        //post.userimgpath = jobject.getString("userimgpath");
//                        //post.postimg = jobject.getString("postimg");
//                        post.postText = jobject.getString("text");
//                        post.username = jobject.getString("user_id");
//                        post.like = jobject.getString("like_num");
//                        post.hate = jobject.getString("hate_num");
//                        name = jobject.getString("user_id");
//                        mMyData.add(post);
//                    }
//                    reverse(mMyData);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            public String getName(){return name;}
//        }
//    }
}
