package com.android.mytani.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.mytani.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private static final String TAG = "LOGIN CLASS";
    Button btn_callRegister, btn_login;
    ProgressBar loading_login;
    TextInputLayout et_logEmail, et_logPassword;

    // FIREBASE AUTH VARIABLES
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        // HOOKS TO LAYOUT
        et_logEmail = findViewById(R.id.et_login_email);
        et_logPassword = findViewById(R.id.et_login_password);
        loading_login = findViewById(R.id.loading_login);

        // PINDAH REGISTER
        btn_callRegister = findViewById(R.id.btn_login_callRegister);

        btn_login = findViewById(R.id.btn_login_masuk);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btn_callRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GO TO REGISTRATION
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private Boolean isValidateEmail(){
        String val = et_logEmail.getEditText().getText().toString();

        if (val.isEmpty()) {
            et_logEmail.setError("Field cannot be empty");
            return false;
        } else {
            et_logEmail.setError(null);
            et_logEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean isValidatePassword(){
        String val = et_logPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            et_logPassword.setError("Field cannot be empty");
            return false;
        } else {
            et_logPassword.setError(null);
            et_logPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser() {
        if (!isValidateEmail() || ! isValidatePassword()){
            return;
        }else {
            // AUTHENTICATE USER LOGIN
            authenticateUser();
//            isUser();
        }
    }

    private void authenticateUser() {
        // INITIALIZE FIREBASE AUTH
        loading_login.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        String userEnteredEmail = et_logEmail.getEditText().getText().toString();
        String userEnteredPassword = et_logPassword.getEditText().getText().toString();

        firebaseAuth.signInWithEmailAndPassword(userEnteredEmail, userEnteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // todo hapus text
                    Log.d(TAG, "signInWithEmail:success");
//                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                    getUserProfileData(currentFirebaseUser.getUid());
                    startActivity(new Intent(Login.this, NavigationBar.class));
                    et_logEmail.getEditText().setText("");
                    et_logPassword.getEditText().setText("");

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(Login.this, "Gagal untuk login",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getUserProfileData(String userId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        // MENGAMBIL NILAI EMAIL DARI FIREBASE UNTUK DI CEK DENGAN INPUT USER
        Query checkUser = reference.orderByChild(userId);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String nameFromDB = snapshot.child(userId).child("name").getValue(String.class);
                        String usernameFromDB = snapshot.child(userId).child("username").getValue(String.class);
                        String phoneNoFromDB = snapshot.child(userId).child("phoneNo").getValue(String.class);
                        String emailFromDB = snapshot.child(userId).child("email").getValue(String.class);
                        String passwordFromDB = snapshot.child(userId).child("password").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), NavigationBar.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("phoneNo", phoneNoFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void isUser(String userId) {
        String userEnteredEmail = et_logEmail.getEditText().getText().toString().trim();
        String userEnteredPassword = et_logPassword.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        // MENGAMBIL NILAI EMAIL DARI FIREBASE UNTUK DI CEK DENGAN INPUT USER
        Query checkUser = reference.orderByChild("email").equalTo(userEnteredEmail);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    et_logEmail.setError(null);
                    et_logEmail.setErrorEnabled(false);

                    String passwordFromDB = snapshot.child(userEnteredEmail).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userEnteredPassword)){

                        et_logEmail.setError(null);
                        et_logEmail.setErrorEnabled(false);

                        String nameFromDB = snapshot.child(userEnteredEmail).child("name").getValue(String.class);
                        String usernameFromDB = snapshot.child(userEnteredEmail).child("username").getValue(String.class);
                        String phoneNoFromDB = snapshot.child(userEnteredEmail).child("phoneNo").getValue(String.class);
                        String emailFromDB = snapshot.child(userEnteredEmail).child("password").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), NavigationBar.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("phoneNo", phoneNoFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);
                    }else {
                        et_logPassword.setError("Password salah");
                        et_logPassword.requestFocus();
                    }
                } else {
                    et_logEmail.setError("Akun belum terdaftar");
                    et_logEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}