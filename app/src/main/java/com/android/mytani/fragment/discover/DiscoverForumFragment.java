package com.android.mytani.fragment.discover;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mytani.R;
import com.android.mytani.activity.PostDetailActivity;
import com.android.mytani.adapter.PostAdapter;
import com.android.mytani.adapter.PostViewHolder;
import com.android.mytani.models.Post;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class DiscoverForumFragment extends Fragment {

    // layout variables
    TextInputLayout til_search_forumDiscovery;
    RecyclerView rc_discover_forum;

    // layout variables from row_post


    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseRecyclerOptions<Post> options_post;
    FirebaseRecyclerAdapter<Post, PostViewHolder> post_adapter;

    // Post list
    List<Post> postList;

    public DiscoverForumFragment() {
        // Required empty public constructor
        // ini coba commit
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_discover_forum, container, false);

        // initialize view
        til_search_forumDiscovery = view.findViewById(R.id.til_search_forumDiscovery);
        rc_discover_forum = view.findViewById(R.id.rc_discover_forum);

        // reycclerview
        rc_discover_forum.setLayoutManager(new LinearLayoutManager(getActivity()));
        rc_discover_forum.hasFixedSize();

        // initialize firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("posts");

        // load data recyclerview
        loadData();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // get list posts from database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postnap : snapshot.getChildren()){
                    Post post = postnap.getValue(Post.class);
                    postList.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadData() {
        options_post = new FirebaseRecyclerOptions.Builder<Post>().setQuery(mDatabaseReference, Post.class).build();
        post_adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options_post) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
                holder.tv_title.setText(model.getTitle());
                Glide.with(getContext())
                        .load(model.getPicture())
                        .placeholder(R.drawable.ic_load_image)
                        .into(holder.iv_imgPost);
                Picasso.get().load(model.getUserPhoto()).into(holder.img_postProfile);
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post_item,parent, false);

                return new PostViewHolder(v, postList, getActivity());
            }
        };
        post_adapter.startListening();
        rc_discover_forum.setAdapter(post_adapter);
    }


    private void forumSearch(String searchText){
        String quary  = searchText.toLowerCase();
        Query searchQuery = mDatabaseReference.orderByChild("title").startAt(quary).endAt(quary+"\uf8ff");

    }

}