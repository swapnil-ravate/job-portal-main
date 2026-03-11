# 📱 Job Portal Android App

A production-ready Android Job Portal application built with **Java**, **Firebase Authentication**, **Firebase Realtime Database**, and **Firebase Storage**.

---

## ✨ Features

### Job Seeker
- Register and log in securely via Firebase Auth
- Browse all available job listings
- View full job details (salary, openings, skills, dates)
- Pick a PDF resume and apply directly in the app
- Track all submitted applications with live status updates (`pending` / `selected` / `rejected`)

### Admin
- Post new job listings with fields: title, company, salary, dates, openings, skills, and description
- View all received applications from job seekers
- Accept or Reject applicants — status synced for both admin and job seeker in real time
- View uploaded resumes (Firebase Storage PDF links)

---

## 🏗️ Tech Stack

| Component | Library / Technology |
|-----------|---------------------|
| Language | Java |
| Min SDK | 31 (Android 12) |
| Target SDK | 35 (Android 15) |
| Authentication | Firebase Auth |
| Database | Firebase Realtime Database |
| File Storage | Firebase Storage (PDF resumes) |
| RecyclerView | FirebaseUI Database `8.0.2` |
| Image Loading | Picasso `2.8` |
| Profile Image | CircleImageView `3.1.0` |
| UI | Material Design Components |

---

## 🗂️ Firebase Database Structure

```
Root
├── users
│   └── {userId}
│       ├── email: String
│       ├── role: "admin" | "jobseeker"
│       ├── displayName: String
│       ├── profilePic: String (URL)
│       └── lastLogin: Long (timestamp)
│
├── jobs
│   └── {jobId}
│       ├── jobId, adminId, companyName, jobTitle
│       ├── jobSalary, jobStartDate, jobLastDate
│       ├── totalOpenings, aboutJob, skillsRequired
│       └── additionalInfo
│
└── jobApplications
    └── {adminId or userId}
        └── {applicationId}
            ├── applicationId, userId, userName, userEmail
            ├── jobTitle, companyName, adminId
            ├── resumeUrl  ← Firebase Storage download URL
            └── status: "pending" | "selected" | "rejected"
```

---

## 📁 Project Structure

```
com.swapnil.jobportal/
│
├── Activities/
│   ├── StartingActivity.java       ← LAUNCHER / splash
│   ├── RegistrationActivity.java
│   ├── LoginActivity.java
│   ├── RoleActivity.java           ← Admin code: ADMIN123
│   ├── AdminActivity.java
│   └── JobDetailsActivity.java     ← PDF resume upload + apply
│
├── Fragments/
│   ├── DisplayJobFragment.java
│   ├── UserDashboardFragment.java
│   ├── UserAllApplicationsFragment.java
│   ├── UserPlacedApplicationsFragment.java
│   ├── UserProfileFragment.java
│   ├── AddJobFragment.java
│   ├── AdminDashboardFragment.java
│   ├── AdminAllApplicationsFragment.java
│   ├── AdminSelectedFragment.java
│   └── AdminProfileFragment.java
│
├── Adapters/
│   ├── JobsAdapter.java
│   ├── UserAllApplicationsAdapter.java
│   ├── UserPlacedApplicationAdapter.java
│   ├── AdminAllApplicationsAdapter.java
│   └── AdminSelectedApplicationAdapter.java
│
├── Model/
│   ├── UserModel.java
│   ├── JobModel.java
│   └── ApplicationModel.java
│
├── MainActivity.java               ← Job Seeker dashboard
└── MyApplication.java              ← Firebase init + offline persistence
```

---

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog or newer
- A Firebase project with **Auth**, **Realtime Database**, and **Storage** enabled
- Your own `google-services.json`

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/swapnil-ravate/job-portal-main.git
   cd job-portal-main
   ```

2. **Add your Firebase config:**
   - Place your `google-services.json` inside `app/`

3. **Set Firebase Security Rules:**

   **Realtime Database:**
   ```json
   {
     "rules": {
       "users": { "$uid": { ".read": "$uid === auth.uid", ".write": "$uid === auth.uid" } },
       "jobs": { ".read": "auth != null", ".write": "auth != null" },
       "jobApplications": { ".read": "auth != null", ".write": "auth != null" }
     }
   }
   ```

   **Firebase Storage:**
   ```
   match /resumes/{userId}/{fileName} {
     allow read: if request.auth != null;
     allow write: if request.auth != null && request.auth.uid == userId;
   }
   ```

4. **Sync Gradle** in Android Studio and run the app.

---

## 🔐 Admin Access

When selecting a role after first login, choosing **Admin** will prompt for a secret code.

> **Admin code:** `ADMIN123`

---

## 📋 Application Flow

```
StartingActivity
    ├── Already logged in? → checkRole → AdminActivity / MainActivity
    ├── LOGIN  → LoginActivity  → checkRole → AdminActivity / MainActivity
    └── SIGN UP → RegistrationActivity → LoginActivity

LoginActivity → after login → checkRole
    ├── role == "admin"     → AdminActivity
    ├── role == "jobseeker" → MainActivity
    └── role == ""          → RoleActivity → assign role → dashboard
```

---

## 📦 Dependencies (app/build.gradle)

```groovy
implementation platform('com.google.firebase:firebase-bom:33.0.0')
implementation 'com.google.firebase:firebase-auth'
implementation 'com.google.firebase:firebase-database'
implementation 'com.google.firebase:firebase-storage'
implementation 'com.google.firebase:firebase-analytics'
implementation 'com.firebaseui:firebase-ui-database:8.0.2'
implementation 'com.squareup.picasso:picasso:2.8'
implementation 'de.hdodenhof:circleimageview:3.1.0'
implementation 'com.google.android.material:material:1.12.0'
```

---

## 📝 License

This project is for educational purposes.