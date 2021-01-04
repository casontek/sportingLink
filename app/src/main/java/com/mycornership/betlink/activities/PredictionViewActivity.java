package com.mycornership.betlink.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.InsideTipAdapter;
import com.mycornership.betlink.models.Prediction;
import com.mycornership.betlink.models.TipsResponse;
import com.mycornership.betlink.models.User;
import com.mycornership.betlink.networks.FBApi;
import com.mycornership.betlink.networks.FBClient;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView profile_dp;
    private ImageView hIcon, aIcon;
    private TextView tv_date, tv_time_score, tv_hteam, tv_ateam,
            tv_tip, tv_joined, tv_total, tv_level, mr;
    private View status;
    private CardView bitmap_view;
    private RecyclerView recyclerView;
    private InsideTipAdapter adapter;
    private AdView adView;
    private AdLoader adLoader;
    private List<Object> objectList = new ArrayList<>();
    private ProgressBar bar;
    private Intent intent;
    private String poster, category, photo;
    private long id;
    private View vw;
    private int number = 1, inc = 1;
    private FloatingActionButton fbPost;
    private String teamH, teamA, hUrl, aUrl, match_date, match_time, tip, tip_status, score, match_status;
    private static final String BASE_URL_IMG = "https://kasonbetlink-res-files-prod.s3.amazonaws.com/public/facebook/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_view);
        toolbar = findViewById(R.id.toolbar_bet);
        //sets up the toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //initialize components
        init();
        //fetch Intent data
        intent = getIntent();
        poster = intent.getStringExtra("poster");
        id = intent.getLongExtra("id", 1);
        getSupportActionBar().setTitle(poster);
        photo = intent.getStringExtra("photo");
        Glide.with(this).load(photo).placeholder(R.drawable.profile_pix).into(profile_dp);
        //bind the UI with the current info
        bindUI(intent);
        //gets poster detail
        posterInfo();
        //load other tips by user
        loadTips();
        //load banner
        loadBanner();
        //load native adds
        loadAds();
        //find the user role
        findUser();
    }

    private void init(){
        //initializes the UI components
        hIcon = findViewById(R.id.home_icon);
        aIcon = findViewById(R.id.away_icon);
        profile_dp = findViewById(R.id.my_profile_px);
        status = findViewById(R.id.prd_status);

        mr = findViewById(R.id.previous_prd);

        tv_ateam = findViewById(R.id.a_team);
        tv_hteam = findViewById(R.id.h_team);
        tv_date = findViewById(R.id.match_date);
        tv_time_score = findViewById(R.id.match_time);
        tv_tip = findViewById(R.id.match_tip_current);

        bar = findViewById(R.id.prediction_detail_bar);
        bar.setVisibility(View.GONE);

        tv_joined = findViewById(R.id.user_joined);
        tv_level = findViewById(R.id.user_tag);
        tv_total = findViewById(R.id.user_post);

        recyclerView = findViewById(R.id.recycler_tip_user);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new InsideTipAdapter(getBaseContext(), objectList);
        recyclerView.setAdapter(adapter);

        //initializes the ad view
        adView = findViewById(R.id.adView);
        //capture view
        vw = findViewById(R.id.capture);

        fbPost = findViewById(R.id.fb_post);
        fbPost.hide();
        //add listener to the facebook post button
        fbPost.setOnClickListener(l ->{
            initTransfer();
            postOnFBPage();
        });
    }

    private void loadTips(){
        //shows progress bar
        bar.setVisibility(View.VISIBLE);
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<TipsResponse> tipsCall = api.fetchTipsByUser(category, poster, id);
        tipsCall.enqueue(new Callback<TipsResponse>() {
            @Override
            public void onResponse(Call<TipsResponse> call, Response<TipsResponse> response) {
                //hides the progress bar
                bar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.body().getCode() == 200){
                        //read the user prediction data from the response
                        List<Prediction> predictions = response.body().getData();
                        if(predictions.size() > 0){
                            objectList.addAll(predictions);
                            adapter = new InsideTipAdapter(getBaseContext(), objectList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<TipsResponse> call, Throwable t) {
                //hides the progress bar
                bar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "fetching data failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindUI(Intent i){
        //gets the intent data passed
        teamH = i.getStringExtra("homeTeam");
        teamA = i.getStringExtra("awayTeam");
        hUrl  = i.getStringExtra("homeLogo");
        aUrl  = i.getStringExtra("awayLogo");
        match_date = i.getStringExtra("matchDate");
        match_time = i.getStringExtra("matchTime");
        tip_status = i.getStringExtra("status");
        score = i.getStringExtra("score");
        tip = i.getStringExtra("tip");
        category = i.getStringExtra("category");
        match_status = i.getStringExtra("matchStatus");

        tv_ateam.setText(teamA);
        tv_hteam.setText(teamH);
        tv_date.setText(match_date.substring(0, 10));
        tv_tip.setText(tip);
        if(match_status.equals("Match Finished"))
            tv_time_score.setText(score);
        else
            tv_time_score.setText(getMatchTime(Long.parseLong(match_time)));
        //displays the teams logos
        Glide.with(this).load(hUrl).into(hIcon);
        Glide.with(this).load(aUrl).into(aIcon);

        if(tip_status.equals("won")){
            //sets the label to win
            status.setBackground(getDrawable(R.drawable.ic_done));
        }
        else if(tip_status.equals("lost")){
            //sets the label to lose
            status.setBackground(getDrawable(R.drawable.lost));
        }
        else{
            //set it to pending
            status.setBackground(getDrawable(R.drawable.pending010));
        }
    }

    private String getMatchTime(long unix) {
        Date date = new Date(unix * 1000L);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setTimeZone(TimeZone.getDefault());
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        String hour,minutes;
        if(h < 10)
            hour = "0" + h;
        else
            hour = h + "";

        if(m < 10)
            minutes = "0" + m;
        else
            minutes = m + "";

        return hour + ":" + minutes;
    }

    private void posterInfo(){
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<User> userCall = api.getUser(poster);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = response.body();
                    if(user != null){
                        tv_level.setText("TAG: " +user.getLevel());
                        tv_joined.setText("Joined " +user.getJoined());
                        tv_total.setText("Total Post: " + user.getPost());

                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("poster_error", t.getMessage());
                Toast.makeText(getBaseContext(), "data error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBanner(){
        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdClicked() {
                //notifyAdsAction();
            }
        });
    }

    private void initTransfer(){
        TransferNetworkLossHandler.getInstance(this);
    }

    private void postOnFBPage(){
        Bitmap bitmap = takescreenshot(vw);

        FileOutputStream out;
        try {
            out = new FileOutputStream("/storage/emulated/0/betlink_info.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            //read the saved file
            File f = new File("/storage/emulated/0/betlink_info.png");
            String path = getFilePath().trim();
            //upload to aws server
            s3FileUpload(f, path);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getFilePath(){
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        int h = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);

        return "betlink-sporting_" + m + "" + d + "" + h + "" + min + "" + sec + "_tip-shot.png";
    }

    private Bitmap takescreenshot(View v){
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        return b;
    }

    private void s3FileUpload(File f, final String path){
        //shows progress bar
        bar.setVisibility(View.VISIBLE);

        TransferUtility transferUtility = TransferUtility.builder()
                .context(this)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                .build();

        TransferObserver observer = transferUtility.upload("public/facebook/" + path, f);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if(state.equals(TransferState.COMPLETED)){
                    String url = BASE_URL_IMG + path;
                    makeFBPost(url);
                }
                else if(state.equals(TransferState.IN_PROGRESS)){

                }
                else {
                    //turn off the visibility of the progress bar
                    //bar.setVisibility(View.GONE);
                    //notify error
                    Log.d("error_upload", "error uploading image: "+ state.toString());
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d("upload_file", "image file upload progress changed.");
            }

            @Override
            public void onError(int id, Exception ex) {
                bar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "post failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeFBPost(String url) {
        String app_link = "kasonmobile.com.ng/betlink";

        String msg = teamH + " Vs " + teamA + '\n' + "#" + poster;
        FBApi api = FBClient.getInstance().create(FBApi.class);
        Call<ResponseBody> postCall = api.postFBPagePhoto(url, msg, getString(R.string.access_token));
        postCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //hides the progress bar
                bar.setVisibility(View.GONE);

                Toast.makeText(getBaseContext(), "facebook posted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //hides the progress bar
                bar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "error posting on facebook. " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findUser(){
        if(Amplify.Auth.getCurrentUser() != null) {
            String username = Amplify.Auth.getCurrentUser().getUsername();

            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<User> userCall = api.getUser(username);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        User user = response.body();
                        if (user != null) {
                            if(user.getRole().equals("ADMIN")){
                                fbPost.show();
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                }
            });
        }
    }

    private void loadAds(){
        adLoader = new AdLoader.Builder(getBaseContext(), getString(R.string.ads_native_units))
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    if(number == 1 && objectList.size() >= 3){
                        objectList.add(3, unifiedNativeAd);
                        adapter.notifyItemInserted(3);

                        number += 1;
                    }
                    else if(number == 2 && objectList.size() >= 8){
                        objectList.add(8, unifiedNativeAd);
                        adapter.notifyItemInserted(8);

                        number += 1;
                    }
                    else if(number == 3 && objectList.size() >= 15){
                        objectList.add(15, unifiedNativeAd);
                        adapter.notifyItemInserted(15);

                        number += 1;
                    }
                    else if(number == 4 && objectList.size() >= 19){
                        objectList.add(19, unifiedNativeAd);
                        adapter.notifyItemInserted(19);

                        number += 1;
                    }
                    else if(number == 5 && objectList.size() >= 27){
                        objectList.add(27, unifiedNativeAd);
                        adapter.notifyItemInserted(27);

                        number += 1;
                    }
                    else if(number == 6 && objectList.size() >= 35){
                        objectList.add(35, unifiedNativeAd);
                        adapter.notifyItemInserted(35);

                        number += 1;
                    }
                    else if(number == 7 && objectList.size() >= 43){
                        objectList.add(43, unifiedNativeAd);
                        adapter.notifyItemInserted(43);

                        number += 1;
                    }

                    inc += 1;
                    Log.d("ads_loaded","ads successfully loaded. " );
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();
                       // notifyAdsAction("LeftApplication");
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        //notifyAdsAction("Clicked");
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                        Log.d("ads_load_error", "error loading ads. " + errorCode);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAds(new AdRequest.Builder().build(), 3);
    }

}
