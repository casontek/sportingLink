package com.mycornership.betlink.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mycornership.betlink.fragments.match.MatchTab;

import java.util.List;

public class MatchTabAdapter extends FragmentStateAdapter {
    private List<String> tablabels, tabvalues;

    public MatchTabAdapter(@NonNull Fragment fragment, List<String> labels, List<String> values) {
        super(fragment);
        this.tablabels = labels;
        this.tabvalues = values;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment matchTab = MatchTab.newInstance(tablabels.get(position), tabvalues.get(position));
        return matchTab;
    }

    @Override
    public int getItemCount() {
        return 7;
    }

}
