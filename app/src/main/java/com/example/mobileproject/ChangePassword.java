package com.example.mobileproject;

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
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener{
    private EditText et_changepassword;
    private Button btn_cancel, btn_change;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        et_changepassword = findViewById(R.id.et_changepassword);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_change = findViewById(R.id.btn_change);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_change.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void changepassword() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(TextUtils.isEmpty(et_changepassword.getText().toString())){
            Toast.makeText(this, "Please enter the new password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        user.updatePassword(et_changepassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(ChangePassword.this, "New Password Updated", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(ChangePassword.this, ProfileActivity.class));
                }
            }
        });
    }

    public void onClick(View view){
        if(view == btn_cancel){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if(view == btn_change){
            changepassword();
        }
    }


}
