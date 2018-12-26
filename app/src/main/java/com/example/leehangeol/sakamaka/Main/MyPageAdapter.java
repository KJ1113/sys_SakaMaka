package com.example.leehangeol.sakamaka.Main;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.leehangeol.sakamaka.HttpConnect;
import com.example.leehangeol.sakamaka.PullImageActivity;
import com.example.leehangeol.sakamaka.R;
import java.util.ArrayList;



public class MyPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    MainActivity activity;

    AQuery aQuery;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userimg;
        ImageView postimg;
        TextView username;
        TextView posttext;
        TextView like;
        TextView hate;
        TextView date;
        Button popup;

        MyViewHolder(View view) {
            super(view);
            userimg = (ImageView)view.findViewById(R.id.userimg);
            postimg = (ImageView)view.findViewById(R.id.postimg);
            username = (TextView)view.findViewById(R.id.username);
            posttext = (TextView)view.findViewById(R.id.posttext);
            like = (TextView)view.findViewById(R.id.like);
            hate = (TextView)view.findViewById(R.id.hate);
            date = (TextView)view.findViewById(R.id.date);
            popup = (Button)view.findViewById(R.id.popup);

        }

    }

    private ArrayList<PostInfo> postInfoArrayList;

    MyPageAdapter(ArrayList<PostInfo> postInfoArrayList){
        this.postInfoArrayList = postInfoArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_mypage, parent, false);

        aQuery = new AQuery(v.getContext());

        return new MyViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        aQuery.id(((MyViewHolder) holder).userimg).image(postInfoArrayList.get(position).userimgpath);
        aQuery.id(((MyViewHolder)holder).postimg).image("http://114.129.198.105:3000/image/"+postInfoArrayList.get(position).postimgpath);
        myViewHolder.userimg.setBackground(new ShapeDrawable(new OvalShape()));
        myViewHolder.userimg.setClipToOutline(true);
        myViewHolder.username.setText(postInfoArrayList.get(position).username);
        myViewHolder.posttext.setText(postInfoArrayList.get(position).postText);
        myViewHolder.like.setText(postInfoArrayList.get(position).like);
        myViewHolder.hate.setText(postInfoArrayList.get(position).hate);
        myViewHolder.date.setText(postInfoArrayList.get(position).date);



        myViewHolder.popup.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(
                        v.getContext(), // 현재 화면의 제어권자
                        v); // anchor : 팝업을 띄울 기준될 위젯
                p.getMenuInflater().inflate(R.menu.menu_item, p.getMenu());
                // 이벤트 처리
                p.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) item -> {
                    if(item.getTitle().equals("게시글 수정"))
                    {
                        activity = (MainActivity) v.getContext();
                        activity.update(postInfoArrayList.get(position).postNo, postInfoArrayList.get(position).postText, postInfoArrayList.get(position).postimgpath);
                    }
                    else
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                        alertDialogBuilder.setTitle("게시글 삭제");
                        alertDialogBuilder
                                .setMessage("현재 게시글을 삭제하시겠습니까?")
                                .setCancelable(false)
                                .setPositiveButton("삭제",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // 프로그램을 종료한다3
                                                Liketask conn = new Liketask();
                                                conn.execute("http://114.129.198.105:3000/removepost?no=" + postInfoArrayList.get(position).postNo);
                                                activity = (MainActivity) v.getContext();
                                                activity.repflahFragment2();
                                            }
                                        }).setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // 다이얼로그 보여주기
                        alertDialog.show();
                    }

                    return false;
                });
                p.show(); // 메뉴를 띄우기
            }
        });


        ((MyViewHolder) holder).postimg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PullImageActivity.class);
                intent.putExtra("pullImg",postInfoArrayList.get(position).postimgpath);
                v.getContext().startActivity(intent);
            }
        });

    }

    public class Liketask extends HttpConnect {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public int getItemCount() {
        return postInfoArrayList.size();
    }


}