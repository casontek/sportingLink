package com.mycornership.betlink.fragments.highlight;

import android.os.Bundle;
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
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.NewsAdapter;
import com.mycornership.betlink.models.News;
import com.mycornership.betlink.models.NewsResponse;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsHighlight extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private NewsAdapter adapter;
    private List<Object> objects = new ArrayList<>();
    private List<News> newsItems = new ArrayList<>();
    private ProgressBar loadinBar;
    private int page = 0;
    private boolean isLoading = true;

    public NewsHighlight(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_tab, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
        //load news items
        loadNews(page);
        //listen to recycler view scroll
        recyclerScrollListener();
    }

    private void init() {
        loadinBar = getView().findViewById(R.id.loading_bar);
        //initialize the recyclerView
        recyclerView = getView().findViewById(R.id.news_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new NewsAdapter(getContext(), objects);
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
                loadNews(page);
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
        loadNews(page);
    }

    private void loadNews(int page) {
        //loads news items from the network
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<NewsResponse> newsCall = api.fetchNews(page);
        newsCall.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().getCode() == 200) {
                        newsItems = response.body().getData();
                        if(page == 0) {
                            objects.addAll(newsItems);
                            adapter = new NewsAdapter(getContext(), objects);
                            recyclerView.setAdapter(adapter);
                        }
                        else{
                            //subsequent pages
                            objects.remove(objects.size() - 1);
                            int x = objects.size();
                            adapter.notifyItemRemoved(x);

                            objects.addAll(newsItems);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                //hides the progress bar
                isLoading = false;
                loadinBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                //hides progress bar
                isLoading = false;
                loadinBar.setVisibility(View.GONE);
                if(page == 0){
                    Toast.makeText(getContext(),"network error!", Toast.LENGTH_SHORT);
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }


}
