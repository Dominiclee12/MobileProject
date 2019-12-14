package com.example.mobileproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.ByteArrayOutputStream;

public class JobListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference databaseJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseJobs = mFirebaseDatabase.getReference("Job");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu, this adds items to the action bar if it present
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                firebaseSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Filter as you type
                firebaseSearch(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //handle other action bar item clicks here
        if (id == R.id.action_settings) {
            //TODO
            startActivity(new Intent(this, ProfileActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    //Load firebase data into recyclerview
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Job, FirebaseViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Job, FirebaseViewHolder>(
                        Job.class,
                        R.layout.cardview,
                        FirebaseViewHolder.class,
                        databaseJobs
                ) {
                    @Override
                    protected void populateViewHolder(FirebaseViewHolder firebaseViewHolder, Job job, int i) {
                        firebaseViewHolder.setDetails(getApplicationContext(), job.getImageUrl(), job.getJobTitle(), job.getSalary(), job.getJobDescription(),
                                job.getLocation(), job.getJobCategories(), job.getWorkDate(), job.getWorkTime(), job.getJobID(), job.getHostID());
                    }

                    @Override
                    public FirebaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        FirebaseViewHolder firebaseViewHolder =  super.onCreateViewHolder(parent, viewType);

                        firebaseViewHolder.setOnClickListener(new FirebaseViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //Views
                                TextView mtvImageUrl = view.findViewById(R.id.tv_image_url);
                                TextView mtvJobTitle = view.findViewById(R.id.tv_job_title);
                                TextView mtvJobCategory = view.findViewById(R.id.tv_job_category);
                                TextView mtvSalary = view.findViewById(R.id.tv_salary);
                                TextView mtvLocation = view.findViewById(R.id.tv_location);
                                TextView mtvJobDescription = view.findViewById(R.id.tv_job_description);
                                TextView mtvWorkDate = view.findViewById(R.id.tv_work_date);
                                TextView mtvWorkTime = view.findViewById(R.id.tv_work_time);
                                TextView mtvJobID = view.findViewById(R.id.tv_job_id);
                                TextView mtvHostID = view.findViewById(R.id.tv_host_id);
                                ImageView mivJobPhoto = view.findViewById(R.id.iv_photo);

                                //get data from views
                                String imageUrl = mtvImageUrl.getText().toString();
                                Drawable mDrawable = mivJobPhoto.getDrawable();
                                Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
                                String jobTitle = mtvJobTitle.getText().toString();
                                String jobCategory = mtvJobCategory.getText().toString();
                                String salary = mtvSalary.getText().toString();
                                String location = mtvLocation.getText().toString();
                                String jobDescription = mtvJobDescription.getText().toString();
                                String workDate = mtvWorkDate.getText().toString();
                                String workTime = mtvWorkTime.getText().toString();
                                String jobID = mtvJobID.getText().toString();
                                String hostID = mtvHostID.getText().toString();

                                //pass this data to new activity
                                Intent intent = new Intent(view.getContext(), JobDetailActivity.class);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] bytes = stream.toByteArray();
                                intent.putExtra("imageUrl", imageUrl);
                                intent.putExtra("image", bytes);
                                intent.putExtra("jobTitle", jobTitle);
                                intent.putExtra("jobCategory", jobCategory);
                                intent.putExtra("salary", salary);
                                intent.putExtra("location", location);
                                intent.putExtra("jobDescription", jobDescription);
                                intent.putExtra("workDate", workDate);
                                intent.putExtra("workTime", workTime);
                                intent.putExtra("jobID", jobID);
                                intent.putExtra("hostID", hostID);

                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO do you own implementation on long item click
                            }
                        });
                        return firebaseViewHolder;
                    }
                };

        //Set adapter into recyclerview
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void firebaseSearch(String searchText) {
        final Query firebaseSearchQuery = databaseJobs.orderByChild("jobTitle").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Job, FirebaseViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Job, FirebaseViewHolder>(
                        Job.class,
                        R.layout.cardview,
                        FirebaseViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(FirebaseViewHolder firebaseViewHolder, Job job, int i) {
                        firebaseViewHolder.setDetails(getApplicationContext(), job.getImageUrl(), job.getJobTitle(), job.getSalary(), job.getJobDescription(), job.getLocation(), job.getJobCategories(),
                                job.getWorkDate(), job.getWorkTime(), job.getJobID(), job.getHostID());
                    }

                    @Override
                    public FirebaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        FirebaseViewHolder firebaseViewHolder =  super.onCreateViewHolder(parent, viewType);

                        firebaseViewHolder.setOnClickListener(new FirebaseViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //Views
                                TextView mtvJobTitle = view.findViewById(R.id.tv_job_title);
                                TextView mtvJobCategory = view.findViewById(R.id.tv_job_category);
                                TextView mtvSalary = view.findViewById(R.id.tv_salary);
                                TextView mtvLocation = view.findViewById(R.id.tv_location);
                                TextView mtvJobDescription = view.findViewById(R.id.tv_job_description);
                                TextView mtvWorkDate = view.findViewById(R.id.tv_work_date);
                                TextView mtvWorkTime = view.findViewById(R.id.tv_work_time);
                                TextView mtvJobID = view.findViewById(R.id.tv_job_id);
                                TextView mtvHostID = view.findViewById(R.id.tv_host_id);
                                ImageView mivJobPhoto = view.findViewById(R.id.iv_photo);

                                //get data from views
                                Drawable mDrawable = mivJobPhoto.getDrawable();
                                Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
                                String jobTitle = mtvJobTitle.getText().toString();
                                String jobCategory = mtvJobCategory.getText().toString();
                                String salary = mtvSalary.getText().toString();
                                String location = mtvLocation.getText().toString();
                                String workDate = mtvWorkDate.getText().toString();
                                String workTime = mtvWorkTime.getText().toString();
                                String jobID = mtvJobID.getText().toString();
                                String hostID = mtvHostID.getText().toString();
                                String jobDescription = mtvJobDescription.getText().toString();

                                //pass this data to new activity
                                Intent intent = new Intent(view.getContext(), JobDetailActivity.class);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] bytes = stream.toByteArray();
                                intent.putExtra("image", bytes);
                                intent.putExtra("jobTitle", jobTitle);
                                intent.putExtra("jobCategory", jobCategory);
                                intent.putExtra("salary", salary);
                                intent.putExtra("location", location);
                                intent.putExtra("jobDescription", jobDescription);
                                intent.putExtra("workDate", workDate);
                                intent.putExtra("workTime", workTime);
                                intent.putExtra("jobID", jobID);
                                intent.putExtra("hostID", hostID);

                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO do you own implementation on long item click
                            }
                        });
                        return firebaseViewHolder;
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
