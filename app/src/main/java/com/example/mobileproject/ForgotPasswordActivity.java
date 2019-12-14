package com.example.mobileproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_email;
    private Button btn_search, btn_back;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        et_email = (EditText)findViewById(R.id.et_email);
        btn_search = (Button)findViewById(R.id.btn_search);
        btn_back = (Button)findViewById(R.id.btn_back);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        btn_search.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    public void findPassword(){
        final String email = et_email.getText().toString();

        progressDialog.setMessage("Searching...");
        progressDialog.show();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(ForgotPasswordActivity.this,"Please enter email", Toast.LENGTH_SHORT).show();
        }
        else {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Password sent to your email", Toast.LENGTH_LONG).show();
                        et_email.setText("");

                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public void onClick(View view){

        if(view == btn_search){
            findPassword();
        }

        if(view == btn_back){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
