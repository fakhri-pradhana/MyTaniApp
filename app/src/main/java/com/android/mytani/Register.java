package com.android.mytani;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    // XML LAYOUT VARIABLES
    TextInputLayout et_RegName, et_RegUsername, et_RegEmail, et_RegPhoneNo, et_RegPassword;
    Button btn_reg_daftar, btn_reg_toLogin;

    // FIREBASE VARIABLES
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        // Hooks to xml file layout
        et_RegName = findViewById(R.id.et_register_namaLengkap);
        et_RegUsername = findViewById(R.id.et_register_username);
        et_RegEmail = findViewById(R.id.et_register_email);
        et_RegPhoneNo = findViewById(R.id.et_register_phoneNo);
        et_RegPassword = findViewById(R.id.et_register_password);
        btn_reg_daftar = findViewById(R.id.btn_register_daftar);
        btn_reg_toLogin = findViewById(R.id.btn_register_toLogin);

    }

    private Boolean isValidateName(){
        String val = et_RegName.getEditText().getText().toString();
        if (val.isEmpty()){
            et_RegName.setError("Nama tidak boleh kosong");
            return false;
        } else {
            et_RegName.setError(null);
            et_RegName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean isValidateUsername(){
        String val = et_RegUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            et_RegUsername.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            et_RegUsername.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            et_RegUsername.setError("White Spaces are not allowed");
            return false;
        } else {
            et_RegUsername.setError(null);
            et_RegUsername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean isValidateEmail(){
        String val = et_RegEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            et_RegEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            et_RegEmail.setError("Invalid email address");
            return false;
        } else {
            et_RegEmail.setError(null);
            et_RegEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean isValidatePhoneNo(){
        String val = et_RegPhoneNo.getEditText().getText().toString();

        if (val.isEmpty()) {
            et_RegPhoneNo.setError("Field cannot be empty");
            return false;
        } else {
            et_RegPhoneNo.setError(null);
            et_RegPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean isValidatePassword(){
        String val = et_RegPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "\\A\\w{4,20}\\z" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (val.isEmpty()) {
            et_RegPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            et_RegPassword.setError("Password is too weak");
            return false;
        } else {
            et_RegPassword.setError(null);
            et_RegPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void registerUser(View view) {

        // VALIDATE REGISTER DATA
        if (!isValidateName()
                || !isValidateEmail()
                || !isValidateUsername()
                || !isValidatePhoneNo()){
            return;
        }
        // GET ALL VALUES FROM LAYOUT
        String name = et_RegName.getEditText().getText().toString();
        String username = et_RegUsername.getEditText().getText().toString();
        String email = et_RegEmail.getEditText().getText().toString();
        String phoneNo = et_RegPhoneNo.getEditText().getText().toString();
        String password = et_RegPassword.getEditText().getText().toString();

        UserHelperClass request = new UserHelperClass(name, username, email, phoneNo, password);
        submitUser(request, username);
//                reference.child(phoneNo).setValue(helperClass);
    }

    private void submitUser(UserHelperClass requests, String username) {
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        reference
                .child(username)
//                .push() --> kalo pingin id user unik
                .setValue(requests)
                .addOnSuccessListener(this, aVoid -> {
//                    loading.dismiss();
                    et_RegName.setPrefixText("");
                    et_RegUsername.setPrefixText("");
                    et_RegEmail.setPrefixText("");
                    et_RegPhoneNo.setPrefixText("");
                    et_RegPassword.setPrefixText("");

                    Toast.makeText(Register.this,
                            "Data berhasil ditambahkan",
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
    }


}