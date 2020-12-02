package com.android.mytani.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.mytani.activity.MainActivity;
import com.android.mytani.R;
import com.android.mytani.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment{

    private static final int REQUESTCODE = 2;
    private static final int PReqCode = 2;

    // firebase variables
    FirebaseUser currentUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // dialog
    Dialog popAddPost;

    // layout variables
    ImageView iv_logout, iv_show_popup, iv_popup_userAvatar_img, iv_popup_post_img, iv_popup_addPost_btn;
    TextView tv_nama;
    TextView et_popup_title, et_popup_description;
    ProgressBar popup_progressbar;
    AutoCompleteTextView autoComplete_popup_category;

    // define forum category :
    private final String[] option_category = {"Buah", "Sayur", "Kacang", "Pohon"};

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String imageAvatarUri;
    private Uri pickedImgUri = null;


    public HomeFragment() {
        // Required empty public constructor
    }

/*    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // initialize firebase current user
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // inflate layout with this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // hooks to layout
        iv_logout = view.findViewById(R.id.iv_logout);
        iv_show_popup = view.findViewById(R.id.iv_show_popup);

        // MENAMBAHKAN FORUM DENGAN POPUP
        // initialize popup
        inipopup();

        // handle user click to add image post
        setupPopImageClick();


        iv_show_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();

            }
        });
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        return view;
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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getActivity(), "Tolong terima permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(),
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    private void inipopup() {
        popAddPost = new Dialog(getContext());
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // initialize popup widget
        iv_popup_userAvatar_img = popAddPost.findViewById(R.id.popup_user_avatar);
        iv_popup_post_img = popAddPost.findViewById(R.id.iv_popup_img);
        iv_popup_post_img = popAddPost.findViewById(R.id.iv_popup_img);
        et_popup_title = popAddPost.findViewById(R.id.et_popup_title);
        et_popup_description = popAddPost.findViewById(R.id.et_popup_description);
        iv_popup_addPost_btn = popAddPost.findViewById(R.id.iv_popup_add_btn);
        popup_progressbar = popAddPost.findViewById(R.id.popup_progressbar);
        autoComplete_popup_category = popAddPost.findViewById(R.id.autoComplete_popup_category);

        // list of forum categories :
        ArrayAdapter arrayAdapter = new ArrayAdapter(
                getActivity(),
                R.layout.option_category_post,
                option_category);

        // default category value :
        autoComplete_popup_category.setText(arrayAdapter.getItem(0).toString(), false);

        // set Adapter for categry
        autoComplete_popup_category.setAdapter(arrayAdapter);


        // load user profile avatar
        showUserAvatar();

        // user clicked the create button --> post
        iv_popup_addPost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_popup_addPost_btn.setVisibility(View.INVISIBLE);
                popup_progressbar.setVisibility(View.VISIBLE);

                // validate user input
                if (!et_popup_title.getText().toString().isEmpty()
                    && !et_popup_description.getText().toString().isEmpty()
                    && pickedImgUri != null
                    && !autoComplete_popup_category.getText().toString().isEmpty()){

                    // TODO create post obj and add it to firebase database

                    // upload the post image to firebase storage
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
                                            et_popup_title.getText().toString(),
                                            et_popup_description.getText().toString(),
                                            autoComplete_popup_category.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getUid(),
                                            getUserAvatarUrl());

                                    // finally add post to firebase database
                                    addPost(post);
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
        });

    }

    private String getUserAvatarUrl() {

        // initialize firebase storage
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageFilePath = mStorageRef.child("image_avatar/").child(currentUser.getUid());

        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageAvatarUri = uri.toString();
            }
        });

        return imageAvatarUri;
    }

    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("posts").push();

        // get post unique ID and update post key
        String key = myRef.getKey();
        post.setPostKey(key);

        // add post data to firebase
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Postingan telah berhasil ditambahkan");
                popup_progressbar.setVisibility(View.INVISIBLE);
                iv_popup_addPost_btn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void showUserAvatar() {
        // initialize firebase storage
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageFilePath = mStorageRef.child("image_avatar/").child(currentUser.getUid());

        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .into(iv_popup_userAvatar_img);
            }
        });
    }

}