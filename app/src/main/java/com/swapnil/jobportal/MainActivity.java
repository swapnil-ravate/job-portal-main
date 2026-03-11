package com.swapnil.jobportal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swapnil.jobportal.Activities.AdminActivity;
import com.swapnil.jobportal.Activities.StartingActivity;
import com.swapnil.jobportal.Fragments.DisplayJobFragment;
import com.swapnil.jobportal.Fragments.UserDashboardFragment;
import com.swapnil.jobportal.Fragments.UserProfileFragment;

/**
 * MainActivity — Job Seeker dashboard with Bottom Navigation:
 *   - Home: DisplayJobFragment
 *   - Dashboard: UserDashboardFragment
 *   - Profile: UserProfileFragment
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        BottomNavigationView bottomNavigation = findViewById(R.id.BottomNavigationView);

        // Load default fragment
        loadFragment(new DisplayJobFragment());

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new DisplayJobFragment());
                return true;
            } else if (itemId == R.id.nav_dashboard) {
                loadFragment(new UserDashboardFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new UserProfileFragment());
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // Not logged in — go back to start
            navigateToStarting();
            return;
        }

        // Verify the user's role matches this dashboard
        databaseReference.child(currentUser.getUid()).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String role = snapshot.getValue(String.class);
                        if ("admin".equals(role)) {
                            // Admin tried to open jobseeker dashboard — redirect
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        // "jobseeker" or empty string: stay here
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // If we can't read role, stay on this screen
                    }
                });
    }

    /**
     * Replaces the fragment container with the given fragment.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FragmentContainer, fragment)
                .commit();
    }

    private void navigateToStarting() {
        Intent intent = new Intent(MainActivity.this, StartingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
