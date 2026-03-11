package com.swapnil.jobportal.Model;

/**
 * JobModel represents a job posting in the Firebase Realtime Database.
 * Stored under: jobs/{jobId}
 */
public class JobModel {

    private String jobId;
    private String adminId;
    private String companyName;
    private String jobTitle;
    private String jobSalary;
    private String jobStartDate;
    private String jobLastDate;
    private String totalOpenings;
    private String aboutJob;
    private String skillsRequired;
    private String additionalInfo;

    // Default constructor required for Firebase deserialization
    public JobModel() {}

    // Parameterized constructor
    public JobModel(String jobId, String adminId, String companyName, String jobTitle,
                    String jobSalary, String jobStartDate, String jobLastDate,
                    String totalOpenings, String aboutJob, String skillsRequired,
                    String additionalInfo) {
        this.jobId = jobId;
        this.adminId = adminId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.jobSalary = jobSalary;
        this.jobStartDate = jobStartDate;
        this.jobLastDate = jobLastDate;
        this.totalOpenings = totalOpenings;
        this.aboutJob = aboutJob;
        this.skillsRequired = skillsRequired;
        this.additionalInfo = additionalInfo;
    }

    // ---- Getters ----

    public String getJobId() {
        return jobId;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobSalary() {
        return jobSalary;
    }

    public String getJobStartDate() {
        return jobStartDate;
    }

    public String getJobLastDate() {
        return jobLastDate;
    }

    public String getTotalOpenings() {
        return totalOpenings;
    }

    public String getAboutJob() {
        return aboutJob;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    // ---- Setters ----

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setJobSalary(String jobSalary) {
        this.jobSalary = jobSalary;
    }

    public void setJobStartDate(String jobStartDate) {
        this.jobStartDate = jobStartDate;
    }

    public void setJobLastDate(String jobLastDate) {
        this.jobLastDate = jobLastDate;
    }

    public void setTotalOpenings(String totalOpenings) {
        this.totalOpenings = totalOpenings;
    }

    public void setAboutJob(String aboutJob) {
        this.aboutJob = aboutJob;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
