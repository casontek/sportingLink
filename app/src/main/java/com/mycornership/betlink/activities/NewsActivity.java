package com.mycornership.betlink.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.RelatedNewsAdapter;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.FBResponse;
import com.mycornership.betlink.models.News;
import com.mycornership.betlink.models.NewsResponse;
import com.mycornership.betlink.models.User;
import com.mycornership.betlink.networks.FBApi;
import com.mycornership.betlink.networks.FBClient;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {
    private TextView tv_title, tv_body1, tv_body2, tv_date, tv_editor;
    private RecyclerView related_recycler;
    private RelatedNewsAdapter adapter;
    private ImageView news_photo;
    private ProgressBar bar;
    private View claim_view;
    private AdView adView2, adView0;
    private UnifiedNativeAdView nativeAdView;
    private FloatingActionButton fbPost;
    private NestedScrollView scrollView;
    private boolean isClaiming = false;
    List<Object> objects = new ArrayList<>();
    private String category, id, body, title, photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //initialize widgets
        init();
        //initialize firebase
        FirebaseApp.initializeApp(this);
        //display news information
        Intent intent = getIntent();
        if(intent.hasExtra("promoter"))
            loadNews(intent.getStringExtra("promoter"), intent.getStringExtra("content"));
        else
            displayInfo(intent);
        //load ads
        loadBanner();
        //load native ads
        loadNativeAds();
        //checks if user is Admin
        findUser();
        //listen to recycler view
        recyclerListener();
    }

    private void init() {
        //initialize components
        tv_title = findViewById(R.id.news_title);
        tv_body1 = findViewById(R.id.news_body1);
        tv_body2 = findViewById(R.id.news_body2);
        tv_date = findViewById(R.id.news_date);
        tv_editor = findViewById(R.id.news_editor);
        news_photo = findViewById(R.id.news_photo);
        adView2 = findViewById(R.id.adView2);
        adView0 = findViewById(R.id.adView0);
        nativeAdView = findViewById(R.id.nativeView);
        scrollView = findViewById(R.id.nested_scroll);
        claim_view = findViewById(R.id.claims);

        //initialise and sets up the related recyclerview
        related_recycler = findViewById(R.id.recycler_related);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new RelatedNewsAdapter(this, new ArrayList<>());
        related_recycler.setLayoutManager(manager);
        related_recycler.setAdapter(adapter);

        bar = findViewById(R.id.progress);
        bar.setVisibility(View.GONE);

        fbPost = findViewById(R.id.fb_post);
        fbPost.hide();
        fbPost.setOnClickListener(l ->{
            feedOnFacebook();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
           getMenuInflater().inflate(R.menu.news_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }
        else if(id == R.id.share_menu){
            bar.setVisibility(View.VISIBLE);
            //display share intent
            shareIntent();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareIntent() {
        createShortDynamicLink();
    }

    private void displayInfo(Intent b) {
        String ttl = b.getStringExtra("title");
        title = ttl;
        String body_in = b.getStringExtra("body");
        body = body_in;
        if(body_in != null) {
            if (body_in.length() < 501) {
                tv_body1.setText(body_in);
            }
            else {
                int x = body_in.length() / 2;
                String body1 = body_in.substring(0, body_in.lastIndexOf('.', x) + 1);
                String body2 = body_in.substring(body1.length(), body_in.length() - 1);
                tv_body1.setText(body1);
                tv_body2.setText(body2);
            }
        }
        String editor = "By " + b.getStringExtra("editor");
        String date = "Published on " + b.getStringExtra("date");
        category = b.getStringExtra("category");
        id = b.getStringExtra("news_id");
        String url = b.getStringExtra("url");
        photoUrl = url;
        //binds data to the UI
        tv_title.setText(ttl);
        tv_editor.setText(editor);
        tv_date.setText(date);
        //display the news image
        Glide.with(this).load(url).into(news_photo);

        //loads the related news
        relatedNews(category, id);
    }

    private void relatedNews(String cat, String ids){
        //loads if the category is not empty
        if(cat != null){
            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<NewsResponse> response = api.fetchRelatedNews(cat, ids);
            response.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    objects = new ArrayList<>();
                    if(response.isSuccessful())
                        if(response.body().getCode() == 200){
                            if(response.body().getData().size() > 0){
                                objects.add(response.body().getData());
                                adapter = new RelatedNewsAdapter(getBaseContext(), response.body().getData());
                                related_recycler.setAdapter(adapter);
                            }
                            else {
                                Log.d("news_", "empty news returned");
                            }
                        }
                }

                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    Log.d("news_", "error fetching news \n" + t.getMessage());
                }
            });
        }
        else
            Log.d("news_category", "empty category for the news");
    }

    private String getLongDynamicLink(){
        String username = "";
        if(Amplify.Auth.getCurrentUser() != null)
            username = Amplify.Auth.getCurrentUser().getUsername();

        String path = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDomainUriPrefix("https://sportinglinks.page.link")
                .setLink(Uri.parse("http://kasonmobile.com.ng/sportingNews?content=" + id + "&promoter=" + username))
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.mycornership.betlink").build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle(title).setDescription("sportingLink latest in sports")
                        .setImageUrl(Uri.parse(photoUrl))
                        .build())
                .buildDynamicLink().getUri().toString();

        Log.d("long_dynamic_link", path);

        return path;
    }

    private void createShortDynamicLink(){
        Task<ShortDynamicLink> shortLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(getLongDynamicLink()))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if(task.isSuccessful()){
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowLink = task.getResult().getPreviewLink();
                            //hides the progress
                            bar.setVisibility(View.GONE);

                            int x = body.length() / 3;
                            String msg;
                            if(body.length() >= 1000)
                                msg= body.substring(0, body.lastIndexOf(".", x) + 1) + '\n' + "Read More On " + shortLink.toString();
                            else
                                msg = body + '\n' + "Read More On " + shortLink.toString();

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
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
    }

    private void loadBanner(){
        AdRequest request = new AdRequest.Builder().build();
        adView2.loadAd(request);
        adView2.setAdListener(new AdListener(){
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

        AdRequest request2 = new AdRequest.Builder().build();
        adView0.loadAd(request2);
    }

    private void feedOnFacebook(){
        String app_link = "http://kasonmobile.com.ng/betlinksporting?content=" + id;
        int x =body.length() / 2;
        String body1 = body.substring(0, body.lastIndexOf('.', x) + 1);
        if(body1.length() >= 1200)
            body1 = body1.substring(0, 1000);
        String caption = title + '\n' + '\n' + body1;

        //show the progress bar
        bar.setVisibility(View.VISIBLE);

        FBApi api = FBClient.getInstance().create(FBApi.class);
        Call<FBResponse> postFB = api.postFBNoLink(photoUrl,caption, app_link, getString(R.string.access_token));
        postFB.enqueue(new Callback<FBResponse>() {
            @Override
            public void onResponse(Call<FBResponse> call, Response<FBResponse> response) {
                //hides progress bar
                bar.setVisibility(View.GONE);

                Toast.makeText(getBaseContext(), "facebook posted", Toast.LENGTH_SHORT).show();
                Log.d("responce_data","id: " + response.body().getId() + " post id: "+ response.body().getPostId());
            }

            @Override
            public void onFailure(Call<FBResponse> call, Throwable t) {
                //hides progress bar
                bar.setVisibility(View.GONE);

                Toast.makeText(getBaseContext(), "error posting on page.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findUser() {
        if (Amplify.Auth.getCurrentUser() != null) {
            String user = Amplify.Auth.getCurrentUser().getUsername();

            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<User> userCall = api.getUser(user);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        User user1 = response.body();
                        if(user1 != null){
                            if(user1.getRole().equals("ADMIN"))
                                fbPost.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                }
            });
        }
    }

    private void loadNativeAds(){
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.ads_native_units))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        //shows the native ads placeholder
                        nativeAdView.setVisibility(View.VISIBLE);

                        // Show the ad.
                        populateView(unifiedNativeAd, nativeAdView);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        // notifyAdsAction();
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
        adLoader.loadAds(new AdRequest.Builder().build(), 1);
    }

    private void populateView(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView nativeAdView) {
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setMediaView(nativeAdView.findViewById(R.id.ad_media));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));

        if(unifiedNativeAd.getBody() == null){
            nativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        }
        else {
            nativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView)nativeAdView.getBodyView()).setText(unifiedNativeAd.getBody());
        }

        if(unifiedNativeAd.getCallToAction() == null){
            nativeAdView.getCallToActionView().setVisibility(View.GONE);
        }
        else {
            nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView)nativeAdView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
        }

        if(unifiedNativeAd.getAdvertiser() == null){
            nativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        }
        else {
            nativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
            ((TextView)nativeAdView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
        }

        nativeAdView.setNativeAd(unifiedNativeAd);
    }

    private void loadNews(String promoter, String content){
        //shows loading bar
        bar.setVisibility(View.VISIBLE);
        String inv = UUID.randomUUID().toString();
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<News> newsCall = api.getNewsItem(content, promoter, inv);
        newsCall.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                //hides loading bar
                bar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    News newsItem = response.body();
                    if(newsItem != null){
                        //display info
                        showNewsItem(newsItem);
                    }
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                //hides loading bar
                bar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "error getting data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNewsItem(News newsItem) {
        String ttl = newsItem.getTitle();
        title = ttl;
        String body_in = newsItem.getDetail();
        body = body_in;
        if(body_in != null) {
            if (body_in.length() < 501) {
                tv_body1.setText(body_in);
            }
            else {
                int x = body_in.length() / 2;
                String body1 = body_in.substring(0, body_in.lastIndexOf('.', x) + 1);
                String body2 = body_in.substring(body1.length(), body_in.length() - 1);
                tv_body1.setText(body1);
                tv_body2.setText(body2);
            }
        }
        String editor = "By " + newsItem.getEditor();
        String date = "Published on " + newsItem.getEventDate();
        category = newsItem.getCategory();
        id = newsItem.getId() + "";
        String url = newsItem.getMediaUrl();
        photoUrl = url;
        //binds data to the UI
        tv_title.setText(ttl);
        tv_editor.setText(editor);
        tv_date.setText(date);
        //display the news image
        Glide.with(this).load(url).into(news_photo);

        //loads the related news
        relatedNews(category, id);
    }

    private void recyclerListener(){
        //listen for the scrollview
        final Rect rectBounds = new Rect();
        scrollView.getHitRect(rectBounds);
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(claim_view != null){
                if(claim_view.getLocalVisibleRect(rectBounds)){
                    if(!claim_view.getLocalVisibleRect(rectBounds) || rectBounds.height() < claim_view.getHeight()){
                        //partially visible
                        if(!isClaiming) {
                            creditUser();
                        }
                        isClaiming = true;
                    }
                    else {
                        //completely visible
                        if(!isClaiming) {
                            creditUser();
                        }
                        isClaiming = true;
                    }
                }
                else{
                    //not visible
                }
            }
        });
    }

    private void creditUser(){
        if(Amplify.Auth.getCurrentUser() != null){
            String username = Amplify.Auth.getCurrentUser().getUsername();
            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<DataResponse> responseCall = api.creditUser(username, id);
            responseCall.enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    if(response.isSuccessful()){
                        if(response.body().getCode() == 200)
                            Toast.makeText(getBaseContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {

                }
            });
        }
    }

}
