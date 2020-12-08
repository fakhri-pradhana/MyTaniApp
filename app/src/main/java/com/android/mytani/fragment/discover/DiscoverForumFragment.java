package com.android.mytani.fragment.discover;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.mytani.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class DiscoverForumFragment extends Fragment {


    // layout variables
    TextInputLayout til_search_forumDiscovery;
    RecyclerView rc_discover_forum;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabaseReference;

    public DiscoverForumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_discover_forum, container, false);

        // initialize view
        til_search_forumDiscovery = view.findViewById(R.id.til_search_forumDiscovery);
        rc_discover_forum = view.findViewById(R.id.rc_discover_forum);

        // initialize firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("posts");

        return view;
    }

    private void forumSearch(String searchText){
        String quary  = searchText.toLowerCase();
        Query searchQuery = mDatabaseReference.orderByChild("title").startAt(quary).endAt(quary+"\uf8ff");

    }
}