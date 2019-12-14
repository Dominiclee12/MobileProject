package com.example.mobileproject;

public class Job {
    String imageUrl;
    String jobID;
    String jobCategories;
    String jobTitle;
    String jobDescription;
    String workDate;
    String workTime;
    String location;
    String salary;
    String hostID;

    public Job() {
    }

    public Job(String imageUrl, String jobID, String jobCategories, String jobTitle, String jobDescription, String workDate, String workTime, String location, String salary, String hostID) {
        this.imageUrl = imageUrl;
        this.jobID = jobID;
        this.jobCategories = jobCategories;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.workDate = workDate;
        this.workTime = workTime;
        this.location = location;
        this.salary = salary;
        this.hostID = hostID;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getJobCategories() {
        return jobCategories;
    }

    public void setJobCategories(String jobCategories) {
        this.jobCategories = jobCategories;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
