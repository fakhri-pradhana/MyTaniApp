package com.android.mytani.fragment.discover;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.android.mytani.R;
import com.google.android.material.tabs.TabLayout;


public class DiscoverFragment extends Fragment {

    // layout variables
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;



    public DiscoverFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }
}