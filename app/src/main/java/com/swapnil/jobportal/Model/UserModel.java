package com.swapnil.jobportal.Model;

/**
 * UserModel represents a user in the Firebase Realtime Database.
 * Stored under: users/{userId}
 */
public class UserModel {

    private String userId;
    private String email;
    private String role;         // "admin" or "jobseeker"
    private String displayName;
    private String profilePic;   // URL to profile picture
    private long lastLogin;      // Unix timestamp

    // Default constructor required for Firebase deserialization
    public UserModel() {}

    // Parameterized constructor
    public UserModel(String userId, String email, String role, String displayName,
                     String profilePic, long lastLogin) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.displayName = displayName;
        this.profilePic = profilePic;
        this.lastLogin = lastLogin;
    }

    // ---- Getters ----

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    // ---- Setters ----

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }
}
