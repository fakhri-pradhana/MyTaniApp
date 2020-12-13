package com.android.mytani.fragment.category;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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

public class CategoryFruitFragment extends Fragment {

    // firebase variables
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databasePostRef;

    PostAdapter postAdapter;
    RecyclerView rv_fruitCat;
    SearchView searchview_catFruit;
    RelativeLayout relative_illustration_notfound;

    // list
    List<Post> filteredPostList;
    List<Post> postList;

    boolean dataExist = false;
    public CategoryFruitFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        showLog("INI ONSTART");
        // get list posts from database
        filteredPostList = new ArrayList<>();
        Query query = databasePostRef.orderByChild("category").equalTo("Buah");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check is data null ?
                if (snapshot.exists()){
                    dataExist = true;
                    postList = new ArrayList<>();
                    for (DataSnapshot postnap : snapshot.getChildren()){
                        Post post = postnap.getValue(Post.class);
                        postList.add(post);
                    }
                    postAdapter = new PostAdapter(getActivity(), postList);
                    rv_fruitCat.setAdapter(postAdapter);
                } else {
                    searchview_catFruit.setVisibility(View.INVISIBLE);
                    relative_illustration_notfound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showLog(String msg) {
        Log.d("FRAGMENT FRUIT ",msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_fruit, container, false);

        // initialize firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databasePostRef = firebaseDatabase.getReference("posts");

        // initialize recyclerview
        rv_fruitCat = view.findViewById(R.id.rv_fruitCat);
        relative_illustration_notfound = view.findViewById(R.id.relative_illustration_notfound);
        rv_fruitCat.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_fruitCat.hasFixedSize();

        searchview_catFruit =view.findViewById(R.id.searchview_catFruit);
        if (dataExist){
            searchview_catFruit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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