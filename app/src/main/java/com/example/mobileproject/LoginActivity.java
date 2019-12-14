package com.example.mobileproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_email, et_pasword;
    private TextView tv_signup;
    private Button tv_forgot;
    private Button btn_login;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        et_email = findViewById(R.id.et_email);
        et_pasword = findViewById(R.id.et_password);
        tv_signup = findViewById(R.id.tv_signup);
        tv_forgot = findViewById(R.id.tv_forgot);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(this);
        tv_signup.setOnClickListener(this);
        tv_forgot.setOnClickListener(this);

    }


    public void loginUser() {
        String email = et_email.getText().toString().trim();
        String password = et_pasword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            // email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(password)) {
            // password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        //if validationa are ok
        //we will first show a progressBar
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void onClick(View view) {

        if (view == btn_login) {
            loginUser();
        }

        if (view == tv_signup) {
            startActivity(new Intent(this, MainActivity.class));
        }

        if (view == tv_forgot) {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        }
    }
}
