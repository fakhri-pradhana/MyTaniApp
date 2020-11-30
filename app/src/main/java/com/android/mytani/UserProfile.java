package com.android.mytani;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Berhasil LOGOUT", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(new Intent(getApplicationContext(), MainActivity.class)));
        finish();
    }
}