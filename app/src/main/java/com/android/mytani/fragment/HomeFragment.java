package com.android.mytani.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.mytani.activity.MainActivity;
import com.android.mytani.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment{

    // firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    // dialog
    Dialog popAddPost;

    // layout variables
    ImageView iv_logout, iv_show_popup, iv_popup_userAvatar_img, iv_popup_post_img, iv_popup_addPost_btn;
    TextView tv_nama;
    TextView et_popup_title, et_popup_description;
    ProgressBar popup_progressbar;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        iv_logout = view.findViewById(R.id.iv_logout);
        iv_show_popup = view.findViewById(R.id.iv_show_popup);

        // MENAMBAHKAN FORUM DENGAN POPUP
        // initialize popup
        inipopup();
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
        // Inflate the layout for this fragment
        return view;
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

        iv_popup_addPost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_popup_addPost_btn.setVisibility(View.INVISIBLE);
                popup_progressbar.setVisibility(View.VISIBLE);
            }
        });

    }
}