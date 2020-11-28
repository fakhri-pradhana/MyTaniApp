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

        // save REGISTER data to FIREBASE on btn click
        btn_reg_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Write a message to the database


                // GET ALL VALUES FROM LAYOUT
                String name = et_RegName.getEditText().getText().toString();
                String username = et_RegUsername.getEditText().getText().toString();
                String email = et_RegEmail.getEditText().getText().toString();
                String phoneNo = et_RegPhoneNo.getEditText().getText().toString();
                String password = et_RegPassword.getEditText().getText().toString();

                UserHelperClass request = new UserHelperClass(name, username, email, phoneNo, password);
                submitUser(request);
//                reference.child(phoneNo).setValue(helperClass);

            }
        });
    }

    private void submitUser(UserHelperClass requests) {
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        reference
                .push()
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