package com.android.mytani.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.mytani.R;
import com.android.mytani.adapter.CommentAdapter;
import com.android.mytani.models.Comment;
import com.android.mytani.models.Post;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final int REQUESTCODE = 2;
    private static final int PReqCode = 2;

    // layout variables
    private ImageView iv_post, iv_userPost, iv_currentUser,
            iv_optionBtn, iv_popup_userAvatar_img,
            iv_popup_post_img, iv_popup_addPost_btn;
    private TextView tv_postDescription, tv_postDateName, tv_postTitle, tv_popup_name;
    private TextInputLayout til_comment;
    private Button btn_addComment;
    private RecyclerView rv_comment;
    private EditText et_popup_title, et_popup_description;
    private ProgressBar popup_progressbar;
    private AutoCompleteTextView autoComplete_popup_category;

    // define forum category :
    private final String[] option_category = {"Buah", "Sayur", "Biji", "Pohon"};

    String postKey;
    List<Comment> listComment;

    private Uri pickedImgUri = null;

    // adapter
    private CommentAdapter commentAdapter;

    // firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    // get data user
    private Uri imageAvatarUri;
    private String imageAvatar;
    private String postUsername="";
    private String currentUsername="";

    // dialog
    Dialog editPopup;

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
        iv_optionBtn = findViewById(R.id.iv_detailoption_btn);

        tv_postTitle = findViewById(R.id.post_detail_title);
        tv_postDescription = findViewById(R.id.post_detail_description);
        tv_postDateName = findViewById(R.id.post_detail_date_name);

        til_comment = findViewById(R.id.til_comment);
        btn_addComment = findViewById(R.id.btn_detailPost_addComment);

        initializeEditPopup();

        setupPopImageClick();

        // get user avatar
        getUserAvatarUrl();

        // get current username
        getCurrentUserName();
        // showing detail data from clicked post via intent
        showPostDetailData();
        
        // initialize recyclerview comment
        showComment();

    }

    private void setupPopImageClick() {
        iv_popup_post_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when image clicked, open the gallery
                // check permission first
                checkAndRequestForPermission();
            }
        });
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(PostDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    PostDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(PostDetailActivity.this, "Tolong terima permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(PostDetailActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else
        {
            openGalery();
        }
    }

    private void openGalery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == REQUESTCODE
                && data!=null){

            // user succes pick an image
            // we need to save its  reference to a Uri variable
            pickedImgUri = data.getData();
            iv_popup_post_img.setImageURI(pickedImgUri);
            Log.d("URI IMAGE  ", pickedImgUri.toString());

        }
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
                showLog("KIRIM DATA COMMENT KE ADAPTER", postKey);
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
        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_load_image)
                .into(iv_post);

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


    public void showOptionPopupMenu(View view) {
        // show action if currentuser = uidPost
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_post_option, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_edit_post :
                editPopup.show();
                return true;
            case R.id.option_delete_post :
                confirmDialogDeletePost();
                return true;
            default:
                return false;
        }
    }

    private void confirmDialogDeletePost() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PostDetailActivity.this);

        // set title dialog
        alertDialogBuilder.setTitle("Yakin ingin Hapus forum?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk Hapus")
                .setCancelable(true)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika ya ditekan remove data
                        deletePost();
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    private void deletePost() {
        Post postIntent = (Post) getIntent().getSerializableExtra("Post");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("posts").child(postIntent.getPostKey());

        myRef.removeValue();

        showToast("Forum berhasil dihapus");

        et_popup_title.setText("");
        et_popup_description.setText("");
        autoComplete_popup_category.setText("");
        iv_popup_post_img.setImageResource(R.drawable.bg_add_image);
        editPopup.dismiss();
        finish();
    }


    private void initializeEditPopup() {
        editPopup = new Dialog(PostDetailActivity.this);
        editPopup.setContentView(R.layout.popup_add_post);
        editPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editPopup.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        editPopup.getWindow().getAttributes().gravity = Gravity.TOP;

        // initialize popup widget
        tv_popup_name = editPopup.findViewById(R.id.tv_popup_name);
        iv_popup_userAvatar_img = editPopup.findViewById(R.id.popup_user_avatar);
        iv_popup_post_img = editPopup.findViewById(R.id.iv_popup_img);
        et_popup_title = editPopup.findViewById(R.id.et_popup_title);
        et_popup_description = editPopup.findViewById(R.id.et_popup_description);
        iv_popup_addPost_btn = editPopup.findViewById(R.id.iv_popup_add_btn);
        popup_progressbar = editPopup.findViewById(R.id.popup_progressbar);
        autoComplete_popup_category = editPopup.findViewById(R.id.autoComplete_popup_category);

        // list of forum categories :
        ArrayAdapter arrayAdapter = new ArrayAdapter(
                PostDetailActivity.this,
                R.layout.option_category_post,
                option_category);

        // set Adapter for categry
        autoComplete_popup_category.setAdapter(arrayAdapter);

        // FILL POPUP DENGAN CURRENT DATA POST
        // get post data via intent from PostAdapter
        Post post = (Post) getIntent().getSerializableExtra("Post");

        tv_popup_name.setText("Ubah Forum");

        String image = post.getPicture();
        pickedImgUri = Uri.parse(image);
        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_load_image)
                .into(iv_popup_post_img);

        String userPostImage = post.getUserPhoto();
        Glide.with(this).load(userPostImage).into(iv_popup_userAvatar_img);

        et_popup_title.setText(post.getTitle());

        et_popup_description.setText(post.getDescription());

        autoComplete_popup_category.setText(post.getCategory());

        iv_popup_addPost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogEditPost();
            }
        });
    }

    private void confirmDialogEditPost() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PostDetailActivity.this);

        // set title dialog
        alertDialogBuilder.setTitle("Yakin simpan perubahan?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk simpan")
                .setCancelable(true)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // tombol yes di klik
                        iv_popup_addPost_btn.setVisibility(View.INVISIBLE);
                        popup_progressbar.setVisibility(View.VISIBLE);

                        showLog("CEK ET TITLE", et_popup_title.getText().toString());

                        // validate user input
                        if (!et_popup_title.getText().toString().equals("")
                                && !et_popup_description.getText().toString().equals("")
                                && pickedImgUri != null
                                && !autoComplete_popup_category.getText().toString().equals("")){


                            // upload the post image to firebase storage
                            Post postIntent = (Post) getIntent().getSerializableExtra("Post");
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image_forum");
                            StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageDownloadLink = uri.toString();

                                            // create post object
                                            Post post = new Post(
                                                    postIntent.getPostKey(),
                                                    et_popup_title.getText().toString(),
                                                    et_popup_description.getText().toString(),
                                                    autoComplete_popup_category.getText().toString(),
                                                    imageDownloadLink,
                                                    currentUser.getUid(),
                                                    imageAvatar);

                                            // finally add post to firebase database
                                            updatePost(post);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showToast(e.getMessage());
                                            popup_progressbar.setVisibility(View.INVISIBLE);
                                            iv_popup_addPost_btn.setVisibility(View.VISIBLE);
                                        }
                                    });

                                }
                            });

                        } else {
                            showToast("Semua wajib diisi termasuk gambar");
                            iv_popup_addPost_btn.setVisibility(View.VISIBLE);
                            popup_progressbar.setVisibility(View.INVISIBLE);

                        }
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    private void updatePost(Post post) {
        Post postIntent = (Post) getIntent().getSerializableExtra("Post");
        /*HashMap hashMap = new HashMap();
        hashMap.put("postKey", postIntent.getPostKey());*/

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("posts").child(postIntent.getPostKey());

        // get post unique ID and update post key
        /*String key = myRef.getKey();
        post.setPostKey(key)*/;

        // add post data to firebase
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Forum berhasil diubah");
                popup_progressbar.setVisibility(View.INVISIBLE);
                iv_popup_addPost_btn.setVisibility(View.VISIBLE);

                et_popup_title.setText("");
                et_popup_description.setText("");
                autoComplete_popup_category.setText("");
                iv_popup_post_img.setImageResource(R.drawable.bg_add_image);
                finish();
                editPopup.dismiss();
            }
        });
    }

    private void getUserAvatarUrl() {

        // initialize firebase storage
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageFilePath = mStorageRef.child("image_avatar/").child(currentUser.getUid());

        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageAvatar = uri.toString();
            }
        });
    }

}