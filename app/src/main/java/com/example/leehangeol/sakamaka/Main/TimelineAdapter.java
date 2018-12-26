package com.example.leehangeol.sakamaka.Main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.leehangeol.sakamaka.ARactivity;
import com.example.leehangeol.sakamaka.HttpConnect;
import com.example.leehangeol.sakamaka.NetInformTransfer;
import com.example.leehangeol.sakamaka.PullImageActivity;
import com.example.leehangeol.sakamaka.R;
import java.util.ArrayList;
import java.util.Arrays;

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    AQuery aQuery;

    boolean likecheck[];
    boolean hatecheck[];

    String currentUser;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView userimg;
        ImageView postimg;
        TextView username;
        TextView posttext;
        TextView like;
        TextView hate;
        Button likebtn;
        Button hatebtn;
        ImageView likeimg;
        ImageView hateimg;
        TextView date;

        MyViewHolder(View view){
            super(view);
            userimg = (ImageView)view.findViewById(R.id.userimg);
            postimg = (ImageView)view.findViewById(R.id.postimg);
            username = (TextView)view.findViewById(R.id.username);
            posttext = (TextView)view.findViewById(R.id.posttext);
            like = (TextView)view.findViewById(R.id.like);
            hate = (TextView)view.findViewById(R.id.hate);
            likebtn = (Button) view.findViewById(R.id.likebtn);
            hatebtn = (Button) view.findViewById(R.id.hatebtn);
            likeimg = (ImageView)view.findViewById(R.id.likeimg);
            hateimg = (ImageView)view.findViewById(R.id.hateimg);
            date = (TextView)view.findViewById(R.id.date);
        }
    }

    private ArrayList<PostInfo> postInfoArrayList;

    TimelineAdapter(ArrayList<PostInfo> postInfoArrayList,String currentUser, boolean likecheck[], boolean hatecheck[]){//OnItemClickListener onItemClickListener){//, String currentUser){
        this.postInfoArrayList = postInfoArrayList;
        this.currentUser = currentUser;
        this.likecheck = likecheck;
        this.hatecheck = hatecheck;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_timeline, parent, false);
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

        if(likecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)]) { myViewHolder.likeimg.setImageResource(R.drawable.like1); }
        else { myViewHolder.likeimg.setImageResource(R.drawable.like2); }
        if(hatecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)]) { myViewHolder.hateimg.setImageResource(R.drawable.hate1); }
        else { myViewHolder.hateimg.setImageResource(R.drawable.hate2); }

        ((MyViewHolder) holder).likebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Liketask conn = new Liketask();

                if(likecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)])
                {
                    //conn.execute();
                    conn.execute("http://114.129.198.105:3000/removelike?no=" + postInfoArrayList.get(position).postNo + "&like_user_id=" + currentUser);
                    myViewHolder.like.setText(String.valueOf(Integer.parseInt(myViewHolder.like.getText().toString()) - 1));
                    myViewHolder.likeimg.setImageResource(R.drawable.like2);
                    likecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)] = false;
                }
                else if(!likecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)] && !hatecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)]){
                    conn.execute("http://114.129.198.105:3000/likeadd?no=" + postInfoArrayList.get(position).postNo + "&like_user_id=" + currentUser);
                    myViewHolder.like.setText(String.valueOf(Integer.parseInt(myViewHolder.like.getText().toString()) + 1));
                    likecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)] = true;
                    myViewHolder.likeimg.setImageResource(R.drawable.like1);
                }
            }
        });

        ((MyViewHolder) holder).hatebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Liketask conn = new Liketask();
                if(hatecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)])
                {
                    //conn.execute();
                    conn.execute("http://114.129.198.105:3000/removehate?no=" + postInfoArrayList.get(position).postNo + "&hate_user_id=" + currentUser);
                    myViewHolder.hate.setText(String.valueOf(Integer.parseInt(myViewHolder.hate.getText().toString()) - 1));
                    myViewHolder.hateimg.setImageResource(R.drawable.hate2);
                    hatecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)] = false;
                }
                else if(!likecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)] && !hatecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)]){
                    conn.execute("http://114.129.198.105:3000/hateadd?no=" + postInfoArrayList.get(position).postNo + "&hate_user_id=" + currentUser);
                    myViewHolder.hate.setText(String.valueOf(Integer.parseInt(myViewHolder.hate.getText().toString()) + 1));
                    myViewHolder.hateimg.setImageResource(R.drawable.hate1);
                    hatecheck[Integer.parseInt(postInfoArrayList.get(position).postNo)] = true;
                }
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