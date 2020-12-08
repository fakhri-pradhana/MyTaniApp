package com.android.mytani.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.mytani.R;
import com.android.mytani.adapter.TabDiscoverAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class DiscoverActivity extends AppCompatActivity {

    // layout variables

    ViewPager mPager;
    TabLayout mTabLayout;
    TabItem item_forum, item_news, item_price;
    TabDiscoverAdapter tabDiscoverAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_discover);

        androidx.appcompat.widget.Toolbar mToolbar = findViewById(R.id.toolbar_discovery);
        setSupportActionBar(mToolbar);

        mPager = findViewById(R.id.viewpager_discovery);
        mTabLayout = findViewById(R.id.tablayout_discover);
        item_forum = findViewById(R.id.tab_forum);
        item_news = findViewById(R.id.tab_news);
        item_price = findViewById(R.id.tab_price);

        Log.d("INI DISCOVER", "INI BISA");
        tabDiscoverAdapter = new TabDiscoverAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
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

}
