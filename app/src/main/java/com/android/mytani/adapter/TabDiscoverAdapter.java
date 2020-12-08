package com.android.mytani.adapter;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.mytani.fragment.discover.DiscoverForumFragment;
import com.android.mytani.fragment.discover.DiscoverNewsFragment;
import com.android.mytani.fragment.discover.DiscoverPriceFragment;

public class TabDiscoverAdapter extends FragmentPagerAdapter {

    private int tabsNumber;


    public TabDiscoverAdapter(@NonNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                showLog("FRAGMENT FORUM");
                return new DiscoverForumFragment();
            case 1:
                showLog("FRAGMENT NEWS");
                return new DiscoverNewsFragment();
            case 2:
                showLog("FRAGMENT PRICE");
                return new DiscoverPriceFragment();
            default: return new DiscoverForumFragment();

        }
    }

    private void showLog(String msg) {
        Log.d("COBA FRAGMENT", msg);
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }
}
