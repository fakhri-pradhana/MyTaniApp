package com.android.mytani.fragment.discover;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.mytani.R;
import com.android.mytani.activity.NavigationBar;
import com.android.mytani.adapter.TabDiscoverAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;


public class DiscoverFragment extends Fragment{

    ViewPager mPager;
    TabLayout mTabLayout;
    TabItem item_forum, item_news, item_price;
    TabDiscoverAdapter tabDiscoverAdapter;

    int numTab;

    public DiscoverFragment() {
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("fragment discover", "onstart");
        tabDiscoverAdapter = new TabDiscoverAdapter(
                ((NavigationBar) getActivity()).getSupportFragmentManager(),
                mTabLayout.getTabCount()
        );

        mPager.setAdapter(tabDiscoverAdapter);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_discover, container, false);

        Log.d("fragment discover", "oncreate");

//        Toolbar mToolbar = view.findViewById(R.id.toolbar_discovery);
//        ((NavigationBar) getActivity()).setSupportActionBar(mToolbar);

        mPager = view.findViewById(R.id.viewpager_discovery);
        mTabLayout = view.findViewById(R.id.tablayout_discover);
        item_forum = view.findViewById(R.id.tab_forum);
        item_news = view.findViewById(R.id.tab_news);
        item_price = view.findViewById(R.id.tab_price);
        return view;
    }

    private void showLog(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}