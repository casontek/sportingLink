package com.mycornership.betlink.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.VideoAdapter;
import com.mycornership.betlink.models.VideoResponse;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoFragment extends Fragment {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<Object> videoItems = new ArrayList<>();
    private ProgressBar loadinBar;

    public VideoFragment() {
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
        loadVideos(1);
    }

    private void init() {
        loadinBar = getView().findViewById(R.id.loading_bar);
        loadinBar.setVisibility(View.GONE);
        //initialize the recyclerView
        recyclerView = getView().findViewById(R.id.video_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new VideoAdapter(getContext(), videoItems);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void loadVideos(int page) {
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<VideoResponse> videoCall = api.fetchVideos(page);
        videoCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                //hides the progress
                loadinBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.body().getCode() == 200){
                        videoItems.addAll(response.body().getData());
                        adapter = new VideoAdapter(getContext(), videoItems);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.d("video_error", t.getMessage());
                //hides the progress
                loadinBar.setVisibility(View.GONE);
            }
        });
    }
    
}
