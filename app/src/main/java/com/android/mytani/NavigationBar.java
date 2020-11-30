package com.android.mytani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class NavigationBar extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);


        bottomNav = findViewById(R.id.bottom_nav);
        if (savedInstanceState == null){
            bottomNav.setItemSelected(R.id.goTo_fragment_home, true);
            fragmentManager = getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .commit();
        }
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {

                // GET FRAGMENT ID TO MOVE FRAGMENT POSITION
                Fragment fragment = null;
                switch (id){
                    case R.id.goTo_fragment_home:
                    fragment = new HomeFragment();
                    break;
                    case R.id.goTo_fragment_discover:
                        fragment = new DiscoverFragment();
                        break;
                    case R.id.goTo_fragment_profile:
                        fragment = new ProfileFragment();
                        showAllDataProfile(fragment);
                }

                // REPLACE FRAME LAYOUT WITH SELECTED FRAGMENT
                if (fragment != null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                } else{
                    Log.e(TAG, "Error dalam membuat fragment");
                }
            }
        });
    }

    private void showAllDataProfile(Fragment profilefragment) {
        Intent intent = getIntent();
        String getUsername = intent.getStringExtra("username");
        String getFullName = intent.getStringExtra("name");
        String getEmail = intent.getStringExtra("email");
        String getPhoneNo = intent.getStringExtra("phoneNo");
        String getPassword = intent.getStringExtra("password");

        Bundle bundle = new Bundle();
        bundle.putString("username", getUsername);
        bundle.putString("name", getFullName);
        bundle.putString("email", getEmail);
        bundle.putString("phoneNo", getPhoneNo);
        bundle.putString("password", getPassword);
        profilefragment.setArguments(bundle);
    }
}