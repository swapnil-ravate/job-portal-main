package com.swapnil.jobportal.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swapnil.jobportal.Model.JobModel;
import com.swapnil.jobportal.R;

/**
 * AddJobFragment — allows an admin to post a new job listing.
 * Prevents duplicate job postings (same title + company by the same admin).
 */
public class AddJobFragment extends Fragment {

    private EditText companyNameEt;
    private EditText jobTitleEt;
    private EditText jobSalaryEt;
    private EditText jobStartDateEt;
    private EditText jobLastDateEt;
    private EditText totalOpeningsEt;
    private EditText aboutJobEt;
    private EditText skillsRequiredEt;
    private EditText additionalInfoEt;  // Fixed typo from AdditionalInfoEditTxt
    private Button addJobBtn;

    private DatabaseReference jobsRef;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_job, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");

        // Bind views (fixed typo: AdditionalInfoEditTxt)
        companyNameEt = view.findViewById(R.id.CompanyNameEditTxt);
        jobTitleEt = view.findViewById(R.id.JobTitleEditTxt);
        jobSalaryEt = view.findViewById(R.id.JobSalaryEditTxt);
        jobStartDateEt = view.findViewById(R.id.JobStartDateEditTxt);
        jobLastDateEt = view.findViewById(R.id.JobLastDateEditTxt);
        totalOpeningsEt = view.findViewById(R.id.TotalOpeningsEditTxt);
        aboutJobEt = view.findViewById(R.id.AboutJobEditTxt);
        skillsRequiredEt = view.findViewById(R.id.SkillsRequiredEditTxt);
        additionalInfoEt = view.findViewById(R.id.AdditionalInfoEditTxt);
        addJobBtn = view.findViewById(R.id.AddJobBtn);

        addJobBtn.setOnClickListener(v -> checkAndAddJob());

        return view;
    }

    /**
     * Validates all required fields before proceeding.
     * Additional info is optional.
     */
    private void checkAndAddJob() {
        String companyName = companyNameEt.getText().toString().trim();
        String jobTitle = jobTitleEt.getText().toString().trim();
        String jobSalary = jobSalaryEt.getText().toString().trim();
        String jobStartDate = jobStartDateEt.getText().toString().trim();
        String jobLastDate = jobLastDateEt.getText().toString().trim();
        String totalOpenings = totalOpeningsEt.getText().toString().trim();
        String aboutJob = aboutJobEt.getText().toString().trim();
        String skillsRequired = skillsRequiredEt.getText().toString().trim();
        String additionalInfo = additionalInfoEt.getText().toString().trim(); // optional

        // Validate required fields
        if (companyName.isEmpty()) { companyNameEt.setError("Required"); return; }
        if (jobTitle.isEmpty()) { jobTitleEt.setError("Required"); return; }
        if (jobSalary.isEmpty()) { jobSalaryEt.setError("Required"); return; }
        if (jobStartDate.isEmpty()) { jobStartDateEt.setError("Required"); return; }
        if (jobLastDate.isEmpty()) { jobLastDateEt.setError("Required"); return; }
        if (totalOpenings.isEmpty()) { totalOpeningsEt.setError("Required"); return; }
        if (aboutJob.isEmpty()) { aboutJobEt.setError("Required"); return; }
        if (skillsRequired.isEmpty()) { skillsRequiredEt.setError("Required"); return; }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String adminId = currentUser.getUid();
        addJobBtn.setEnabled(false);

        // Check for duplicate: same jobTitle + companyName posted by same admin
        jobsRef.orderByChild("adminId").equalTo(adminId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean isDuplicate = false;
                        for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                            JobModel job = jobSnapshot.getValue(JobModel.class);
                            if (job != null
                                    && jobTitle.equalsIgnoreCase(job.getJobTitle())
                                    && companyName.equalsIgnoreCase(job.getCompanyName())) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (isDuplicate) {
                            addJobBtn.setEnabled(true);
                            Toast.makeText(getContext(),
                                    "A job with this title and company already exists.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            addJobToDatabase(adminId, companyName, jobTitle, jobSalary,
                                    jobStartDate, jobLastDate, totalOpenings,
                                    aboutJob, skillsRequired, additionalInfo);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        addJobBtn.setEnabled(true);
                        Toast.makeText(getContext(),
                                "Error checking jobs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Pushes a new job entry to the "jobs" node with a Firebase-generated key.
     */
    private void addJobToDatabase(String adminId, String companyName, String jobTitle,
                                  String jobSalary, String jobStartDate, String jobLastDate,
                                  String totalOpenings, String aboutJob, String skillsRequired,
                                  String additionalInfo) {
        // Generate a unique key for this job
        String jobId = jobsRef.push().getKey();
        if (jobId == null) {
            addJobBtn.setEnabled(true);
            Toast.makeText(getContext(), "Failed to generate job ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        JobModel jobModel = new JobModel(jobId, adminId, companyName, jobTitle, jobSalary,
                jobStartDate, jobLastDate, totalOpenings, aboutJob, skillsRequired, additionalInfo);

        jobsRef.child(jobId).setValue(jobModel)
                .addOnSuccessListener(unused -> {
                    addJobBtn.setEnabled(true);
                    clearAllFields();
                    Toast.makeText(getContext(), "Job posted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    addJobBtn.setEnabled(true);
                    Toast.makeText(getContext(),
                            "Failed to post job: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Clears all form fields after successful submission.
     */
    private void clearAllFields() {
        companyNameEt.setText("");
        jobTitleEt.setText("");
        jobSalaryEt.setText("");
        jobStartDateEt.setText("");
        jobLastDateEt.setText("");
        totalOpeningsEt.setText("");
        aboutJobEt.setText("");
        skillsRequiredEt.setText("");
        additionalInfoEt.setText("");
    }
}
