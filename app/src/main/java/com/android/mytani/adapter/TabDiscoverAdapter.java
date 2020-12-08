package com.android.mytani.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.mytani.fragment.discover.DiscoverForumFragment;
import com.android.mytani.fragment.discover.DiscoverNewsFragment;
import com.android.mytani.fragment.discover.DiscoverPriceFragment;

public class TabDiscoverAdapter extends FragmentPagerAdapter {
    public TabDiscoverAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                DiscoverForumFragment discoverForumFragment = new DiscoverForumFragment();
                return discoverForumFragment;
            case 1:
                DiscoverNewsFragment discoverNewsFragment = new DiscoverNewsFragment();
                return discoverNewsFragment;
            case 2:
                DiscoverPriceFragment discoverPriceFragment = new DiscoverPriceFragment();
                return discoverPriceFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 0;
    }
}
