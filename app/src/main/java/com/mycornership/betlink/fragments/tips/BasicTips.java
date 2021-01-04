package com.mycornership.betlink.fragments.tips;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.TipsAdapter;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.Prediction;
import com.mycornership.betlink.models.TipsResponse;
import com.mycornership.betlink.networks.NetworkAvailability;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasicTips extends Fragment {
    private RecyclerView recyclerView;
    private TipsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<Object> objects = new ArrayList<>();
    private List<Prediction> tips = new ArrayList<>();
    private ProgressBar loadinBar;
    private AdLoader adLoader;
    private int page = 0, number = 1;
    private boolean isLoading =  true;
    private MaterialButton btn_rfs;

    public BasicTips() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_tips, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
        //load news items
        loadTips(0);
        //add listener to the recycler view
        recyclerScrollListener();
        //loads native ads
        loadAds();
    }

    private void init() {
        loadinBar = getView().findViewById(R.id.loading_bar);
        btn_rfs = getView().findViewById(R.id.refresh_btn);
        refreshLayout = getView().findViewById(R.id.refresh);
        //initialize the recyclerView
        recyclerView = getView().findViewById(R.id.tips_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new TipsAdapter(getContext(), objects, "basic");
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        //listen to refresh action
        refreshLayout.setOnRefreshListener( () ->{
            //hides refresh button if its visible
            btn_rfs.setVisibility(View.GONE);
            if(!isLoading) {
                isLoading = true;
                page = 0;
                objects = new ArrayList<>();
                //load news items
                loadTips(page);
            }
        });
        btn_rfs.setOnClickListener(l ->{
            //hides the button
            btn_rfs.setVisibility(View.GONE);
            loadinBar.setVisibility(View.VISIBLE);
            if(!isLoading) {
                isLoading = true;
                page = 0;
                objects = new ArrayList<>();
                //load news items
                loadTips(page);
            }
        });
    }

    private void loadTips(int page) {
        if(NetworkAvailability.isConnected(getContext())) {
            //load basic tips
            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<TipsResponse> tipsCall = api.fetchTips("basic", page);
            tipsCall.enqueue(new Callback<TipsResponse>() {
                @Override
                public void onResponse(Call<TipsResponse> call, Response<TipsResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getCode() == 200) {
                            tips = response.body().getData();
                            if(page == 0) {
                                objects.addAll(tips);
                                adapter = new TipsAdapter(getContext(), objects, "basic");
                                //binds the recyclerview
                                recyclerView.setAdapter(adapter);
                            }
                            else {
                                //subsequent pages
                                objects.remove(objects.size() - 1);
                                int x = objects.size();
                                adapter.notifyItemRemoved(x);

                                objects.addAll(tips);
                                adapter.notifyDataSetChanged();
                                //close network loading handles
                                isLoading = false;
                                //flush ads on the addea items
                                loadAds();
                            }
                        }
                    }
                    //set loading to false
                    isLoading = false;
                    //turns off refreshing if its On
                    refreshLayout.setRefreshing(false);
                    //hides the progress bar
                    loadinBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(Call<TipsResponse> call, Throwable t) {
                    if(page == 0) {
                        loadinBar.setVisibility(View.GONE);
                        btn_rfs.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "network error!", Toast.LENGTH_SHORT).show();
                    }
                    isLoading = false;
                    //turns off refreshing if its On
                    refreshLayout.setRefreshing(false);
                }
            });
        }
        else{
            loadinBar.setVisibility(View.GONE);
            //shows refresh button
            btn_rfs.setVisibility(View.VISIBLE);
            //turns off refreshing if its On
            refreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "No network", Toast.LENGTH_SHORT).show();
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

    private void loadMoreData() {
                page += 1;
                objects.add(null);
                //notify adapter for the item change
                adapter.notifyItemInserted(objects.size() - 1);
                //load the next data on the list
                loadTips(page);
    }

    private void loadAds(){
        adLoader = new AdLoader.Builder(getContext(), getString(R.string.ads_native_units))
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    int x = number * 13;
                    if(objects.size() >= x)
                        objects.add(x, unifiedNativeAd);
                        adapter.notifyItemInserted(x);

                        number += 1;
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
