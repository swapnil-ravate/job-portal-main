package com.swapnil.jobportal.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.swapnil.jobportal.Model.ApplicationModel;
import com.swapnil.jobportal.R;

/**
 * JobDetailsActivity — displays full details of a job and allows the user to apply.
 * The user must pick a PDF resume, which is uploaded to Firebase Storage before saving.
 */
public class JobDetailsActivity extends AppCompatActivity {

    // Job data passed via Intent
    private String jobId;
    private String adminId;
    private String companyName;
    private String jobTitle;
    private String jobDescription;
    private String jobSalary;
    private String startDate;
    private String lastDate;
    private String totalOpenings;
    private String requiredSkills;
    private String additionalInfo;

    // UI
    private ProgressBar progressBar;
    private Button selectResumeBtn;
    private Button applyBtn;
    private TextView selectedFileNameTv;

    // Resume
    private Uri selectedResumeUri = null;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    // Activity Result Launcher for PDF picker
    private final ActivityResultLauncher<String> pdfPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedResumeUri = uri;
                    // Extract and display the filename
                    String path = uri.getPath();
                    String fileName = path != null
                            ? path.substring(path.lastIndexOf('/') + 1)
                            : "resume.pdf";
                    selectedFileNameTv.setText("Selected: " + fileName);
                    selectedFileNameTv.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        // Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("jobApplications");
        storageReference = FirebaseStorage.getInstance().getReference("resumes");

