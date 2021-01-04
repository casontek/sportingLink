package com.mycornership.betlink.fragments.tips;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.AuthActivity;
import com.mycornership.betlink.activities.NavHomeActivity;
import com.mycornership.betlink.activities.SubscriptionActivity;
import com.mycornership.betlink.adapters.TipsAdapter;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.Prediction;
import com.mycornership.betlink.models.Subscription;
import com.mycornership.betlink.models.TipsResponse;
import com.mycornership.betlink.networks.NetworkAvailability;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SponsoredTips extends Fragment {
    private RecyclerView recyclerView;
    private TipsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<Object> objects = new ArrayList<>();
    private List<Prediction> tips = new ArrayList<>();
    private ProgressBar loadinBar;
    private int page = 0;
    private boolean isLoading = true;
    boolean subscribed = false;
    private String user = "";

    public SponsoredTips() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sponsored_tips, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
        //confirm user before loading
        confirmUser();
        //add listener to the recycler view
        recyclerScrollListener();
    }

    private void init() {
        loadinBar = getView().findViewById(R.id.loading_bar);
        loadinBar.setVisibility(View.GONE);
        //initialize the recyclerView
        recyclerView = getView().findViewById(R.id.tips_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new TipsAdapter(getContext(), objects, "premium");
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        refreshLayout = getView().findViewById(R.id.refresh);
        refreshLayout.setDistanceToTriggerSync(100);
        refreshLayout.setOnRefreshListener( () ->{
            if(!TextUtils.isEmpty(user)) {
                if(!isLoading) {
                    isLoading = true;
                    page = 0;
                    objects = new ArrayList<>();
                    //load news items
                    if(subscribed)
                        loadTips(page);
                }
                refreshLayout.setRefreshing(false);
            }

        });
    }

    private void loadTips(int page) {
        if(NetworkAvailability.isConnected(getContext())){
            //load basic tips
            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<TipsResponse> tipsCall = api.fetchTips("premium", page);
            tipsCall.enqueue(new Callback<TipsResponse>() {
                @Override
                public void onResponse(Call<TipsResponse> call, Response<TipsResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getCode() == 200) {
                                tips = response.body().getData();
                                objects.addAll(tips);
                                adapter = new TipsAdapter(getContext(), objects, "premium");
                                //binds the recyclerview
                                recyclerView.setAdapter(adapter);
                        }
                    }
                    //set loading to false
                    isLoading = false;
                    //hides the progress bar
                    loadinBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<TipsResponse> call, Throwable t) {
                    if(page == 0) {
                        loadinBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "network error!", Toast.LENGTH_SHORT).show();
                    }
                    isLoading = false;
                }
            });
        }
        else {
            loadinBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No network!", Toast.LENGTH_SHORT).show();
        }
    }

    private void recyclerScrollListener() {
        //listen to whenever the recycler view scroll changes
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (manager != null && manager.findLastCompletelyVisibleItemPosition() == objects.size() - 1) {
                    if (!isLoading) {
                        isLoading = true;
                        loadMoreData();
                    }
                }
            }
        });

    }

    private void userSubscription(String user){
        loadinBar.setVisibility(View.VISIBLE);

        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> subscriptionCall = api.userSub(user);
        subscriptionCall.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                loadinBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.body().getCode() == 200){
                        if(response.body().getMessage().equals("subscribed")){
                            subscribed = true;
                            loadTips(page);
                        }
                        else {
                            subDialog();
                        }
                    }
                    else
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else
                    subDialog();
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                loadinBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "verification failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subDialog(){
        loadinBar.setVisibility(View.GONE);
        //shows login action
        Snackbar.make(getView(), "Monthly subscription is needed for premium Predictions", Snackbar.LENGTH_INDEFINITE)
                .setAction("Subscribe Now", v ->{
                    //navigates to the login page
                    Intent i = new Intent(getContext(), SubscriptionActivity.class);
                    startActivity(i);
                }).setTextColor(getContext().getResources().getColor(R.color.white))
                .setActionTextColor(getContext().getResources().getColor(R.color.yll))
                .show();
    }

    private void loadMoreData() {
        page += 1;
        objects.add(null);
        //notify adapter for the item change
        adapter.notifyItemInserted(objects.size() - 1);
        //load the next data on the list
        loadTips(page);
    }

    @Override
    public void onResume() {
        super.onResume();
        confirmUser();
    }

    private void confirmUser(){
        if(Amplify.Auth.getCurrentUser() != null) {
            user = Amplify.Auth.getCurrentUser().getUsername();
            //show progress
            loadinBar.setVisibility(View.VISIBLE);
            //load users account information
            userSubscription(user);
        }
        else {
            //shows login action
            Snackbar.make(getView(), "Sign-In to access Predictions", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Login", v ->{
                        //navigates to the login page
                        Intent i = new Intent(getContext(), AuthActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                    }).setTextColor(getContext().getResources().getColor(R.color.white))
                    .setActionTextColor(getContext().getResources().getColor(R.color.yll))
                    .show();
        }
    }

}
