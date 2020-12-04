package com.android.mytani.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mytani.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2500;

    Animation topAnim, bottomAnim;
    ImageView iv_logo;
    TextView  tv_slogan;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Hooks
        iv_logo = findViewById(R.id.iv_logo);
        tv_slogan = findViewById(R.id.tv_slogan);

        // setting animation
        iv_logo.setAnimation(topAnim);
        tv_slogan.setAnimation(bottomAnim);

        // create splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // CEK APAKAH USER SUDAH LOGIN
                firebaseAuth = FirebaseAuth.getInstance();
                if (firebaseAuth.getCurrentUser() != null){
                    Intent goToHomeintent = new Intent(MainActivity.this, NavigationBar.class);
                    startActivity(goToHomeintent);
                    finish();
                }else {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_SCREEN);

    }
}