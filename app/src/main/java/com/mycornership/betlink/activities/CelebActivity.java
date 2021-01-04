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
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.StickerAdapter;
import com.mycornership.betlink.models.BirthdaySticker;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CelebActivity extends AppCompatActivity {
    TextView tv_note, tv_bio, tv_ach, tv_achievement;
    ImageView img_photo, img_photo2;
    VideoView videoView;
    Toolbar toolbar;
    Intent intent;
    ProgressBar bar;
    AdView adView, adView2;
    FloatingActionButton share_btn;
    MediaController controller;
    private String photoUrl, name, note1;
    List<BirthdaySticker> birthdayStickers = new ArrayList<>();
    RecyclerView recyclerView;
    StickerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celeb);
        toolbar = findViewById(R.id.toolbar);
        controller = new MediaController(this);
        //sets up the toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        //initialize components
        init();
        //fetch Intent data
        intent = getIntent();
        getSupportActionBar().setTitle("Happy birthday " + intent.getStringExtra("name") + " from sportingLink");
        //binds UI data
        bindUI();
        //load ads
        loadAds();
        //load people that celebrated him/her
        loadPeople();

    }

    private void init(){
        tv_note = findViewById(R.id.note_view);
        tv_bio = findViewById(R.id.bio_view);
        tv_achievement = findViewById(R.id.achievements);
        videoView = findViewById(R.id.video_view);

        img_photo = findViewById(R.id.photo_view);
        img_photo2 = findViewById(R.id.photo_view_2);

        adView = findViewById(R.id.adView);
        adView2 = findViewById(R.id.adView2);

        bar = findViewById(R.id.progress);
        bar.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.people_recycler);
        adapter = new StickerAdapter(this, birthdayStickers);
        recyclerView.setAdapter(adapter);

        share_btn = findViewById(R.id.btn_share);
        share_btn.setOnClickListener(v ->{
            generateLinks();
        });
    }

    private void bindUI() {
        String note = intent.getStringExtra("note");
        note1 = note;
        String bio = intent.getStringExtra("bio");
        String photo = intent.getStringExtra("photo");
        photoUrl = photo;
        String photo2 = intent.getStringExtra("photo2");
        String video = intent.getStringExtra("video");
        String nm = intent.getStringExtra("name");
        name = nm;
        String achievements = intent.getStringExtra("achievements");

        tv_bio.setText(bio);
        tv_note.setText(note);
        tv_achievement.setText(achievements);
        //display the image and video
        Glide.with(this).load(photo).into(img_photo);
        Glide.with(this).load(photo2).into(img_photo2);

        videoView.setMediaController(controller);
        videoView.setVideoURI(Uri.parse(video));
    }

    private void generateLinks(){
        //show progress bar
        bar.setVisibility(View.VISIBLE);
        createShortDynamicLink();
    }

    private String getLongDynamicLink(){
        String username = "";
        if(Amplify.Auth.getCurrentUser() != null)
            username = Amplify.Auth.getCurrentUser().getUsername();

        String title = name + " birthday Celebration @sportingLink";

        String path = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDomainUriPrefix("https://sportinglinks.page.link")
                .setLink(Uri.parse("http://kasonmobile.com.ng/birthday?promoter=" + username))
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.mycornership.betlink").build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle(title).setDescription("Join us and Celebrate " + name)
                        .setImageUrl(Uri.parse(photoUrl))
                        .build())
                .buildDynamicLink().getUri().toString();

        return path;
    }

    private void createShortDynamicLink(){
        Task<ShortDynamicLink> shortLinkGen = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(getLongDynamicLink()))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        Uri shortLink = task.getResult().getShortLink();
                        //hides the progress
                        bar.setVisibility(View.GONE);

                        String msg = "";
                        if(note1 != null) {
                            if(note1.length() > 400)
                                msg = note1.substring(0, note1.indexOf('.', 400)) + '\n' + "Read More On " + shortLink.toString();
                            else
                                msg = note1  + '\n' + "Read More On " + shortLink.toString();
                        }
                        //share the short link generated
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_TEXT, msg);
                        startActivity(i);
                    }
                    else{
                        //hides the progress
                        bar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Short Link not generated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAds(){
        AdRequest request = new AdRequest.Builder().build();
        adView2.loadAd(request);
        AdRequest request2 = new AdRequest.Builder().build();
        adView.loadAd(request2);
    }

    private void loadPeople() {
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<List<BirthdaySticker>> call = api.celebStickers(name);
        call.enqueue(new Callback<List<BirthdaySticker>>() {
            @Override
            public void onResponse(Call<List<BirthdaySticker>> call, Response<List<BirthdaySticker>> response) {
                Log.d("resp_", response.toString());
                if(response.isSuccessful()){
                    if(response.body().size() > 0){
                        //display people that have liked or loved the celebrant
                        adapter = new StickerAdapter(getBaseContext(), response.body());
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BirthdaySticker>> call, Throwable t) {

            }
        });

    }

}
