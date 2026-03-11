package com.swapnil.jobportal.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.swapnil.jobportal.Model.ApplicationModel;
import com.swapnil.jobportal.R;

/**
 * UserPlacedApplicationAdapter — displays only the "selected" applications for a job seeker.
 * Reuses the same item layout as UserAllApplicationsAdapter.
 */
public class UserPlacedApplicationAdapter
        extends FirebaseRecyclerAdapter<ApplicationModel, UserPlacedApplicationAdapter.ViewHolder> {

    private final TextView emptyStateTv;

    public UserPlacedApplicationAdapter(@NonNull FirebaseRecyclerOptions<ApplicationModel> options,
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
        holder.jobTitleTv.setText(model.getJobTitle());
        holder.companyNameTv.setText(model.getCompanyName());
        holder.statusTv.setText(model.getStatus());
        // All displayed items are selected; show green badge
        holder.statusTv.setTextColor(Color.parseColor("#4CAF50"));
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (emptyStateTv != null) {
            emptyStateTv.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleTv;
        TextView companyNameTv;
        TextView statusTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleTv = itemView.findViewById(R.id.JobTitleTv);
            companyNameTv = itemView.findViewById(R.id.CompanyNameTv);
            statusTv = itemView.findViewById(R.id.StatusTv);
        }
    }
}
