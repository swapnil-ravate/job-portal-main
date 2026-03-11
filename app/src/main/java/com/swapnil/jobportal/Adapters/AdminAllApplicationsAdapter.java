package com.swapnil.jobportal.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swapnil.jobportal.Model.ApplicationModel;
import com.swapnil.jobportal.R;

/**
 * AdminAllApplicationsAdapter — displays all received applications for an admin.
 * Allows the admin to Accept (status = "selected") or Reject (status = "rejected").
 * Status update is saved to BOTH admin and user paths in the database.
 */
public class AdminAllApplicationsAdapter
        extends FirebaseRecyclerAdapter<ApplicationModel, AdminAllApplicationsAdapter.ViewHolder> {

    private final TextView emptyStateTv;

    public AdminAllApplicationsAdapter(@NonNull FirebaseRecyclerOptions<ApplicationModel> options,
                                       TextView emptyStateTv) {
        super(options);
        this.emptyStateTv = emptyStateTv;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_job_application_accept_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position,
                                    @NonNull ApplicationModel model) {
        holder.applicantNameTv.setText(model.getUserName());
        holder.applicantEmailTv.setText(model.getUserEmail());
        holder.jobTitleTv.setText(model.getJobTitle());
        holder.companyNameTv.setText(model.getCompanyName());
        holder.statusTv.setText(model.getStatus());
        applyStatusColor(holder.statusTv, model.getStatus());

        // Open resume in browser (Firebase Storage URL)
        holder.viewResumeBtn.setOnClickListener(view -> {
            String resumeUrl = model.getResumeUrl();
            if (resumeUrl != null && !resumeUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resumeUrl));
                view.getContext().startActivity(browserIntent);
            } else {
                Toast.makeText(view.getContext(), "No resume available.", Toast.LENGTH_SHORT).show();
            }
        });

        // Accept application → update status to "selected" in both paths
        holder.acceptBtn.setOnClickListener(view ->
                updateApplicationStatus(model, "selected", view));

        // Reject application → update status to "rejected" in both paths
        holder.rejectBtn.setOnClickListener(view ->
                updateApplicationStatus(model, "rejected", view));
    }

    /**
     * Updates the application status in BOTH:
     *   jobApplications/{adminId}/{applicationId}/status
     *   jobApplications/{userId}/{applicationId}/status
     */
    private void updateApplicationStatus(ApplicationModel model, String newStatus, View view) {
        String applicationId = model.getApplicationId();
        String adminId = model.getAdminId();
        String userId = model.getUserId();

        if (applicationId == null || adminId == null || userId == null) {
            Toast.makeText(view.getContext(), "Invalid application data.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference appRef = FirebaseDatabase.getInstance().getReference("jobApplications");

        // Update in admin's path
        appRef.child(adminId).child(applicationId).child("status").setValue(newStatus)
                .addOnSuccessListener(unused1 -> {
                    // Also update in user's path
                    appRef.child(userId).child(applicationId).child("status").setValue(newStatus)
                            .addOnSuccessListener(unused2 -> {
                                String msg = "selected".equals(newStatus)
                                        ? "Application accepted!" : "Application rejected.";
                                Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(view.getContext(),
                                    "Failed to update user record: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(view.getContext(),
                        "Failed to update status: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void applyStatusColor(TextView statusTv, String status) {
        if (status == null) return;
        switch (status.toLowerCase()) {
            case "selected":
                statusTv.setTextColor(Color.parseColor("#4CAF50")); // green
                break;
            case "rejected":
                statusTv.setTextColor(Color.parseColor("#F44336")); // red
                break;
            case "pending":
            default:
                statusTv.setTextColor(Color.parseColor("#FF9800")); // orange
                break;
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (emptyStateTv != null) {
            emptyStateTv.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView applicantNameTv;
        TextView applicantEmailTv;
        TextView jobTitleTv;
        TextView companyNameTv;
        TextView statusTv;
        Button viewResumeBtn;
        Button acceptBtn;
        Button rejectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            applicantNameTv = itemView.findViewById(R.id.ApplicantNameTv);
            applicantEmailTv = itemView.findViewById(R.id.ApplicantEmailTv);
            jobTitleTv = itemView.findViewById(R.id.JobTitleTv);
            companyNameTv = itemView.findViewById(R.id.CompanyNameTv);
            statusTv = itemView.findViewById(R.id.StatusTv);
            viewResumeBtn = itemView.findViewById(R.id.ViewResumeBtn);
            acceptBtn = itemView.findViewById(R.id.AcceptBtn);
            rejectBtn = itemView.findViewById(R.id.RejectBtn);
        }
    }
}
