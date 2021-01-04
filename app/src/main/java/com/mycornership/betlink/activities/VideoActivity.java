package com.mycornership.betlink.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.VideoRelatedAdapter;
import com.mycornership.betlink.models.VideoResponse;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;
    private ProgressBar bar;
    private VideoView videoView;
    private MediaController controller;
    private String category, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        toolbar = findViewById(R.id.toolbar);
        //sets up the toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        //initialize components
        init();
        //initialize media controller
        controller = new MediaController(this);
        videoView.setMediaController(controller);
        //fetch Intent data
        intent = getIntent();
        category = intent.getStringExtra("category");
        id = intent.getStringExtra("id");
        //load current video
        currentVideo();
        //add listenner to the video view
        videoView.setOnPreparedListener(mp -> bar.setVisibility(View.GONE));
        videoView.setOnCompletionListener(mp -> bar.setVisibility(View.GONE));

    }

    private void init(){
        videoView = findViewById(R.id.video_view);
        bar = findViewById(R.id.loading_bar);

    }

    private void currentVideo(){
        String url = intent.getStringExtra("url");
        String ttl = intent.getStringExtra("title");
        String id = intent.getStringExtra("id");
        //load and start the clip play
        videoView.setVideoURI(Uri.parse(url));
        videoView.start();

    }

}
