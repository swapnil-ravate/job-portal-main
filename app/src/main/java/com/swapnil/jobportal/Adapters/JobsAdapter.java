package com.swapnil.jobportal.Adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.swapnil.jobportal.Activities.JobDetailsActivity;
import com.swapnil.jobportal.Model.JobModel;
import com.swapnil.jobportal.R;

/**
 * JobsAdapter — displays job listings in the DisplayJobFragment RecyclerView.
 * Each item shows company name, job title, salary, last date, and total openings.
 * Clicking a job navigates to JobDetailsActivity with full job details.
 */
public class JobsAdapter extends FirebaseRecyclerAdapter<JobModel, JobsAdapter.ViewHolder> {

    private final TextView emptyStateTv;

    public JobsAdapter(@NonNull FirebaseRecyclerOptions<JobModel> options, TextView emptyStateTv) {
        super(options);
        this.emptyStateTv = emptyStateTv;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_data_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull JobModel model) {
        holder.companyNameTv.setText(model.getCompanyName());
        holder.jobTitleTv.setText(model.getJobTitle());
        holder.jobSalaryTv.setText("Salary: " + model.getJobSalary());
        holder.jobLastDateTv.setText("Apply By: " + model.getJobLastDate());
        holder.totalOpeningsTv.setText("Openings: " + model.getTotalOpenings());

        // On item click → open JobDetailsActivity with all job data
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), JobDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("companyName", model.getCompanyName());
            bundle.putString("jobTitle", model.getJobTitle());
            bundle.putString("jobDescription", model.getAboutJob());
            bundle.putString("jobSalary", model.getJobSalary());
            bundle.putString("startDate", model.getJobStartDate());
            bundle.putString("lastDate", model.getJobLastDate());
            bundle.putString("totalOpenings", model.getTotalOpenings());
            bundle.putString("requiredSkills", model.getSkillsRequired());
            bundle.putString("additionalInfo", model.getAdditionalInfo());
            bundle.putString("userId", model.getAdminId());   // key "userId" holds adminId per spec
            bundle.putString("jobId", model.getJobId());
            intent.putExtras(bundle);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        // Show or hide empty state based on item count
        if (emptyStateTv != null) {
            emptyStateTv.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyNameTv;
        TextView jobTitleTv;
        TextView jobSalaryTv;
        TextView jobLastDateTv;
        TextView totalOpeningsTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyNameTv = itemView.findViewById(R.id.CompanyNameTv);
            jobTitleTv = itemView.findViewById(R.id.JobTitleTv);
            jobSalaryTv = itemView.findViewById(R.id.JobSalaryTv);
            jobLastDateTv = itemView.findViewById(R.id.JobLastDateTv);
            totalOpeningsTv = itemView.findViewById(R.id.TotalOpeningsTv);
        }
    }
}
