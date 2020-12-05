package com.android.mytani.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.android.mytani.models.Comment;
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

import java.util.Calendar;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    // layout variables
    ImageView iv_post, iv_userPost, iv_currentUser;
    TextView tv_postDescription, tv_postDateName, tv_postTitle;
    TextInputLayout til_comment;
    Button btn_addComment;

    String postKey;

    // firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;

    // get data user
    Uri imageAvatarUri;
    private String username="";

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
        iv_post = findViewById(R.id.post_detail_img);
        iv_userPost = findViewById(R.id.post_detail_user_img);
        iv_currentUser = findViewById(R.id.iv_detailPost_currentUser);

        tv_postTitle = findViewById(R.id.post_detail_title);
        tv_postDescription = findViewById(R.id.post_detail_description);
        tv_postDateName = findViewById(R.id.post_detail_date_name);

        til_comment = findViewById(R.id.til_comment);
        btn_addComment = findViewById(R.id.btn_detailPost_addComment);

        // showing detail data from clicked post via intent
        showPostDetailData();
    }

    private void showPostDetailData() {
        // get post data via intent from PostAdapter
        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(iv_post);

        String postTitle = getIntent().getStringExtra("title");
        tv_postTitle.setText(postTitle);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(iv_userPost);

        String postDescription = getIntent().getStringExtra("description");
        tv_postDescription.setText(postDescription);

        showCurrentUserPhoto();

        postKey = getIntent().getStringExtra("postKey");

        String date = timeStampToString(getIntent().getExtras().getLong("postDate"));
        showDateName(date);
    }

    private void showDateName(String date){
        String uid = currentUser.getUid();
        DatabaseReference userRef = firebaseDatabase.getReference("users");

        Query getUser = userRef.orderByChild(uid);
        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child(uid).child("name").getValue(String.class);
                tv_postDateName.setText(date + " | oleh @" + username);
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
        btn_addComment.setVisibility(View.INVISIBLE);
        DatabaseReference commentRef = firebaseDatabase.getReference("comments").child(postKey).push();
        String commentContent = til_comment.getEditText().getText().toString();

        String uid = currentUser.getUid();
        String uname = username;
        String uimg = imageAvatarUri.toString();
        int upvote = 0;
        int devote = 0;
        Comment comment = new Comment(commentContent, uid, uimg, uname, upvote, devote);

        commentRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Komentar berhasil ditambahkan");
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