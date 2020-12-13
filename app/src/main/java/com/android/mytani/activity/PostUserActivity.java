package com.android.mytani.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.mytani.R;
import com.android.mytani.adapter.PostAdapter;
import com.android.mytani.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostUserActivity extends AppCompatActivity {

    // firebase variables
    private FirebaseAuth currentUser;
    private FirebaseDatabase database;
    private DatabaseReference postDatabaseRef;

    // layout variables
    RecyclerView rc_userPost;
    TextView tv_dataCount;
    SearchView searchView_post;

    // list
    List<Post> filteredPostList;

    // adapter recylerview
    PostAdapter postAdapter;

    // current user
    String uid;
    int dataCount;

    @Override
    protected void onStart() {
        super.onStart();

        filteredPostList = new ArrayList<>();
        uid = currentUser.getUid();
        Query query = postDatabaseRef.orderByChild("userId").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postnap : snapshot.getChildren()){
                    Post post = postnap.getValue(Post.class);
                    filteredPostList.add(post);
                }
                dataCount = (int) snapshot.getChildrenCount();
                tv_dataCount.setText("Menampilkan " + String.valueOf(dataCount) + " forum");
                showLog(String.valueOf(dataCount));
                postAdapter = new PostAdapter(PostUserActivity.this, filteredPostList);
                rc_userPost.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*postDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postnap : snapshot.getChildren()){
                    Post post = postnap.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter = new PostAdapter(PostUserActivity.this, postList);
                rc_userPost.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }

    private void showLog(String msg) {
        Log.d("POST USER ACT " , msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_user);

        // initialize firebase
        database = FirebaseDatabase.getInstance();
        postDatabaseRef = database.getReference("posts");
        currentUser = FirebaseAuth.getInstance();
        currentUser.getCurrentUser();


        // initialize layout view
        rc_userPost = findViewById(R.id.rc_userPost);
        rc_userPost.setLayoutManager(new LinearLayoutManager(PostUserActivity.this));
        rc_userPost.hasFixedSize();
        tv_dataCount = findViewById(R.id.tv_postUser_jmlPost);
        searchView_post = findViewById(R.id.searchview_postUserForum);


        searchView_post.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        showLog(String.valueOf(dataCount));

        tv_dataCount.setText("Menampilkan " + String.valueOf(dataCount) + " Forum");



    }
}