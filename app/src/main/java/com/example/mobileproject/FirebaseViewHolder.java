package com.example.mobileproject;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public FirebaseViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        //item long click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
                return false;
            }
        });
    }

    public void setDetails(Context ctx, String image, String jobTitle, String salary, String jobDescription, String location, String jobCategory,
                           String workDate, String workTime, String jobID, String hostID) {
        ImageView mImage = mView.findViewById(R.id.iv_photo);
        TextView mImageUrl = mView.findViewById(R.id.tv_image_url);
        TextView mJobTitle = mView.findViewById(R.id.tv_job_title);
        TextView mJobCategory = mView.findViewById(R.id.tv_job_category);
        TextView mSalary = mView.findViewById(R.id.tv_salary);
        TextView mJobDescription = mView.findViewById(R.id.tv_job_description);
        TextView mLocation = mView.findViewById(R.id.tv_location);
        TextView mWorkDate = mView.findViewById(R.id.tv_work_date);
        TextView mWorkTime = mView.findViewById(R.id.tv_work_time);
        TextView mJobID = mView.findViewById(R.id.tv_job_id);
        TextView mHostID = mView.findViewById(R.id.tv_host_id);


        Picasso.get().load(image).placeholder(R.mipmap.ic_launcher_round).into(mImage);
        mImageUrl.setText(image);
        mJobTitle.setText(jobTitle);
        mJobCategory.setText(jobCategory);
        mSalary.setText(salary);
        mJobDescription.setText(jobDescription);
        mLocation.setText(location);
        mWorkDate.setText(workDate);
        mWorkTime.setText(workTime);
        mJobID.setText(jobID);
        mHostID.setText(hostID);
    }

    private FirebaseViewHolder.ClickListener mClickListener;

    //interface to send callbacks
    public interface ClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(FirebaseViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
