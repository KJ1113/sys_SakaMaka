package com.example.leehangeol.sakamaka.Main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.leehangeol.sakamaka.ARactivity;
import com.example.leehangeol.sakamaka.NetInformTransfer;
import com.example.leehangeol.sakamaka.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentPostWrite extends Fragment {

    private final int REQ_CODE_SELECT_IMAGE = 1001;

    MainActivity activity;


    private final String IMG_FILE_PATH = "imgfilepath";
    private final String IMG_TITLE = "imgtitle";
    private final String IMG_ORIENTATION = "imgorientation";

    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOrient = null;

    private String image_id;


    FileInputStream fileInputStream ;

    private ImageButton imgadd;
    private ImageView img;
    private TextView username;
    private Button cancle;
    private Button postadd;
    private EditText posttext;
    Date date;
    int count=0;

    String userId;
    String path;

    NetInformTransfer netInformTransfer= new NetInformTransfer();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postwrite, container, false);

        imgadd = (ImageButton) view.findViewById(R.id.imgadd);

        AQuery aQuery = new AQuery(getActivity());

        username = (TextView)view.findViewById(R.id.username);
        img = (ImageView)view.findViewById(R.id.userimg);
        cancle = (Button)view.findViewById(R.id.cancle);
        postadd = (Button)view.findViewById(R.id.add);
        posttext = (EditText)view.findViewById(R.id.posttext);
        String userId = getArguments().getString("name");
        String path = getArguments().getString("img");

        username.setText(userId);
        aQuery.id(img).image(path);
        img.setBackground(new ShapeDrawable(new OvalShape()));
        img.setClipToOutline(true);

        imgadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGallery();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.callFragment(2);
            }
        });

        postadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분");
                SimpleDateFormat d = new SimpleDateFormat("yyyyMMddhhmmssSSS");
                date = new Date();
                String today = df.format(date);
                String id = d.format(date);
                netInformTransfer.removeAllvalue();
                netInformTransfer.setBodyinform(path);
                netInformTransfer.setBodyinform(posttext.getText().toString());
                netInformTransfer.setBodyinform(username.getText().toString());
                netInformTransfer.setBodyinform(id);
                netInformTransfer.setBodyinform(today);
                new PosTextinfoTask().execute(netInformTransfer.getServerUrl()+"/uptext");
                posttext.setText("");
                mImgTitle="";

                activity.callFragment(2);
            }
        });

        return view;
    }

    private void getGallery()
    {
        Intent intent = null;

        // 안드로이드 KitKat(level 19)부터는 ACTION_PICK 이용
        if(Build.VERSION.SDK_INT >= 19)
        {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        else
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 선택된 사진을 받아 서버에 업로드한다.
        if (requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Uri uri = data.getData();
                getImageNameToUri(uri);

                try
                {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    imgadd.setImageBitmap(bm);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * URI 정보를 이용하여 사진 정보 가져옴
     */
    private void getImageNameToUri(Uri data) {
        String[] proj = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION
        };

        Cursor cursor = getActivity().getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        int column_orientation = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);


        mImgPath = cursor.getString(column_data);
        mImgTitle = cursor.getString(column_title);
        mImgOrient = cursor.getString(column_orientation);


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

    private String getPostDataString(HashMap<String, String> params) {
        boolean isFirst = true;
        StringBuilder result = new StringBuilder();

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (true == isFirst) {
                    isFirst = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }


    public class PosTextinfoTask extends AsyncTask<String, String, String> {
        String line = "error";
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("title", netInformTransfer.getindexTovalue(0));
                jsonObject.accumulate("text", netInformTransfer.getindexTovalue(1));
                jsonObject.accumulate("user_id", netInformTransfer.getindexTovalue(2));
                jsonObject.accumulate("picture_id", netInformTransfer.getindexTovalue(3));
                jsonObject.accumulate("write_time", netInformTransfer.getindexTovalue(4));
                HttpURLConnection con = null;
                BufferedReader reader = null;
                try{
                    //URL url = new URL(“http://192.168.25.16:3000/users“);
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();
                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            new HttpRequestAsyncTask().execute(mImgPath, mImgTitle, mImgOrient);

        }
    }


    private int uploadImageInfo(HashMap<String, String> aParams) {

        final String SERVER_URL = "http://114.129.198.105:3000/upimage";
        int result = 0;

        String line = null;
        URL url = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        HttpURLConnection conn = null;

        try {

            // URL 객체를생성한다.
            fileInputStream = new FileInputStream(mImgPath);
            url = new URL(SERVER_URL);
            conn = (HttpURLConnection) url.openConnection();
            //읽기와 쓰기 모두 가능하게 설정
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //캐시를 사용하지 않게 설정
            conn.setUseCaches(false);

            //POST타입으로 설정
            conn.setRequestMethod("POST");

            //헤더 설정
            conn.setRequestProperty("Connection","Keep-Alive");
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

            //Output스트림을 열어
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data;filename=\""+netInformTransfer.getindexTovalue(3)+"\""+"\r\n");
            dos.writeBytes("\r\n");

            //버퍼사이즈를 설정하여 buffer할당
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            //스트림에 작성
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                // Upload file part(s)
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes("\r\n");
            dos.writeBytes("--" + boundary + "--" + "\r\n");
            fileInputStream.close();
            //써진 버퍼를 stream에 출력.
            dos.flush();
            // 서버요청에 대한 응답 코드를 받는다.
            int responseCode = conn.getResponseCode();
            // 200 ~ 299는 성공이다. 나머지는 에러를 리턴한다.
            if (responseCode >= 200 && responseCode < 300) {
                // 응답정보를InputStream에서 String 으로변경한다.
                InputStream in = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(in));
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }

                rd.close();
                in.close();

                if (null != response && 0 != response.length()) {
                    result = 0; // 정보전송성공

                }
            } else {
                result = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        return result;
    }

    public class HttpRequestAsyncTask extends AsyncTask<String, Integer, Integer> {
        private String mImagePath = null;
        private String mImageTitle = null;
        private String mImageOrientation = null;
        private ProgressDialog mWaitDlg = null;
        /**
         * 작업을 시작하기 전에 필요한 UI를 화면에 보여주도록 한다
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        /*
         * 서버 요청 작업을 진행한다.
         */
        @Override
        protected Integer doInBackground(String... arg) {
            mImagePath = arg[0];
            mImageTitle = arg[1];
            mImageOrientation = arg[2];

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(IMG_FILE_PATH, mImagePath);
            params.put(IMG_TITLE, mImageTitle);
            params.put(IMG_ORIENTATION, mImageOrientation);

            int result = uploadImageInfo(params);
            return result;
        }
        /**
         * 서버 작업 진행 중에 UI를 갱신할 필요가 있는 경우 호출 되어진다.
         * doInBackground에서 publicshProgress()를 호출하면 invoked 된다.
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }
        /**
         * 서버 작업 완료 후 화면에 필요한 UI를 보여주도록 한다.
         */
        @Override
        protected void onPostExecute(Integer aResult) {
            super.onPostExecute(aResult);
        }
    }

}