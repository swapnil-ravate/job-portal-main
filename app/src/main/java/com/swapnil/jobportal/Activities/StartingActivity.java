package com.swapnil.jobportal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swapnil.jobportal.MainActivity;
import com.swapnil.jobportal.R;

/**
 * StartingActivity — Splash/Welcome screen.
 * Checks if the user is already logged in and redirects accordingly.
 */
public class StartingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        firebaseAuth = FirebaseAuth.getInstance();

        // Use Handler with 0 delay to check auth state without blocking UI thread
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // User is already logged in — check role and redirect
                checkRoleAndRedirect(currentUser.getUid());
            }
            // If not logged in, stay on this screen (buttons handle navigation)
        }, 0);

        // Login button — navigate to LoginActivity
        Button loginBtn = findViewById(R.id.LoginBtn);
        loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(StartingActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Sign Up button — navigate to RegistrationActivity
        Button signUpBtn = findViewById(R.id.SignUpBtn);
        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(StartingActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Reads the user's role from Firebase and navigates to the correct dashboard.
     */
    private void checkRoleAndRedirect(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users").child(userId).child("role");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                if (role == null || role.isEmpty()) {
                    // No role assigned yet
                    Intent intent = new Intent(StartingActivity.this, RoleActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (role.equals("admin")) {
                    Intent intent = new Intent(StartingActivity.this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (role.equals("jobseeker")) {
                    Intent intent = new Intent(StartingActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // On error, let the user interact with the start screen normally
            }
        });
    }
}
