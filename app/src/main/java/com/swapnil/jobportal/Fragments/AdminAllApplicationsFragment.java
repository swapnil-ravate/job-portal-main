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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.swapnil.jobportal.Adapters.AdminAllApplicationsAdapter;
import com.swapnil.jobportal.Model.ApplicationModel;
import com.swapnil.jobportal.R;

/**
 * AdminAllApplicationsFragment — shows all applications for the admin.
 * Same as AdminDashboardFragment but kept as a separate class per spec.
 */
public class AdminAllApplicationsFragment extends Fragment {

    private RecyclerView applicationsRecyclerView;
    private TextView emptyStateTv;
    private AdminAllApplicationsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_all_applications, container, false);

        applicationsRecyclerView = view.findViewById(R.id.ApplicationsRecyclerView);
        emptyStateTv = view.findViewById(R.id.EmptyStateTv);

        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return view;

        FirebaseRecyclerOptions<ApplicationModel> options =
                new FirebaseRecyclerOptions.Builder<ApplicationModel>()
                        .setQuery(FirebaseDatabase.getInstance()
                                .getReference("jobApplications").child(currentUser.getUid()),
                                ApplicationModel.class)
                        .build();

        adapter = new AdminAllApplicationsAdapter(options, emptyStateTv);
        applicationsRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}
