package com.example.kITa;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PreItemsPagerAdapter extends FragmentStateAdapter {

    public PreItemsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create and return the appropriate fragment for each position
        switch (position) {
            case 0:
                return new PreTodayItemsFragment();
            case 1:
                return new PreWeekItemsFragment();
            case 2:
                return new PreOlderItemsFragment();
            default:
                return new PreTodayItemsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // We have 3 tabs
    }
}
