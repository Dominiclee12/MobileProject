package com.example.mobileproject;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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

import java.text.DateFormat;
import java.util.Calendar;

public class PostJobActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    String uid, email;

    EditText etJobTitle, etJobDescription, etWorkDate, etWorkTime, etLocation, etSalary;
    ImageView imageViewUpload;
    Spinner sCategory;
    Button buttonPostJob, buttonWorkDate, buttonChoose;

    private Uri mImageUri;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private DatabaseReference databaseJobs, databaseUsers, databaseJobsPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        user = auth.getInstance().getCurrentUser();
        uid = user.getUid();

        etJobTitle = findViewById(R.id.et_job_title);
        etJobDescription = findViewById(R.id.et_job_description);
        etWorkTime = findViewById(R.id.et_work_time);
        etWorkDate = findViewById(R.id.et_work_date);
        etLocation = findViewById(R.id.et_location);
        etSalary = findViewById(R.id.et_salary);
        sCategory = findViewById(R.id.spinner_job_category);

        buttonPostJob = findViewById(R.id.btn_post_job);
        buttonWorkDate = findViewById(R.id.btn_work_date);
        buttonChoose = findViewById(R.id.btn_choose);

        imageViewUpload = findViewById(R.id.iv_photo);

        databaseJobs = FirebaseDatabase.getInstance().getReference("Job");
        databaseJobsPost = FirebaseDatabase.getInstance().getReference("UserInformation");
        databaseUsers = FirebaseDatabase.getInstance().getReference("UserInformation");
        mStorageRef = FirebaseStorage.getInstance().getReference("Job");

        buttonPostJob.setOnClickListener(this);
        buttonWorkDate.setOnClickListener(this);
        etWorkDate.setOnClickListener(this);
        buttonChoose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonPostJob) {
            addJob();
            finish();
        }

        if (view == buttonWorkDate) {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "datepicker");
        }

        if (view == etWorkDate) {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "datepicker");
        }

        if (view == buttonChoose) {
            openFileChooser();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());

        etWorkDate.setText(currentDateString);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //Job adding to firebase
    private void addJob() {
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

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

                        final String id = databaseJobs.push().getKey();
                        String hostID = uid;

                        databaseJobsPost = databaseUsers.child(uid).child("JobPost").child(id);

                        databaseUsers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String poster = dataSnapshot.child(uid).child("email").getValue(String.class);
                                String phone = dataSnapshot.child(uid).child("phone").getValue(String.class);

                                databaseJobs.child(id).child("email").setValue(poster);
                                databaseJobs.child(id).child("phone").setValue(phone);
                                databaseJobs.child(id).child("hostID").setValue(uid);
                                databaseJobsPost.child("email").setValue(poster);
                                databaseJobsPost.child("phone").setValue(phone);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        String jobCategories = sCategory.getSelectedItem().toString();
                        String jobTitle = etJobTitle.getText().toString().trim();
                        String jobDescription = etJobDescription.getText().toString().trim();
                        String jobWorkDate = etWorkDate.getText().toString().trim();
                        String jobWorkTime = etWorkTime.getText().toString().trim();
                        String location = etLocation.getText().toString().trim();
                        String salary = etSalary.getText().toString().trim();

                        if (TextUtils.isEmpty(jobCategories)) {
                            // email is empty
                            Toast.makeText(PostJobActivity.this, "Please select category", Toast.LENGTH_SHORT).show();
                            //stopping the function execution further
                            return;
                        }

                        if (TextUtils.isEmpty(jobTitle)) {
                            // email is empty
                            Toast.makeText(PostJobActivity.this, "Please enter job title", Toast.LENGTH_SHORT).show();
                            //stopping the function execution further
                            return;
                        }

                        if (TextUtils.isEmpty(jobDescription)) {
                            // email is empty
                            Toast.makeText(PostJobActivity.this, "Please enter job decription", Toast.LENGTH_SHORT).show();
                            //stopping the function execution further
                            return;
                        }

                        if (TextUtils.isEmpty(jobWorkDate)) {
                            // email is empty
                            Toast.makeText(PostJobActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
                            //stopping the function execution further
                            return;
                        }

                        if (TextUtils.isEmpty(jobWorkTime)) {
                            // email is empty
                            Toast.makeText(PostJobActivity.this, "Please enter time", Toast.LENGTH_SHORT).show();
                            //stopping the function execution further
                            return;
                        }

                        if (TextUtils.isEmpty(location)) {
                            // email is empty
                            Toast.makeText(PostJobActivity.this, "Please enter location", Toast.LENGTH_SHORT).show();
                            //stopping the function execution further
                            return;
                        }

                        if (TextUtils.isEmpty(salary)) {
                            // email is empty
                            Toast.makeText(PostJobActivity.this, "Please enter salary", Toast.LENGTH_SHORT).show();
                            //stopping the function execution further
                            return;
                        }

                        Job job = new Job(image, id, jobCategories, jobTitle, jobDescription, jobWorkDate, jobWorkTime, location, salary, hostID);

                        databaseJobs.child(id).setValue(job);
                        databaseJobsPost.setValue(job);

                        Toast.makeText(PostJobActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PostJobActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(PostJobActivity.this, "Please provide all the information include photo", Toast.LENGTH_LONG).show();
        }
    }

    //Open photo gallery in Android phone
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //image set to imageView after selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            imageViewUpload.setImageURI(mImageUri);
        }
    }
}
