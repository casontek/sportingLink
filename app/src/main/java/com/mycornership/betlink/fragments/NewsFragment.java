package com.mycornership.betlink.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.HighlightTabAdapter;
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

public class NewsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private HighlightTabAdapter tabAdapter;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize the tablayout
        tabLayout = getView().findViewById(R.id.highlight_tablayout);
        viewPager = getView().findViewById(R.id.highlight_viewpager);
        tabAdapter = new HighlightTabAdapter(this);
        viewPager.setAdapter(tabAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->{
            switch (position){
                case 0: tab.setText("News");
                    break;
                case 1: tab.setText("Video");
                    break;
            }
        }).attach();
    }

}
