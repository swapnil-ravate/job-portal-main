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
import com.swapnil.jobportal.Model.ApplicationModel;
import com.swapnil.jobportal.R;

/**
 * AdminSelectedApplicationAdapter — displays only accepted (status="selected") applications.
 * Allows viewing the resume URL in a browser.
 */
public class AdminSelectedApplicationAdapter
        extends FirebaseRecyclerAdapter<ApplicationModel, AdminSelectedApplicationAdapter.ViewHolder> {

    private final TextView emptyStateTv;

    public AdminSelectedApplicationAdapter(@NonNull FirebaseRecyclerOptions<ApplicationModel> options,
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
        holder.statusTv.setTextColor(Color.parseColor("#4CAF50")); // always green for selected

        // Open resume in browser
        holder.viewResumeBtn.setOnClickListener(view -> {
            String resumeUrl = model.getResumeUrl();
            if (resumeUrl != null && !resumeUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resumeUrl));
                view.getContext().startActivity(browserIntent);
            } else {
                Toast.makeText(view.getContext(), "No resume available.", Toast.LENGTH_SHORT).show();
            }
        });

        // Hide accept/reject buttons for selected-only view
        if (holder.acceptBtn != null) holder.acceptBtn.setVisibility(View.GONE);
        if (holder.rejectBtn != null) holder.rejectBtn.setVisibility(View.GONE);
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
