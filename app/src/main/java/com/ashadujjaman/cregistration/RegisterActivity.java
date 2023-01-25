package com.ashadujjaman.cregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private boolean passwordshowing = false;
    EditText registerName, registerEmail, registerPassword, registerMobile;
    Button registerBtn;
    TextView signinBtn;
    ImageView passwordIcon;
    boolean valid = true;

    ProgressDialog lodingScreen;
    Dialog emailVerificationDialog;
    String email, username, mobile, password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailVerificationDialog = new Dialog(this);
        lodingScreen = new ProgressDialog(this);

        registerName = findViewById(R.id.registerName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerMobile = findViewById(R.id.registerMobile);
        registerBtn = findViewById(R.id.registerBtn);
        signinBtn = findViewById(R.id.signinBtn);

        passwordIcon = findViewById(R.id.passwordIcon);

        registerBtn.setOnClickListener(view -> {
            checkField(registerName);
            checkField(registerEmail);
            checkField(registerPassword);
            checkField(registerMobile);

            if (valid){
                username = registerName.getText().toString().trim();
                email = registerEmail.getText().toString().trim();
                mobile = registerMobile.getText().toString().trim();
                password = registerPassword.getText().toString().trim();
                createNewAccount();
            }
        });

        passwordIcon.setOnClickListener(view -> {
            if (passwordshowing){
                passwordshowing = false;

                registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordIcon.setImageResource(R.drawable.show_password);
            }

            else{
                passwordshowing=true;

                registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordIcon.setImageResource(R.drawable.hide_password);
            }
            registerPassword.setSelection(registerPassword.length());
        });

        signinBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }


    private void createNewAccount() {
        lodingScreen.setTitle("Creating new Account");
        lodingScreen.setMessage("Please wait...");
        lodingScreen.setCanceledOnTouchOutside(false);
        lodingScreen.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    saveFirebaseData(user);
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed to Create Account", Toast.LENGTH_SHORT).show());
    }

    private void saveFirebaseData(FirebaseUser user) {

        //current time to timestamp
        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> getData = new HashMap<>();
        getData.put("userId", ""+user.getUid());
        getData.put("email", ""+user.getEmail());
        getData.put("name", ""+ registerName.getText().toString());
        getData.put("phone", ""+ registerMobile.getText().toString());
        getData.put("fatherName", "");
        getData.put("emergency1", "");
        getData.put("emergency2", "");
        getData.put("address", "");
        getData.put("userType", "user");
        getData.put("timestamp", ""+timestamp);

        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(user.getUid()).setValue(getData)
                .addOnSuccessListener(aVoid -> {
                    //db upload
                    Toast.makeText(RegisterActivity.this,"Account create Success",Toast.LENGTH_SHORT).show();
                    sendEmailVerificationMessage();
                    lodingScreen.dismiss();
                    /*startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    finish();*/
                })
                .addOnFailureListener(e -> {
                    //failed updating
                    Toast.makeText(RegisterActivity.this,"Not create"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    lodingScreen.dismiss();
                });
    }

    private void sendEmailVerificationMessage() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    showEmailVerificationPopupDialog();
                }
                else {
                    Toast.makeText(RegisterActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            });
        }
    }

    private void showEmailVerificationPopupDialog() {
        emailVerificationDialog.setContentView(R.layout.email_verification_popup_dialog_box);
        emailVerificationDialog.findViewById(R.id.dismiss_btn).setOnClickListener(v -> {
            emailVerificationDialog.dismiss();
            sendUserToLoginActivity();
            mAuth.signOut();
        });
        emailVerificationDialog.getWindow();
        emailVerificationDialog.setCanceledOnTouchOutside(false);
        emailVerificationDialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void checkField(EditText textField){
        if (textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }
        else {
            valid = true;
        }
    }
}