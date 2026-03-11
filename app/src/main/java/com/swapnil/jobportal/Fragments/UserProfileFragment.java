package com.swapnil.jobportal.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.swapnil.jobportal.Activities.StartingActivity;
import com.swapnil.jobportal.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * UserProfileFragment — displays the current user's profile info and provides sign-out.
 */
public class UserProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        CircleImageView profileImageView = view.findViewById(R.id.ProfileImageView);
        TextView displayNameTv = view.findViewById(R.id.DisplayNameTv);
        TextView emailTv = view.findViewById(R.id.EmailTv);
        Button signOutBtn = view.findViewById(R.id.SignOutBtn);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Display name: use displayName if available, fallback to email
            String name = currentUser.getDisplayName();
            if (name == null || name.isEmpty()) {
                name = currentUser.getEmail();
            }
            displayNameTv.setText(name);
            emailTv.setText(currentUser.getEmail());

            // Load profile photo with Picasso if available
            if (currentUser.getPhotoUrl() != null) {
                Picasso.get()
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.ic_default_profile)
                        .error(R.drawable.ic_default_profile)
                        .into(profileImageView);
            }
        }

        signOutBtn.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), StartingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) getActivity().finish();
        });

        return view;
    }
}
