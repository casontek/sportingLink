package com.mycornership.betlink.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.NotificationAdapter;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.Notification;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar bar;
    AdView adView;
    NotificationAdapter adapter;
    List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        //shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification messages");
        //initialize components
        init();
        //load notifications
        notificationCheck();
        //loads banner
        loadBanner();
    }

    private void init(){
        recyclerView = findViewById(R.id.notification_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        adView = findViewById(R.id.adView2);

        bar = findViewById(R.id.progress);
        bar.setVisibility(View.GONE);
    }

    private void notificationCheck(){
        if(Amplify.Auth.getCurrentUser() != null){
            bar.setVisibility(View.VISIBLE);

            String username = Amplify.Auth.getCurrentUser().getUsername();
            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<List<Notification>> notCall = api.getNotification(username);
            notCall.enqueue(new Callback<List<Notification>>() {
                @Override
                public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                    bar.setVisibility(View.GONE);
                    if(response.isSuccessful()){
                        List<Notification> notificationList = response.body();
                        if(notificationList.size() > 0){
                            notifications = notificationList;
                            adapter = new NotificationAdapter(getBaseContext(), notifications);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Notification>> call, Throwable t) {
                    bar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "communication failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
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

}
