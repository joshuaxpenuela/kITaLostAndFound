package com.example.kITa;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ItemsPagerAdapter extends FragmentStateAdapter {

    public ItemsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create and return the appropriate fragment for each position
        switch (position) {
            case 0:
                return new TodayItemsFragment();
            case 1:
                return new WeekItemsFragment();
            case 2:
                return new OlderItemsFragment();
            default:
                return new TodayItemsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // We have 3 tabs
    }
}
