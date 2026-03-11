package com.swapnil.jobportal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swapnil.jobportal.MainActivity;
import com.swapnil.jobportal.R;

/**
 * RoleActivity — lets new users choose their role (Job Seeker or Admin).
 * Admin role requires a secret code for security.
 */
public class RoleActivity extends AppCompatActivity {

    private static final String ADMIN_SECRET_CODE = "ADMIN123";

    private Button jobSeekerBtn;
    private Button adminBtn;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        jobSeekerBtn = findViewById(R.id.JobSeekerBtn);
        adminBtn = findViewById(R.id.AdminBtn);
        progressBar = findViewById(R.id.ProgressBar);

        progressBar.setVisibility(View.INVISIBLE);

        // Job Seeker — straightforward role assignment
        jobSeekerBtn.setOnClickListener(view -> saveRole("jobseeker"));

        // Admin — requires secret code verification
        adminBtn.setOnClickListener(view -> showAdminCodeDialog());
    }

    /**
     * Shows an AlertDialog asking for the admin secret code before assigning admin role.
     */
    private void showAdminCodeDialog() {
        EditText codeInput = new EditText(this);
        codeInput.setHint("Enter admin code");

        new AlertDialog.Builder(this)
                .setTitle("Admin Verification")
                .setMessage("Please enter the admin access code to continue:")
                .setView(codeInput)
                .setPositiveButton("Verify", (dialog, which) -> {
                    String enteredCode = codeInput.getText().toString().trim();
                    if (enteredCode.equals(ADMIN_SECRET_CODE)) {
                        saveRole("admin");
                    } else {
                        Toast.makeText(this, "Invalid admin code. Access denied.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Saves the selected role to the Firebase database under users/{uid}/role.
     */
    private void saveRole(String role) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        jobSeekerBtn.setEnabled(false);
        adminBtn.setEnabled(false);

        String userId = currentUser.getUid();

        databaseReference.child(userId).child("role").setValue(role)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    jobSeekerBtn.setEnabled(true);
                    adminBtn.setEnabled(true);

                    // Navigate to the correct dashboard
                    if (role.equals("admin")) {
                        Intent intent = new Intent(RoleActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(RoleActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    jobSeekerBtn.setEnabled(true);
                    adminBtn.setEnabled(true);
                    Toast.makeText(this, "Failed to save role. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
