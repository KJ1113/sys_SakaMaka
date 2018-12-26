package com.example.leehangeol.sakamaka;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.androidquery.AQuery;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;


public class PullImageActivity extends AppCompatActivity {

    private PhotoView photo_view;
    PhotoViewAttacher attacher;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_image);

        photo_view = (PhotoView)findViewById(R.id.photo_view);

        Intent intent = getIntent();
        AQuery aQuery = new AQuery(this);
        aQuery.id(R.id.photo_view).image("http://114.129.198.105:3000/image/"+intent.getStringExtra("pullImg"));
        attacher = new PhotoViewAttacher(photo_view);

    }
}
