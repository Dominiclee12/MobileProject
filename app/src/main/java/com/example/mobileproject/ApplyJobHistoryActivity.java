package com.example.mobileproject;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ApplyJobHistoryActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseJobApply;

    ListView lv_job;
    List<Job> jobList;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job_history);

        user = auth.getInstance().getCurrentUser();
        uid = user.getUid();

        databaseJobApply = FirebaseDatabase.getInstance().getReference("UserInformation").child(uid).child("JobApply");
        lv_job = findViewById(R.id.lv_job);
        jobList = new ArrayList<>();
        viewData1();
    }

    protected void viewData1() {
        super.onStart();
        Toast.makeText(this, "Jobs applied is shown.", Toast.LENGTH_SHORT).show();
        databaseJobApply.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    Job job = jobSnapshot.getValue(Job.class);

                    jobList.add(job);
                }
                JobList adapter = new JobList(ApplyJobHistoryActivity.this, jobList);
                lv_job.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
