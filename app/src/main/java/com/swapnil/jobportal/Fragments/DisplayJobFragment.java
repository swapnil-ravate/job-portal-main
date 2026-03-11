package com.swapnil.jobportal.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.swapnil.jobportal.Adapters.JobsAdapter;
import com.swapnil.jobportal.Model.JobModel;
import com.swapnil.jobportal.R;

/**
 * DisplayJobFragment — shows all available jobs from Firebase using a RecyclerView
 * and FirebaseRecyclerAdapter.
 */
public class DisplayJobFragment extends Fragment {

    private RecyclerView jobsRecyclerView;
    private TextView emptyStateTv;
    private JobsAdapter jobsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_job, container, false);

        jobsRecyclerView = view.findViewById(R.id.JobsRecyclerView);
        emptyStateTv = view.findViewById(R.id.EmptyStateTv);

        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Build Firebase query options for all jobs
        FirebaseRecyclerOptions<JobModel> options = new FirebaseRecyclerOptions.Builder<JobModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("jobs"), JobModel.class)
                .build();

        jobsAdapter = new JobsAdapter(options, emptyStateTv);
        jobsRecyclerView.setAdapter(jobsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (jobsAdapter != null) {
            jobsAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (jobsAdapter != null) {
            jobsAdapter.stopListening();
        }
    }
}
