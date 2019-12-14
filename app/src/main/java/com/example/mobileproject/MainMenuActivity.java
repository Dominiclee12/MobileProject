package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    public TextView tv_name;

    public Button btn_apply, btn_post;
    public ImageView i_profile;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        btn_apply = findViewById(R.id.btn_apply);
        btn_post = findViewById(R.id.btn_post);
        i_profile = findViewById(R.id.i_profile);
        tv_name = findViewById(R.id.tv_name);


        btn_apply.setOnClickListener(this);
        btn_post.setOnClickListener(this);
        i_profile.setOnClickListener(this);

        uid = user.getUid();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("UserInformation");

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                tv_name.setText(name);

                String userProfile = dataSnapshot.child(uid).child("profileImageUrl").getValue(String.class);
                Picasso.get().load(userProfile).placeholder(R.mipmap.ic_launcher_round).into(i_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btn_apply) {
            startActivity(new Intent(this, JobListActivity.class));
        }

        if (view == btn_post) {
            startActivity(new Intent(this, PostJobActivity.class));
        }

        if (view == i_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }
}
