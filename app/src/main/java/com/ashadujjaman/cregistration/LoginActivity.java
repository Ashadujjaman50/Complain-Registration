package com.ashadujjaman.cregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    private boolean passwordshowing = false;
    EditText LoginEmail, LoginPassword;
    String email, password;
    Button loginBtn;
    TextView signupBtn;
    boolean valid = true;

    ProgressDialog progressDialog;
    Dialog emailVerificationDialog;
    Boolean emailAddressChecker;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        emailVerificationDialog = new Dialog(this);

        LoginEmail = findViewById(R.id.LoginEmail);
        LoginPassword = findViewById(R.id.LoginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        final ImageView passwordIcon = findViewById(R.id.passwordIcon);

       loginBtn.setOnClickListener(view -> {
           checkField(LoginEmail);
           checkField(LoginPassword);

           if (valid){
               email = LoginEmail.getText().toString().trim();
               password = LoginPassword.getText().toString().trim();
               allowingUserToLogin();
           }
       });

        passwordIcon.setOnClickListener(view -> {
            if (passwordshowing){
                passwordshowing = false;

                LoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordIcon.setImageResource(R.drawable.show_password);
            }

            else{
                passwordshowing=true;

                LoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordIcon.setImageResource(R.drawable.hide_password);
            }
            LoginPassword.setSelection(LoginPassword.length());
        });

        signupBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));
    }

    private void allowingUserToLogin() {
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                verifyEmailAddress();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

    }

    private void verifyEmailAddress() {
        FirebaseUser user = mAuth.getCurrentUser();
        emailAddressChecker = user.isEmailVerified();

        if (emailAddressChecker){
            DatabaseReference dReference = FirebaseDatabase.getInstance().getReference("Users");
            Query query = dReference.orderByChild("userId").equalTo(user.getUid());
            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()){
                                        String userType = ""+ds.child("userType").getValue();
                                        if (userType.equals("user")){

                                            progressDialog.dismiss();
                                            sendUserToMainActivity();
                                        }
                                        else {

                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "This email is Admin", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                }
                            });
        }
        else {
            progressDialog.dismiss();
            sendEmailVerificationMessage();

            showEmailVerificationPopupDialog();
        }
    }

    private void sendEmailVerificationMessage() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    mAuth.signOut();
                }
                else {
                    Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            });
        }
    }

    private void showEmailVerificationPopupDialog() {
        emailVerificationDialog.setContentView(R.layout.email_verification_popup_dialog_box);
        emailVerificationDialog.findViewById(R.id.dismiss_btn).setOnClickListener(v -> {
            emailVerificationDialog.dismiss();
            mAuth.signOut();
        });
        emailVerificationDialog.getWindow();
        emailVerificationDialog.setCanceledOnTouchOutside(false);
        emailVerificationDialog.show();
    }

    private void sendUserToMainActivity() {
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        finish();
    }

    public void checkField(EditText textField){
        if (textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user  != null){
            sendUserToMainActivity();
        }
    }
}