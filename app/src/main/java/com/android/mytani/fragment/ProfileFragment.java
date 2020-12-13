package com.android.mytani.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mytani.R;
import com.android.mytani.UserHelperClass;
import com.android.mytani.activity.PostUserActivity;
import com.android.mytani.adapter.PostAdapter;
import com.android.mytani.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    //    PROFILE FRAGMENTS DATA INTENT
    TextInputLayout til_fullName, til_email, til_phoneNo, til_password;
    TextView tv_fullName, tv_username, tv_jmlPertanyaan, tv_jmlJawaban;
    ImageView iv_profile;
    CardView cv_post;

    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;

    // firebase variables
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;
    DatabaseReference dataPostRef;


    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    ArrayList<UserHelperClass> listUser = new ArrayList<>();

    List<UserHelperClass> userList;
    List<Post> postList;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();

        String uid = firebaseAuth.getUid();
        // get post data from database
        Query query = dataPostRef.orderByChild("userId").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataCount = (int) snapshot.getChildrenCount();
                showLog(String.valueOf(dataCount));
                tv_jmlPertanyaan.setText(String.valueOf(dataCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showLog(String s) {
        Log.d("PROFILE FRAGMENT ", s);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // HOOKS FOR PROFILE FRAGMENT
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        showUserProfileData(view);
        dataPostRef = firebaseDatabase.getReference("posts");

        cv_post = view.findViewById(R.id.cv_post);
        iv_profile = view.findViewById(R.id.iv_profile_avatar);
        tv_jmlPertanyaan = view.findViewById(R.id.tv_profile_jml_pertanyaan);

        // TODO TAMPILKAN JML POST
        cv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentShowUserPost = new Intent(getActivity(), PostUserActivity.class);
                startActivity(intentShowUserPost);
            }
        });

        // ADD USER AVATAR
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                } else {
                    openGalery();
                }
            }
        });

        return view;
    }

    private void openGalery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == REQUESTCODE
                && data!=null){
            // user succes pick an image
            // we need to save its  reference to a Uri variable
            pickedImgUri = data.getData();
            Log.d("URI IMAGE  ", pickedImgUri.toString());
//            iv_profile.setImageURI(pickedImgUri);

            firebaseStorage = FirebaseStorage.getInstance();
            mStorageRef = firebaseStorage.getReference();
            uploadImage();


//            submitUserAvatar(pickedImgUri);
        }
    }

    private void uploadImage() {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading image ...");
        pd.show();
        final String randomKey = UUID.randomUUID().toString();
        StorageReference imageFilePath = mStorageRef.child("image_avatar/").child(firebaseAuth.getUid());

        imageFilePath.putFile(pickedImgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        showToast("Image uploaded");

                        iv_profile.findViewById(R.id.iv_profile_avatar);
                        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get()
                                        .load(uri)
                                        .placeholder(R.drawable.ic_add_user_avatar)
                                        .into(iv_profile);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        showToast("Failed upload image");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progress " + (int) progressPercent + "%");
            }
        });

    }

    private void submitUserAvatar(Uri pickedImgUri) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String username = firebaseAuth.getUid();

        // we need upload user avatar to firebase in order to get firebase storage url
        mStorageRef = FirebaseStorage.getInstance().getReference().child("users_avatar");
        StorageReference imageFilePath = mStorageRef.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image upload success
                // we can get our image url

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // uri contain user image url
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            showToast("Foto profil berhasil diperbarui");
                                        }
                                    }
                                });
                    }
                });

            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showUserProfileData(View view) {
        til_fullName = view.findViewById(R.id.til_fullName);
        til_phoneNo = view.findViewById(R.id.til_phoneNo);
        til_password = view.findViewById(R.id.til_password);
        til_email = view.findViewById(R.id.til_email);
        tv_fullName = view.findViewById(R.id.tv_profile_full_name);
        tv_username = view.findViewById(R.id.tv_profile_username);

        showProfileAvatar(view);
        String userId = firebaseAuth.getUid();
        DatabaseReference reference = firebaseDatabase.getReference("users");


        Query getUser = reference.orderByChild(userId);
        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listUser.isEmpty()){
                    String nameFromDB = snapshot.child(userId).child("name").getValue(String.class);
                    String usernameFromDB = snapshot.child(userId).child("username").getValue(String.class);
                    String phoneNoFromDB = snapshot.child(userId).child("phoneNo").getValue(String.class);
                    String emailFromDB = snapshot.child(userId).child("email").getValue(String.class);
                    String passwordFromDB = snapshot.child(userId).child("password").getValue(String.class);

                    listUser.add(new UserHelperClass(
                            nameFromDB, usernameFromDB, emailFromDB, phoneNoFromDB, passwordFromDB
                    ));
                    til_fullName.getEditText().setText(nameFromDB);
                    tv_username.setText("@" + usernameFromDB);
                    til_phoneNo.getEditText().setText(phoneNoFromDB);
                    til_password.getEditText().setText(passwordFromDB);
                    til_email.getEditText().setText(emailFromDB);
                    tv_fullName.setText(nameFromDB);
                    iv_profile.setImageURI(pickedImgUri);
                } else {
                    til_fullName.getEditText().setText((CharSequence) listUser.get(1));
                    tv_username.setText("@" + (CharSequence) listUser.get(2));
                    til_email.getEditText().setText((CharSequence) listUser.get(3));
                    til_phoneNo.getEditText().setText((CharSequence) listUser.get(3));
                    til_password.getEditText().setText((CharSequence) listUser.get(4));
                    tv_fullName.setText((CharSequence) listUser.get(1));
                    iv_profile.setImageURI(pickedImgUri);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showProfileAvatar(View view) {
        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = firebaseStorage.getReference();

        StorageReference imageFilePath = mStorageRef.child("image_avatar/").child(firebaseAuth.getUid());

        iv_profile = view.findViewById(R.id.iv_profile_avatar);
        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.ic_add_user_avatar)
                        .into(iv_profile);
            }
        });

    }


}