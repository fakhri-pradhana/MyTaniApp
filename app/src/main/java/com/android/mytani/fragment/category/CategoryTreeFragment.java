package com.android.mytani.fragment.category;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.mytani.R;
import com.android.mytani.adapter.PostAdapter;
import com.android.mytani.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryTreeFragment extends Fragment {

    // firebase variables
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databasePostRef;

    PostAdapter postAdapter;
    RecyclerView rv_treeCat;
    SearchView searchview_catTree;

    // list
    List<Post> filteredPostList;
    List<Post> postList;

    boolean dataExist = false;

    public CategoryTreeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        // get list posts from database
        filteredPostList = new ArrayList<>();
        Query query = databasePostRef.orderByChild("category").equalTo("Pohon");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();
                if (snapshot.exists()){
                    dataExist = true;
                    for (DataSnapshot postnap : snapshot.getChildren()){
                        Post post = postnap.getValue(Post.class);
                        postList.add(post);
                    }
                    postAdapter = new PostAdapter(getActivity(), postList);
                    rv_treeCat.setAdapter(postAdapter);
                } else {
                    searchview_catTree.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_tree, container, false);

        // initialize firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databasePostRef = firebaseDatabase.getReference("posts");

        // initialize recyclerview
        rv_treeCat = view.findViewById(R.id.rv_treeCat);
        rv_treeCat.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_treeCat.hasFixedSize();

        searchview_catTree =view.findViewById(R.id.searchview_catTree);
        if (dataExist){
            searchview_catTree.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    postAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }

        return view;
    }
}