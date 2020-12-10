package com.android.mytani.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.mytani.fragment.category.CategoryFruitFragment;
import com.android.mytani.fragment.category.CategorySeedFragment;
import com.android.mytani.fragment.category.CategoryTreeFragment;
import com.android.mytani.fragment.discover.DiscoverForumFragment;
import com.android.mytani.fragment.discover.DiscoverNewsFragment;
import com.android.mytani.fragment.discover.DiscoverPriceFragment;

public class TabCategoryAdapter extends FragmentPagerAdapter {

    private int tabsNumber;


    public TabCategoryAdapter(@NonNull FragmentManager fm, int tabsNumber) {
        super(fm);
        this.tabsNumber = tabsNumber;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new CategoryFruitFragment();
            case 1:
                showLog("FRAGMENT NEWS");
                return new CategorySeedFragment();
            case 2:
                showLog("FRAGMENT PRICE");
                return new CategoryTreeFragment();
            case 3:
                return new CategoryTreeFragment();
            default: return new CategoryFruitFragment();

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
