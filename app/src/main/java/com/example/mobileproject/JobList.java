package com.example.mobileproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class JobList extends ArrayAdapter<Job> {
    private Activity context;
    private List<Job> jobList;

    public JobList(Activity context, List<Job> jobList) {
        super(context, R.layout.activity_user_job_list, jobList);
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.activity_user_job_list, null, true);

        TextView textViewTitle = listViewItem.findViewById(R.id.tv_title);
        TextView textViewCat = listViewItem.findViewById(R.id.tv_cat);
        TextView textViewDate = listViewItem.findViewById(R.id.tv_date);
        TextView textViewTime = listViewItem.findViewById(R.id.tv_time);
        TextView textViewLocation = listViewItem.findViewById(R.id.tv_location);
        TextView textViewSalary = listViewItem.findViewById(R.id.tv_salary);

        Job job = jobList.get(position);

        textViewTitle.setText(job.getJobTitle());
        textViewCat.setText(job.getJobCategories());
        textViewDate.setText(job.getWorkDate());
        textViewTime.setText(job.getWorkTime());
        textViewLocation.setText(job.getLocation());
        textViewSalary.setText(job.getSalary());

        return listViewItem;
    }
}
