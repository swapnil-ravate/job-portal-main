package com.example.jobportalapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportalapp.Fragments.UserAllApplicationsFragment;
import com.example.jobportalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView companyNameTxt, jobTitleTxt, jobDescriptionTxt, jobSalaryTxt;
    private TextView startDateTxt, lastDateTxt, totalOpeningsTxt, requiredSkillsTxt;
    private TextView additionalInfoTxt, selectedFileNameTxt;
    private Button applyJobBtn, uploadResumeBtn;

    private String userId, userName, adminId;
    private String companyName, jobTitle, jobDescription, jobSalary;
    private String startDate, lastDate, totalOpenings, requiredSkills, additionalInfo;
    private Uri resumeUri;

    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            (ActivityResultCallback<Uri>) result -> {
                if (result != null) {
                    resumeUri = result;
                    selectedFileNameTxt.setText("Selected: " + result.getLastPathSegment());
                    Log.d("ResumeSelection", "Selected URI: " + result.toString());
                } else {
                    selectedFileNameTxt.setText("No file selected");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to UserAllApplicationsFragment when back is pressed
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.UserFragmentContainer, new UserAllApplicationsFragment()) // Make sure to use your container's ID
                        .addToBackStack(null) // Add to back stack for proper navigation behavior
                        .commit();
            }
        });



        // Get job details from intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            companyName = bundle.getString("companyName", "");
            jobTitle = bundle.getString("jobTitle", "");
            jobDescription = bundle.getString("jobDescription", "");
            jobSalary = bundle.getString("jobSalary", "");
            startDate = bundle.getString("startDate", "");
            lastDate = bundle.getString("lastDate", "");
            totalOpenings = bundle.getString("totalOpenings", "");
            requiredSkills = bundle.getString("requiredSkills", "");
            additionalInfo = bundle.getString("additionalInfo", "");
            adminId = bundle.getString("userId", ""); // admin who posted the job

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                userId = currentUser.getUid();
                userName = currentUser.getDisplayName();
                if (userName == null || userName.isEmpty()) {
                    userName = currentUser.getEmail(); // fallback
                }
            }
        }

        // Assign views
        companyNameTxt = findViewById(R.id.CompanyNameTxt);
        jobTitleTxt = findViewById(R.id.JobTitleTxt);
        jobDescriptionTxt = findViewById(R.id.JobDescriptionTxt);
        jobSalaryTxt = findViewById(R.id.SalaryTxt);
        startDateTxt = findViewById(R.id.JobStartDateTxt);
        lastDateTxt = findViewById(R.id.LastDateToApplyTxt);
        totalOpeningsTxt = findViewById(R.id.TotolNoOfOpeningsTxt);
        requiredSkillsTxt = findViewById(R.id.RequiredSkillsTxt);
        additionalInfoTxt = findViewById(R.id.AdditionalDataTxt);
        selectedFileNameTxt = findViewById(R.id.SelectedFileNameTxt);
        uploadResumeBtn = findViewById(R.id.SelectResumeBtn);
        applyJobBtn = findViewById(R.id.ApplyJobBtn);

        // Set job details
        companyNameTxt.setText("Company: " + companyName);
        jobTitleTxt.setText("Title: " + jobTitle);
        jobDescriptionTxt.setText("About: " + jobDescription);
        jobSalaryTxt.setText("Salary: " + jobSalary);
        startDateTxt.setText("Start Date: " + startDate);
        lastDateTxt.setText("Last Date: " + lastDate);
        totalOpeningsTxt.setText("Openings: " + totalOpenings);
        requiredSkillsTxt.setText("Skills: " + requiredSkills);
        additionalInfoTxt.setText("Info: " + additionalInfo);

        // Resume file picker
        uploadResumeBtn.setOnClickListener(view -> getContentLauncher.launch("application/pdf"));

        // Apply job
        applyJobBtn.setOnClickListener(view -> {
            if (resumeUri == null) {
                Toast.makeText(this, "Please upload a resume", Toast.LENGTH_SHORT).show();
            } else {
                checkIfAlreadyApplied(resumeUri.toString());
            }
        });
    }

    private void checkIfAlreadyApplied(String resumeLink) {
        FirebaseDatabase.getInstance().getReference().child("jobApplications")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean alreadyApplied = false;

                        for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                            String existingTitle = applicationSnapshot.child("jobTitle").getValue(String.class);
                            String existingCompany = applicationSnapshot.child("companyName").getValue(String.class);
                            String existingAdmin = applicationSnapshot.child("adminId").getValue(String.class);

                            if (existingTitle != null && existingCompany != null && existingAdmin != null &&
                                    existingTitle.equalsIgnoreCase(jobTitle) &&
                                    existingCompany.equalsIgnoreCase(companyName) &&
                                    existingAdmin.equalsIgnoreCase(adminId)) {
                                alreadyApplied = true;
                                break;
                            }
                        }

                        if (alreadyApplied) {
                            Toast.makeText(JobDetailsActivity.this, "You already applied for this job", Toast.LENGTH_SHORT).show();
                        } else {
                            submitJobApplication(resumeLink);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(JobDetailsActivity.this, "Error checking application", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitJobApplication(String resumeLink) {
        String key = FirebaseDatabase.getInstance().getReference().child("jobApplications").push().getKey();
        if (key == null) return;

        HashMap<String, Object> applicationData = new HashMap<>();
        applicationData.put("userId", userId);
        applicationData.put("userName", userName);
        applicationData.put("jobTitle", jobTitle);
        applicationData.put("companyName", companyName);
        applicationData.put("resumeLink", resumeLink);
        applicationData.put("adminId", adminId);

        // Save to admin's and user's application list
        FirebaseDatabase.getInstance().getReference().child("jobApplications")
                .child(adminId).child(key).setValue(applicationData)
                .addOnSuccessListener(aVoid -> {
                    FirebaseDatabase.getInstance().getReference().child("jobApplications")
                            .child(userId).child(key).setValue(applicationData)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(JobDetailsActivity.this, "Successfully Applied", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(JobDetailsActivity.this, "User save failed", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(JobDetailsActivity.this, "Application failed", Toast.LENGTH_SHORT).show());
    }
}
