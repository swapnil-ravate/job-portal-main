package com.swapnil.jobportal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swapnil.jobportal.Model.Model;
import com.swapnil.jobportal.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminAllApplicationsAdapter extends FirebaseRecyclerAdapter<Model, AdminAllApplicationsAdapter.Viewholder> {

    public AdminAllApplicationsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminAllApplicationsAdapter.Viewholder holder, int position, @NonNull Model model) {
        Context context = holder.itemView.getContext();

        String userName = model.getUserName();
        String jobTitle = model.getJobTitle();
        String resumeLink = model.getResumeLink();

        holder.txtTitle.setText(userName != null ? userName : context.getString(R.string.unknown_user));
        holder.txtDesc.setText(jobTitle != null ? "Job Title: " + jobTitle : context.getString(R.string.unknown_job_title));

        if (resumeLink != null && !resumeLink.isEmpty()) {
            holder.viewResumeBtn.setVisibility(View.VISIBLE);
            holder.viewResumeBtn.setOnClickListener(view -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(resumeLink), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to open resume. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("ResumeView", "Error opening resume URI: " + resumeLink, e);
                }
            });
        } else {
            holder.viewResumeBtn.setVisibility(View.GONE);
        }

        holder.acceptJobApplicationBtn.setOnClickListener(view -> {
            String adminId = model.getAdminId();
            String userId = model.getUserId();
            String companyName = model.getCompanyName();

            if (adminId != null && userId != null && jobTitle != null && companyName != null && userName != null) {
                acceptJobApplication(adminId, userId, jobTitle, companyName, userName, context, holder.acceptJobApplicationBtn);
            } else {
                showToast(context, context.getString(R.string.error_missing_data));
            }
        });
    }

    private void acceptJobApplication(String adminId, String userId, String jobTitle, String companyName, String userName, Context context, Button acceptBtn) {
        FirebaseDatabase.getInstance().getReference()
                .child("selectedApplications")
                .child(adminId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean alreadyAccepted = false;

                        for (DataSnapshot data : snapshot.getChildren()) {
                            String existingUserId = data.child("userId").getValue(String.class);
                            String existingJobTitle = data.child("jobTitle").getValue(String.class);
                            String existingCompanyName = data.child("companyName").getValue(String.class);

                            if (userId.equals(existingUserId) &&
                                    jobTitle.equalsIgnoreCase(existingJobTitle) &&
                                    companyName.equalsIgnoreCase(existingCompanyName)) {
                                alreadyAccepted = true;
                                break;
                            }
                        }

                        if (alreadyAccepted) {
                            showToast(context, "Already accepted");
                        } else {
                            String key = FirebaseDatabase.getInstance().getReference()
                                    .child("selectedApplications")
                                    .child(adminId)
                                    .push().getKey();

                            if (key == null) {
                                showToast(context, context.getString(R.string.error_failed_to_generate_key));
                                return;
                            }

                            HashMap<String, Object> applicationDetails = new HashMap<>();
                            applicationDetails.put("jobTitle", jobTitle);
                            applicationDetails.put("adminId", adminId);
                            applicationDetails.put("companyName", companyName);
                            applicationDetails.put("userId", userId);
                            applicationDetails.put("userName", userName);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("selectedApplications")
                                    .child(adminId)
                                    .child(key)
                                    .updateChildren(applicationDetails)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("selectedApplications")
                                                    .child(userId)
                                                    .child(key)
                                                    .updateChildren(applicationDetails)
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            showToast(context, "Application accepted");
                                                            acceptBtn.setEnabled(false);
                                                            acceptBtn.setText("Accepted");
                                                        } else {
                                                            Log.e("Firebase", "User update failed", task1.getException());
                                                            showToast(context, "Failed to save to user side");
                                                        }
                                                    });
                                        } else {
                                            Log.e("Firebase", "Admin update failed", task.getException());
                                            showToast(context, "Failed to save to admin side");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Database error: " + error.getMessage());
                        showToast(context, "Error accessing database");
                    }
                });
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_job_application_accept_file, parent, false);
        return new Viewholder(view);
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView txtusername, txtTitle, txtDesc;
        Button viewResumeBtn, acceptJobApplicationBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            txtusername = itemView.findViewById(R.id.username);
            txtTitle = itemView.findViewById(R.id.Title);
            txtDesc = itemView.findViewById(R.id.Desc);
            viewResumeBtn = itemView.findViewById(R.id.ViewResumeBtn);
            acceptJobApplicationBtn = itemView.findViewById(R.id.AcceptJobApplicationBtn);
        }
    }
}
