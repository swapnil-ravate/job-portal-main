package com.swapnil.jobportal.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.swapnil.jobportal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddJobFragment extends Fragment {

    // Declare all the EditTexts
    EditText companyNameEditTxt, jobTitleEditTxt, jobSalaryEditTxt, jobStartDateEditTxt, jobLastDateEditTxt;
    EditText totalOpeningsEditTxt, aboutJobEditTxt, skillsRequiredEditTxt, additionalInfoEditTxt;
    Button addJobBtn;

    public AddJobFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_job, container, false);

        // Assign UI elements
        companyNameEditTxt = view.findViewById(R.id.CompanyNameEditTxt);
        jobTitleEditTxt = view.findViewById(R.id.JobTitleEditTxt);
        jobSalaryEditTxt = view.findViewById(R.id.JobSalaryEditTxt);
        jobStartDateEditTxt = view.findViewById(R.id.JobStartDateEditTxt);
        jobLastDateEditTxt = view.findViewById(R.id.JobLastDateEditTxt);
        totalOpeningsEditTxt = view.findViewById(R.id.TotalOpeningsEditTxt);
        aboutJobEditTxt = view.findViewById(R.id.AboutJobEditTxt);
        skillsRequiredEditTxt = view.findViewById(R.id.SkillsRequiredEditTxt);
        additionalInfoEditTxt = view.findViewById(R.id.AddationalInfoEditTxt); // Watch for typo in XML too

        addJobBtn = view.findViewById(R.id.AddJobBtn);
        addJobBtn.setOnClickListener(view1 -> {
            String companyName = companyNameEditTxt.getText().toString().trim();
            String jobTitle = jobTitleEditTxt.getText().toString().trim();
            String jobSalary = jobSalaryEditTxt.getText().toString().trim();
            String jobStartDate = jobStartDateEditTxt.getText().toString().trim();
            String jobLastDate = jobLastDateEditTxt.getText().toString().trim();
            String totalOpenings = totalOpeningsEditTxt.getText().toString().trim();
            String aboutJob = aboutJobEditTxt.getText().toString().trim();
            String skillsRequired = skillsRequiredEditTxt.getText().toString().trim();
            String additionalInfo = additionalInfoEditTxt.getText().toString().trim();

            if (companyName.isEmpty() || jobTitle.isEmpty() || jobSalary.isEmpty() || jobStartDate.isEmpty()
                    || jobLastDate.isEmpty() || totalOpenings.isEmpty() || aboutJob.isEmpty() || skillsRequired.isEmpty()) {
                Toast.makeText(getContext(), "Please, Enter All Required Details", Toast.LENGTH_SHORT).show();
            } else {
                checkAndAddJob(companyName, jobTitle, jobSalary, jobStartDate, jobLastDate,
                        totalOpenings, aboutJob, skillsRequired, additionalInfo);
            }
        });

        return view;
    }

    private void checkAndAddJob(String companyName, String jobTitle, String jobSalary, String jobStartDate,
                                String jobLastDate, String totalOpenings, String aboutJob,
                                String skillsRequired, String additionalInfo) {

        String adminId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (adminId == null) {
            Toast.makeText(getContext(), "No user logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("jobs")
                .orderByChild("adminId")
                .equalTo(adminId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean alreadyPosted = false;

                        for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                            String existingTitle = jobSnapshot.child("jobTitle").getValue(String.class);
                            String existingCompany = jobSnapshot.child("companyName").getValue(String.class);

                            if (existingTitle != null && existingCompany != null &&
                                    existingTitle.equalsIgnoreCase(jobTitle) &&
                                    existingCompany.equalsIgnoreCase(companyName)) {
                                alreadyPosted = true;
                                break;
                            }
                        }

                        if (alreadyPosted) {
                            Toast.makeText(getContext(), "This job has already been posted.", Toast.LENGTH_SHORT).show();
                        } else {
                            addJobToDatabase(adminId, companyName, jobTitle, jobSalary, jobStartDate,
                                    jobLastDate, totalOpenings, aboutJob, skillsRequired, additionalInfo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error checking for existing jobs", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addJobToDatabase(String adminId, String companyName, String jobTitle, String jobSalary, String jobStartDate,
                                  String jobLastDate, String totalOpenings, String aboutJob,
                                  String skillsRequired, String additionalInfo) {

        String key = FirebaseDatabase.getInstance().getReference().child("jobs").push().getKey();
        if (key == null) {
            Toast.makeText(getContext(), "Error generating job key", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> jobDetails = new HashMap<>();
        jobDetails.put("companyName", companyName);
        jobDetails.put("jobTitle", jobTitle);
        jobDetails.put("jobSalary", jobSalary);
        jobDetails.put("jobStartDate", jobStartDate);
        jobDetails.put("jobLastDate", jobLastDate);
        jobDetails.put("totalOpenings", totalOpenings);
        jobDetails.put("aboutJob", aboutJob);
        jobDetails.put("skillsRequired", skillsRequired);
        jobDetails.put("additionalInfo", additionalInfo);
        jobDetails.put("adminId", adminId);

        FirebaseDatabase.getInstance().getReference().child("jobs")
                .child(key)
                .updateChildren(jobDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Job Details Added Successfully", Toast.LENGTH_SHORT).show();

                        companyNameEditTxt.setText("");
                        jobTitleEditTxt.setText("");
                        jobSalaryEditTxt.setText("");
                        jobStartDateEditTxt.setText("");
                        jobLastDateEditTxt.setText("");
                        totalOpeningsEditTxt.setText("");
                        aboutJobEditTxt.setText("");
                        skillsRequiredEditTxt.setText("");
                        additionalInfoEditTxt.setText("");
                    } else {
                        Toast.makeText(getContext(), "Failed to add job details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
