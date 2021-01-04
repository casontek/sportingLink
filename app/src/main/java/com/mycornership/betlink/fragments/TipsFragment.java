package com.mycornership.betlink.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amplifyframework.core.Amplify;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.AddPredictionActivity;
import com.mycornership.betlink.activities.AuthActivity;
import com.mycornership.betlink.activities.NavHomeActivity;
import com.mycornership.betlink.adapters.TipsTabAdapter;

public class TipsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TipsTabAdapter tabAdapter;

    public TipsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initialize the tablayout
        tabLayout = getView().findViewById(R.id.tips_tablayout);
        viewPager = getView().findViewById(R.id.tips_viewpager);
        tabAdapter = new TipsTabAdapter(this);
        viewPager.setAdapter(tabAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->{
            switch (position){
                case 0: tab.setText("basic");
                    break;
                case 1: tab.setText("standard");
                    break;
                case 2: tab.setText("premium");
                    break;
            }
        }).attach();
        //initialize floating action button
        FloatingActionButton userBtn = getView().findViewById(R.id.user_button);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Amplify.Auth.getCurrentUser() != null) {
                    Intent i = new Intent(getContext(), AddPredictionActivity.class);
                    startActivity(i);
                }
                else{
                    //shows login action
                    Snackbar.make(getView(), "Sign-In to access Predictions", Snackbar.LENGTH_LONG)
                            .setAction("Login", v2 ->{
                                //navigates to the login page
                                Intent i = new Intent(getContext(), AuthActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);
                            }).setTextColor(getContext().getResources().getColor(R.color.white))
                            .setActionTextColor(getContext().getResources().getColor(R.color.yll))
                            .show();
                }
            }
        });
        //show or hide floating action button on swipe
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(position == 0)
                    userBtn.show();
                else
                    userBtn.hide();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }


}
