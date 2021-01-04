package com.mycornership.betlink.fragments.highlight;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.VideoAdapter;
import com.mycornership.betlink.models.Video;
import com.mycornership.betlink.models.VideoResponse;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoHighlight extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private VideoAdapter adapter;
    private AdLoader adLoader;
    private List<Object> objects = new ArrayList<>();
    private List<Video> videos = new ArrayList<>();
    private ProgressBar loadinBar;
    private boolean isLoading = true;
    private int page = 0, number = 1;

    public VideoHighlight() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
        //load news items
        loadVideos(page);
        //listen to recycler view scroll
        recyclerScrollListener();
        //loads the ads bellow
        loadAds();
    }

    private void init() {
        loadinBar = getView().findViewById(R.id.loading_bar);
        //initialize the recyclerView
        recyclerView = getView().findViewById(R.id.video_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new VideoAdapter(getContext(), objects);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        refreshLayout = getView().findViewById(R.id.refresh);
        //listen to refresh action
        refreshLayout.setDistanceToTriggerSync(100);
        refreshLayout.setOnRefreshListener( () ->{
            if(!isLoading) {
                isLoading = true;
                page = 0;
                objects = new ArrayList<>();
                //load news items
                loadVideos(page);
            }
        });
    }

    private void loadVideos(int page) {
        //load videos from network
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<VideoResponse> videoCall = api.fetchVideos(page);
        videoCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().getCode() == 200){
                        videos = response.body().getData();
                        if(page == 0) {
                            objects.addAll(videos);
                            //add the fetched videos to the list
                            adapter = new VideoAdapter(getContext(), objects);
                            recyclerView.setAdapter(adapter);
                        }
                        else{
                            //subsequent pages
                            objects.remove(objects.size() - 1);
                            int x = objects.size();
                            adapter.notifyItemRemoved(x);

                            objects.addAll(videos);
                            adapter.notifyDataSetChanged();
                            //close network loading handles
                            isLoading = false;
                            //flush ads on the new item list
                            loadAds();
                        }
                    }
                }
                //hides the progress bar
                isLoading = false;
                loadinBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                if(page == 0){
                    loadinBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "network error", Toast.LENGTH_SHORT);

                    refreshLayout.setRefreshing(false);
                }
                isLoading = false;
            }
        });
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
        loadVideos(page);
    }


    private void loadAds(){
        adLoader = new AdLoader.Builder(getContext(), getString(R.string.ads_native_units))
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    int x = number * 6;
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
