package com.example.mobileproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JobDetailActivity extends AppCompatActivity implements View.OnClickListener {

    String uid;

    TextView tvJobTitle, tvSalary, tvJobCategory, tvLocation, tvWorkDate, tvWorkTime, tvJobDescription, tvJobID, tvHostID;
    ImageButton imageButtonCall, imageButtonEmail;
    Button buttonApply;
    ImageView ivJobPhoto;
    ListView mListView;

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    UserInformation applicants;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseApplicants, databaseUser, databaseJobApply, databaseUserContact, databaseReference;
    private String emailDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        user = auth.getInstance().getCurrentUser();
        uid = user.getUid();
        applicants = new UserInformation();
        mListView = findViewById(R.id.lv_job_applicant);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.customlayout, R.id.tv_user_name, arrayList);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Post Detail");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        tvJobTitle = findViewById(R.id.tv_job_title);
        tvJobCategory = findViewById(R.id.tv_job_category);
        tvSalary = findViewById(R.id.tv_salary);
        tvLocation = findViewById(R.id.tv_location);
        tvJobDescription = findViewById(R.id.tv_job_description);
        tvWorkDate = findViewById(R.id.tv_work_date);
        tvWorkTime = findViewById(R.id.tv_work_time);
        tvJobID = findViewById(R.id.tv_job_id);
        tvHostID = findViewById(R.id.tv_host_id);

        imageButtonCall = findViewById(R.id.ib_call);
        imageButtonEmail = findViewById(R.id.ib_email);
        buttonApply = findViewById(R.id.btn_apply);

        ivJobPhoto = findViewById(R.id.iv_job_photo);

        //get data from intent
        byte[] bytes = getIntent().getByteArrayExtra("image");
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String jobCategory = getIntent().getStringExtra("jobCategory");
        String salary = getIntent().getStringExtra("salary");
        String location = getIntent().getStringExtra("location");
        String jobDescription = getIntent().getStringExtra("jobDescription");
        String workDate = getIntent().getStringExtra("workDate");
        String workTime = getIntent().getStringExtra("workTime");
        String jobID = getIntent().getStringExtra("jobID");
        String hostID = getIntent().getStringExtra("hostID");

        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //set data to views
        tvJobTitle.setText(jobTitle);
        tvJobCategory.setText(jobCategory);
        tvSalary.setText(salary);
        tvLocation.setText(location);
        tvJobDescription.setText(jobDescription);
        tvWorkDate.setText(workDate);
        tvWorkTime.setText(workTime);
        ivJobPhoto.setImageBitmap(bmp);

        imageButtonCall.setOnClickListener(this);
        imageButtonEmail.setOnClickListener(this);
        buttonApply.setOnClickListener(this);

        databaseApplicants = FirebaseDatabase.getInstance().getReference("UserInformation").child(hostID).child("JobPost").child(jobID).child("JobApplicant");
        databaseReference = FirebaseDatabase.getInstance().getReference("UserInformation");

        //email detail
        databaseUser = FirebaseDatabase.getInstance().getReference("UserInformation");
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                String gender = dataSnapshot.child(uid).child("gender").getValue(String.class);
                String phone = dataSnapshot.child(uid).child("phone").getValue(String.class);

                emailDetail = "Hi, Sir/Mdm, I'm " + name + ", " + gender + ", contact number of " +
                        phone + ". I've read your job description and I'm interested in applying this job.";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseApplicants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    applicants = ds.getValue(UserInformation.class);
                    arrayList.add(applicants.getName() + " - " + applicants.getPhone());
                }
                mListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        String jobID = getIntent().getStringExtra("jobID");
        String hostID = getIntent().getStringExtra("hostID");
        databaseUserContact = FirebaseDatabase.getInstance().getReference("Job").child(jobID);
        databaseUser = FirebaseDatabase.getInstance().getReference("UserInformation").child(hostID);


        switch (view.getId()) {
            case R.id.ib_call:
                databaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        String phone = dataSnapshot.child("phone").getValue(String.class);

                        callIntent.setData(Uri.parse("tel: " + phone));

                        if (callIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(callIntent);
                        } else {
                            Toast.makeText(JobDetailActivity.this, "Sorry! Your mobile do not have applicable program to run this activity.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

            case R.id.ib_email:

                databaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String jobTitle = getIntent().getStringExtra("jobTitle");

                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Application for Job '" + jobTitle + "'");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hai Sir/Mdm, I am interested in applying your job but can I have more details regarding the job posted.");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(emailIntent);
                        } else {
                            Toast.makeText(JobDetailActivity.this, "Sorry! Your mobile do not have applicable program to run this activity.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

            case R.id.btn_apply:


                databaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String jobTitle = getIntent().getStringExtra("jobTitle");

                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Application for Job '" + jobTitle + "'");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, emailDetail);
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(emailIntent);
                        } else {
                            Toast.makeText(JobDetailActivity.this, "Sorry! Your mobile do not have applicable program to run this activity.", Toast.LENGTH_SHORT).show();
                        }
                        saveApplicant();
                        saveApplyJob();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;
        }
    }

    private void saveApplicant() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                String email = dataSnapshot.child(uid).child("email").getValue(String.class);
                String phone = dataSnapshot.child(uid).child("phone").getValue(String.class);

                databaseApplicants.child(uid).child("userID").setValue(uid);
                databaseApplicants.child(uid).child("name").setValue(name);
                databaseApplicants.child(uid).child("email;").setValue(email);
                databaseApplicants.child(uid).child("phone").setValue(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveApplyJob() {
        String image = getIntent().getStringExtra("imageUrl");
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String jobCategories = getIntent().getStringExtra("jobCategory");
        String salary = getIntent().getStringExtra("salary");
        String location = getIntent().getStringExtra("location");
        String jobDescription = getIntent().getStringExtra("jobDescription");
        String jobWorkDate = getIntent().getStringExtra("workDate");
        String jobWorkTime = getIntent().getStringExtra("workTime");
        String id = getIntent().getStringExtra("jobID");
        String hostID = getIntent().getStringExtra("hostID");

        databaseJobApply = FirebaseDatabase.getInstance().getReference("UserInformation").child(uid).child("JobApply").child(id);

        databaseUserContact.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String poster = dataSnapshot.child("email").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);

                databaseJobApply.child("email").setValue(poster);
                databaseJobApply.child("phone").setValue(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Job job = new Job(image, id, jobCategories, jobTitle, jobDescription, jobWorkDate, jobWorkTime, location, salary, hostID);
        //save host's phone and email
        // solve the image getter
        databaseJobApply.setValue(job);
    }
}