        // Get job data from Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jobId = extras.getString("jobId", "");
            adminId = extras.getString("userId", "");    // key "userId" holds adminId per spec
            companyName = extras.getString("companyName", "");
            jobTitle = extras.getString("jobTitle", "");
            jobDescription = extras.getString("jobDescription", "");
            jobSalary = extras.getString("jobSalary", "");
            startDate = extras.getString("startDate", "");
            lastDate = extras.getString("lastDate", "");
            totalOpenings = extras.getString("totalOpenings", "");
            requiredSkills = extras.getString("requiredSkills", "");
            additionalInfo = extras.getString("additionalInfo", "");
        }

        // Bind UI views
        progressBar = findViewById(R.id.ProgressBar);
        selectResumeBtn = findViewById(R.id.SelectResumeBtn);
        applyBtn = findViewById(R.id.ApplyBtn);
        selectedFileNameTv = findViewById(R.id.SelectedFileNameTv);
        progressBar.setVisibility(View.INVISIBLE);
        selectedFileNameTv.setVisibility(View.GONE);

        // Populate TextViews with job details
        setTextView(R.id.CompanyNameTv, companyName);
        setTextView(R.id.JobTitleTv, jobTitle);
        setTextView(R.id.JobSalaryTv, "Salary: " + jobSalary);
        setTextView(R.id.JobStartDateTv, "Start Date: " + startDate);
        setTextView(R.id.JobLastDateTv, "Apply By: " + lastDate);
        setTextView(R.id.TotalOpeningsTv, "Openings: " + totalOpenings);
        setTextView(R.id.AboutJobTv, jobDescription);
        setTextView(R.id.SkillsRequiredTv, "Skills: " + requiredSkills);
        setTextView(R.id.AdditionalInfoTv, additionalInfo);

        // Select Resume button — open PDF file picker
        selectResumeBtn.setOnClickListener(view ->
                pdfPickerLauncher.launch("application/pdf"));

        // Apply button
        applyBtn.setOnClickListener(view -> handleApply());

        // Back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Validates that resume is selected, then checks for duplicate applications.
     */
    private void handleApply() {
        if (selectedResumeUri == null) {
            Toast.makeText(this, "Please select your resume (PDF) first.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to apply.", Toast.LENGTH_SHORT).show();
            return;
        }

        checkIfAlreadyApplied(currentUser);
    }

    /**
     * Checks if this user has already applied for this job to prevent duplicate applications.
     */
    private void checkIfAlreadyApplied(FirebaseUser currentUser) {
        progressBar.setVisibility(View.VISIBLE);
        applyBtn.setEnabled(false);

        String userId = currentUser.getUid();

        // Applications for this user are stored under jobApplications/{userId}
        databaseReference.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean alreadyApplied = false;
                        for (DataSnapshot appSnapshot : snapshot.getChildren()) {
                            ApplicationModel app = appSnapshot.getValue(ApplicationModel.class);
                            if (app != null && jobId.equals(app.getApplicationId() != null
                                    ? null : null)) {
                                // Check by jobTitle + companyName combination
                            }
                            // Check by jobTitle and companyName to detect duplicates
                            if (app != null
                                    && jobTitle.equals(app.getJobTitle())
                                    && companyName.equals(app.getCompanyName())) {
                                alreadyApplied = true;
                                break;
                            }
                        }

                        if (alreadyApplied) {
                            progressBar.setVisibility(View.INVISIBLE);
                            applyBtn.setEnabled(true);
                            Toast.makeText(JobDetailsActivity.this,
                                    "You have already applied for this job.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            uploadResumeToStorage(currentUser);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        applyBtn.setEnabled(true);
                        Toast.makeText(JobDetailsActivity.this,
                                "Error checking application status.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Uploads the selected PDF resume to Firebase Storage and then saves the application.
     * Path: resumes/{userId}/{jobId}_{timestamp}.pdf
     */
    private void uploadResumeToStorage(FirebaseUser currentUser) {
        String userId = currentUser.getUid();
        long timestamp = System.currentTimeMillis();
        String fileName = jobId + "_" + timestamp + ".pdf";

        StorageReference resumeRef = storageReference.child(userId).child(fileName);

        resumeRef.putFile(selectedResumeUri)
                .addOnProgressListener(taskSnapshot -> {
                    // Could show upload progress here if needed
                })
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    resumeRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                // Submit application with the Firebase Storage URL
                                submitJobApplication(currentUser, downloadUri.toString());
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                applyBtn.setEnabled(true);
                                Toast.makeText(JobDetailsActivity.this,
                                        "Failed to get resume URL: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    applyBtn.setEnabled(true);
                    Toast.makeText(this, "Resume upload failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Saves the ApplicationModel to both admin and user paths in the database.
     * Keys:
     *   jobApplications/{adminId}/{applicationId}
     *   jobApplications/{userId}/{applicationId}
     *
     * @param resumeUrl Firebase Storage download URL (NEVER a local file URI)
     */
    private void submitJobApplication(FirebaseUser currentUser, String resumeUrl) {
        String userId = currentUser.getUid();
        String userName = currentUser.getDisplayName() != null
                ? currentUser.getDisplayName() : currentUser.getEmail();
        String userEmail = currentUser.getEmail();

        // Generate a unique application ID
        String applicationId = databaseReference.child(adminId).push().getKey();
        if (applicationId == null) {
            progressBar.setVisibility(View.INVISIBLE);
            applyBtn.setEnabled(true);
            Toast.makeText(this, "Failed to generate application ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApplicationModel application = new ApplicationModel(
                applicationId, userId, userName, userEmail,
                jobTitle, companyName, adminId, resumeUrl, "pending");

        // Save to admin's path: jobApplications/{adminId}/{applicationId}
        databaseReference.child(adminId).child(applicationId).setValue(application)
                .addOnSuccessListener(unused1 -> {
                    // Also save to user's path: jobApplications/{userId}/{applicationId}
                    databaseReference.child(userId).child(applicationId).setValue(application)
                            .addOnSuccessListener(unused2 -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                applyBtn.setEnabled(true);
                                Toast.makeText(JobDetailsActivity.this,
                                        "Application submitted successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                applyBtn.setEnabled(true);
                                Toast.makeText(JobDetailsActivity.this,
                                        "Failed to save application: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    applyBtn.setEnabled(true);
                    Toast.makeText(this,
                            "Failed to submit application: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setTextView(int viewId, String text) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setText(text != null ? text : "");
        }
    }
}
