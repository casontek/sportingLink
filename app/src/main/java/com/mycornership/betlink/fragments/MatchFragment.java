package com.mycornership.betlink.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.MatchTabAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MatchTabAdapter adapter;
    private List<String> tabLabels = new ArrayList<>();
    private List<String> tabsValues = new ArrayList<>();

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components here
        tabLayout = getView().findViewById(R.id.match_tabbar);
        viewPager = getView().findViewById(R.id.match_viewpager);
        constructTabHeaders();
        adapter = new MatchTabAdapter(this, tabLabels, tabsValues);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabLabels.get(position));
        }).attach();

    }

    private void constructTabHeaders(){
        int offset = 0;
        for (int i = 1; i <= 7; i++){
            if(i == 1){
                offset = -2;
            }
            else if(i == 2){
                offset = -1;
            }
            else if(i == 3){
                offset = 0;
            }
            else if(i == 4){
                offset = 1;
            }
            else if(i == 5){
                offset = 2;
            }
            else if(i == 6){
                offset = 3;
            }
            else if(i == 7){
                offset = 4;
            }
            getLabels(offset);
        }
    }

    private void getLabels(int offset){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, offset);

        int yr = c.get(Calendar.YEAR);
        int mth = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String month, today;
        //checks month
        if(mth < 10)
            month = "0" + mth;
        else
            month = mth + "";
        //checks day
        if(day < 10)
            today = "0" + day;
        else
            today = day + "";
        String month_label = getMonthLabel(c.get(Calendar.MONTH));
        String day_label = getDayLabel(c.get(Calendar.DAY_OF_WEEK));

        String label = day_label + "," + month_label + " " + day;
        String value = yr + "-" + month + "-" + today;
        //sets the labels and values
        if(offset == -1){
            tabLabels.add("Yesterday");
            tabsValues.add(value);
        }
        else if(offset == 0){
            tabLabels.add("Today");
            tabsValues.add(value);
        }
        else if(offset == 1){
            tabLabels.add("Tomorrow");
            tabsValues.add(value);
        }
        else {
            tabLabels.add(label);
            tabsValues.add(value);
        }

    }

    private String getMonthLabel(int mnth){
        switch (mnth){
            case 0: return "Jan";
            case 1: return "Feb";
            case 2: return "Mar";
            case 3: return "Apr";
            case 4: return "May";
            case 5: return "Jun";
            case 6: return "Jul";
            case 7: return "Aug";
            case 8: return "Sep";
            case 9: return "Oct";
            case 10: return "Nov";
            case 11: return "Dec";
            default: return null;
        }
    }

    private String getDayLabel(int day){
        switch (day){
            case 1: return "Sun";
            case 2: return "Mon";
            case 3: return "Tue";
            case 4: return "Wen";
            case 5: return "Thu";
            case 6: return "Fri";
            case 7: return "Sat";
            default: return null;
        }
    }


}
