package com.swapnil.jobportal.Model;

/**
 * ApplicationModel represents a job application in the Firebase Realtime Database.
 * Stored under: jobApplications/{adminId}/{applicationId}
 *          and: jobApplications/{userId}/{applicationId}
 */
public class ApplicationModel {

    private String applicationId;
    private String userId;
    private String userName;
    private String userEmail;
    private String jobTitle;
    private String companyName;
    private String adminId;
    private String resumeUrl;    // Firebase Storage download URL (NOT local URI)
    private String status;       // "pending", "selected", or "rejected"

    // Default constructor required for Firebase deserialization
    public ApplicationModel() {}

    // Parameterized constructor
    public ApplicationModel(String applicationId, String userId, String userName,
                            String userEmail, String jobTitle, String companyName,
                            String adminId, String resumeUrl, String status) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.adminId = adminId;
        this.resumeUrl = resumeUrl;
        this.status = status;
    }

    // ---- Getters ----

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public String getStatus() {
        return status;
    }

    // ---- Setters ----

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
