package com.android.mytani;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    //    PROFILE FRAGMENTS DATA INTENT
    TextInputLayout til_fullName, til_email, til_phoneNo, til_password;
    TextView tv_fullName, tv_username, tv_jmlPertanyaan, tv_jmlJawaban;

    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // HOOKS FOR PROFILE FRAGMENT
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        showUserProfileData(view);


        return view;
    }

    private void showUserProfileData(View view) {
        til_fullName = view.findViewById(R.id.til_fullName);
        til_phoneNo = view.findViewById(R.id.til_phoneNo);
        til_password = view.findViewById(R.id.til_password);
        til_email = view.findViewById(R.id.til_email);
        tv_fullName = view.findViewById(R.id.tv_profile_full_name);
        tv_username = view.findViewById(R.id.tv_profile_username);

        String userId = firebaseAuth.getUid();
        DatabaseReference reference = firebaseDatabase.getReference("users");

        Query getUser = reference.orderByChild(userId);
        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nameFromDB = snapshot.child(userId).child("name").getValue(String.class);
                String usernameFromDB = snapshot.child(userId).child("username").getValue(String.class);
                String phoneNoFromDB = snapshot.child(userId).child("phoneNo").getValue(String.class);
                String emailFromDB = snapshot.child(userId).child("email").getValue(String.class);
                String passwordFromDB = snapshot.child(userId).child("password").getValue(String.class);

                til_fullName.getEditText().setText(nameFromDB);
                tv_username.setText(usernameFromDB);
                til_phoneNo.getEditText().setText(phoneNoFromDB);
                til_password.getEditText().setText(passwordFromDB);
                til_email.getEditText().setText(emailFromDB);
                tv_fullName.setText(nameFromDB);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}