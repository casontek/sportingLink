package com.mycornership.betlink.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mycornership.betlink.fragments.highlight.NewsHighlight;
import com.mycornership.betlink.fragments.highlight.VideoHighlight;
import com.mycornership.betlink.fragments.tips.BasicTips;
import com.mycornership.betlink.fragments.tips.ControlledTips;
import com.mycornership.betlink.fragments.tips.SponsoredTips;

public class HighlightTabAdapter extends FragmentStateAdapter {

    public HighlightTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new NewsHighlight();
            case 1:
                return new VideoHighlight();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
