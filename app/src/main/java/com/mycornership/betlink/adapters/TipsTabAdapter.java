package com.mycornership.betlink.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mycornership.betlink.fragments.tips.BasicTips;
import com.mycornership.betlink.fragments.tips.ControlledTips;
import com.mycornership.betlink.fragments.tips.SponsoredTips;

public class TipsTabAdapter extends FragmentStateAdapter {

    public TipsTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new BasicTips();
            case 1:
                return new ControlledTips();
            case 2:
                return new SponsoredTips();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
