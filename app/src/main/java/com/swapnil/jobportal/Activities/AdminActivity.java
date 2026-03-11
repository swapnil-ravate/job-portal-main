package com.swapnil.jobportal.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.swapnil.jobportal.Fragments.AddJobFragment;
import com.swapnil.jobportal.Fragments.AdminDashboardFragment;
import com.swapnil.jobportal.Fragments.AdminProfileFragment;
import com.swapnil.jobportal.R;

/**
 * AdminActivity — Admin dashboard with Bottom Navigation:
 *   - Home: AddJobFragment
 *   - Dashboard: AdminDashboardFragment
 *   - Profile: AdminProfileFragment
 */
public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigation = findViewById(R.id.BottomNavigationView);

        // Load default fragment
        loadFragment(new AddJobFragment());

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new AddJobFragment());
                return true;
            } else if (itemId == R.id.nav_dashboard) {
                loadFragment(new AdminDashboardFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new AdminProfileFragment());
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
            Intent intent = new Intent(AdminActivity.this, StartingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
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
}
