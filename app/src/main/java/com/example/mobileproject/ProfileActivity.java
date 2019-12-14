package com.example.mobileproject;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView tv_name, tv_phone, tv_gender, tv_date, tv_password, tvJobPost, tvJobApply;
    private ImageView i_profile;
    private Button btn_logout, btn_save;

    //store data to database
    private DatabaseReference databaseReference, databaseUser;

    StorageReference mStorageRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Uri mImageUri;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = auth.getInstance().getCurrentUser();
        uid = user.getUid();

        btn_logout = findViewById(R.id.btn_logout);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_gender = findViewById(R.id.et_gender);
        tv_date = findViewById(R.id.tv_date);
        tv_password = findViewById(R.id.et_password);
        i_profile = findViewById(R.id.i_profile);
        tvJobPost = findViewById(R.id.tv_jobpost);
        tvJobApply = findViewById(R.id.tv_jobapply);
        btn_save = findViewById(R.id.btn_update);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        btn_logout.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        tv_phone.setOnClickListener(this);
        tv_gender.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        tv_password.setOnClickListener(this);
        i_profile.setOnClickListener(this);
        tvJobPost.setOnClickListener(this);
        tvJobApply.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        //store data to database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseUser = FirebaseDatabase.getInstance().getReference("UserInformation");

        //show user information
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                tv_name.setText(name);

                String phone = dataSnapshot.child(uid).child("phone").getValue(String.class);
                tv_phone.setText(phone);

                String gender = dataSnapshot.child(uid).child("gender").getValue(String.class);
                tv_gender.setText(gender);

                String date = dataSnapshot.child(uid).child("date").getValue(String.class);
                tv_date.setText(date);

                String userProfile = dataSnapshot.child(uid).child("profileImageUrl").getValue(String.class);
                Picasso.get().load(userProfile).placeholder(R.mipmap.ic_launcher_round).into(i_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void openDialogEditName() {
        EditName editName = new EditName();
        editName.show(getSupportFragmentManager(), "example dialog");
    }

    public void openDialogEditPhone() {
        EditPhone editPhone = new EditPhone();
        editPhone.show(getSupportFragmentManager(), "example dialog");
    }

    public void openDialogEditGender() {
        EditGender editGender = new EditGender();
        editGender.show(getSupportFragmentManager(), "example dialog");
    }

    private void openDialogEditDate() {
        EditDate editDate = new EditDate();
        editDate.show(getSupportFragmentManager(), "example dialog");
    }

    private void changepicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void updatepicture() {

        if (mImageUri != null) {
            StorageReference Ref = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(mImageUri));

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getExtension(mImageUri));

            fileReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        String image = downloadUri.toString();

                        databaseUser.child(uid).child("profileImageUrl").setValue(image);

                        Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            i_profile.setImageURI(mImageUri);
        }
    }


    @Override
    public void onClick(View view) {
        if (view == btn_logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        if (view == tv_password) {
            startActivity(new Intent(this, ChangePassword.class));
        }

        if (view == tv_name) {
            openDialogEditName();
        }

        if (view == tv_phone) {
            openDialogEditPhone();
        }

        if (view == tv_gender) {
            openDialogEditGender();
        }

        if (view == tv_date) {
            openDialogEditDate();
        }

        if (view == i_profile) {
            changepicture();
        }

        if (view == tvJobPost) {
            startActivity(new Intent(this, PostJobHistoryActivity.class));
        }

        if (view == tvJobApply) {
            startActivity(new Intent(this, ApplyJobHistoryActivity.class));
        }

        if (view == btn_save) {
            updatepicture();
        }
    }

}
