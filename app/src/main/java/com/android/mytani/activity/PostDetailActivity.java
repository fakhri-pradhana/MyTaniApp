package com.android.mytani.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mytani.R;
import com.android.mytani.adapter.CommentAdapter;
import com.android.mytani.models.Comment;
import com.android.mytani.models.Post;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    // layout variables
    private ImageView iv_post, iv_userPost, iv_currentUser;
    private TextView tv_postDescription, tv_postDateName, tv_postTitle;
    private TextInputLayout til_comment;
    private Button btn_addComment;
    private RecyclerView rv_comment;

    String postKey;
    List<Comment> listComment;

    // adapter
    private CommentAdapter commentAdapter;

    // firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    // get data user
    private Uri imageAvatarUri;
    private String postUsername="";
    private String currentUsername="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // set the statue bar to transparent
/*        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);*/

        // initialize firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // initialize Views
        rv_comment = findViewById(R.id.rv_comment);
        iv_post = findViewById(R.id.post_detail_img);
        iv_userPost = findViewById(R.id.post_detail_user_img);
        iv_currentUser = findViewById(R.id.iv_detailPost_currentUser);

        tv_postTitle = findViewById(R.id.post_detail_title);
        tv_postDescription = findViewById(R.id.post_detail_description);
        tv_postDateName = findViewById(R.id.post_detail_date_name);

        til_comment = findViewById(R.id.til_comment);
        btn_addComment = findViewById(R.id.btn_detailPost_addComment);

        // get current username
        getCurrentUserName();
        // showing detail data from clicked post via intent
        showPostDetailData();
        
        // initialize recyclerview comment
        showComment();
    }

    private void showComment() {
        rv_comment.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference commentRef = firebaseDatabase.getReference("comments").child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);
                }
                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                rv_comment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showPostDetailData() {
        // get post data via intent from PostAdapter
        Post post = (Post) getIntent().getSerializableExtra("Post");

        String image = post.getPicture();
        Glide.with(this).load(image).into(iv_post);

        String postTitle = post.getTitle();
        tv_postTitle.setText(postTitle);

        String userPostImage = post.getUserPhoto();
        Glide.with(this).load(userPostImage).into(iv_userPost);

        String postDescription = post.getDescription();
        tv_postDescription.setText(postDescription);

        showCurrentUserPhoto();

        postKey = post.getPostKey();

        String postUuid = post.getUserId();
        String date = timeStampToString((long) post.getTimeStamp());
        showPostDateName(date, postUuid);
    }

    private void showPostDateName(String date, String uid){
        // this method showing name and date from post data
        DatabaseReference userRef = firebaseDatabase.getReference("users");

        Query getUser = userRef.orderByChild(uid);
        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postUsername = snapshot.child(uid).child("name").getValue(String.class);
                tv_postDateName.setText(date + " | oleh @" + postUsername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showLog(String tag, String msg) {
        Log.d(tag,msg);
    }

    private String timeStampToString (long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);

        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();

        return date;
    }

    private void showCurrentUserPhoto(){

        // initialize firebase storage
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageFilePath = mStorageRef.child("image_avatar/").child(currentUser.getUid());

        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageAvatarUri = uri;
                Picasso.get()
                        .load(uri)
                        .into(iv_currentUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Belum ada foto");
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // handle user onclick on btn add comment
    public void submitComment(View view) {
        if (til_comment.getEditText().getText().toString().isEmpty()){
            showToast("Silakan masukkan komentar");
        } else {
            getCurrentUserName();
            btn_addComment.setVisibility(View.INVISIBLE);
            DatabaseReference commentRef = firebaseDatabase.getReference("comments").child(postKey).push();
            String commentContent = til_comment.getEditText().getText().toString();

            String uid = currentUser.getUid();
            String uname = currentUsername;
            String uimg = imageAvatarUri.toString();
            int upvote = 0;
            int devote = 0;
            Comment comment = new Comment(commentContent, uid, uimg, uname, upvote, devote);

            commentRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showToast("Komentar berhasil ditambahkan");
                    showLog("INI USERNAME COMMENT", currentUsername);
                    til_comment.getEditText().setText("");
                    btn_addComment.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast("Komentar gagal ditambahkan" + e.getMessage());
                }
            });
        }
    }

    private void getCurrentUserName() {
        DatabaseReference userRef = firebaseDatabase.getReference("users");
        String uid = currentUser.getUid();

        Query getUser = userRef.orderByChild(uid);
        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUsername = snapshot.child(uid).child("name").getValue(String.class);
                showLog("INI USERNAME COMMENT", currentUsername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}