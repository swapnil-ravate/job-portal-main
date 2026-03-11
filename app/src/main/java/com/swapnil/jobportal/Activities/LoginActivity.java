package com.swapnil.jobportal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swapnil.jobportal.MainActivity;
import com.swapnil.jobportal.R;

import java.util.HashMap;
import java.util.Map;

/**
 * LoginActivity — handles user authentication via Firebase.
 * On success, reads the user role and navigates to the appropriate dashboard.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginBtn;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        emailEditText = findViewById(R.id.EmailEditTxt);
        passwordEditText = findViewById(R.id.PasswordEditTxt);
        loginBtn = findViewById(R.id.LoginBtn);
        progressBar = findViewById(R.id.ProgressBar);

        progressBar.setVisibility(View.INVISIBLE);

        loginBtn.setOnClickListener(view -> loginUser());

        // Navigate to registration screen
        Button registerBtn = findViewById(R.id.RegisterBtn);
        if (registerBtn != null) {
            registerBtn.setOnClickListener(view -> {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            });
        }
    }

    /**
     * Validates input and attempts Firebase sign-in.
     */
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate fields
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please enter both email and password.");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        loginBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    updateLoginInfo(userId);
                    checkUserRoleAndNavigate(userId);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    loginBtn.setEnabled(true);
                    showToast("Authentication failed. Please check your credentials.");
                });
    }

    /**
     * Updates the lastLogin timestamp for this user in the database.
     */
    private void updateLoginInfo(String userId) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("lastLogin", System.currentTimeMillis());
        databaseReference.child(userId).updateChildren(updateMap);
    }

    /**
     * Reads the user's role from the database and navigates to the correct dashboard.
     */
    private void checkUserRoleAndNavigate(String userId) {
        databaseReference.child(userId).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        progressBar.setVisibility(View.INVISIBLE);
                        loginBtn.setEnabled(true);

                        String role = snapshot.getValue(String.class);

                        if (role == null || role.isEmpty()) {
                            // No role assigned yet — go to role selection
                            navigateToRoleActivity();
                        } else if (role.equals("admin")) {
                            navigateToAdminDashboard();
                        } else if (role.equals("jobseeker")) {
                            navigateToMainActivity();
                        } else {
                            // Unknown role — fall back to role selection
                            navigateToRoleActivity();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        loginBtn.setEnabled(true);
                        showToast("Error loading user data. Please try again.");
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToAdminDashboard() {
        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToRoleActivity() {
        Intent intent = new Intent(LoginActivity.this, RoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
