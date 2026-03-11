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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swapnil.jobportal.R;

import java.util.HashMap;
import java.util.Map;

/**
 * RegistrationActivity — handles new user sign-up with Firebase Authentication.
 * On success, creates a user record in the Realtime Database and redirects to LoginActivity.
 */
public class RegistrationActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText fullNameEditText;
    private Button signUpBtn;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Bind UI views
        emailEditText = findViewById(R.id.EmailEditTxt);
        passwordEditText = findViewById(R.id.PasswordEditTxt);
        confirmPasswordEditText = findViewById(R.id.ConfirmPasswordEditTxt);
        fullNameEditText = findViewById(R.id.FullNameEditTxt);
        signUpBtn = findViewById(R.id.SignUpBtn);
        progressBar = findViewById(R.id.ProgressBar);

        progressBar.setVisibility(View.INVISIBLE);

        // Start registration when user taps Sign Up
        signUpBtn.setOnClickListener(view -> registerUser());
    }

    /**
     * Validates all input fields and kicks off Firebase registration.
     */
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();

        // --- Validation ---
        if (fullName.isEmpty()) {
            fullNameEditText.setError("Full name is required");
            fullNameEditText.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Please confirm your password");
            confirmPasswordEditText.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Disable button and show progress
        signUpBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Create user with Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Update the display name on the Auth profile
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build();

                    authResult.getUser().updateProfile(profileUpdate)
                            .addOnSuccessListener(unused -> {
                                // Save user data to the Realtime Database
                                saveUserToDatabase(authResult.getUser().getUid(), email, fullName);
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                signUpBtn.setEnabled(true);
                                showToast("Failed to update profile: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    signUpBtn.setEnabled(true);

                    if (e instanceof FirebaseAuthUserCollisionException) {
                        showToast("An account with this email already exists.");
                    } else {
                        showToast("Registration failed: " + e.getMessage());
                    }
                });
    }

    /**
     * Saves a new user record under users/{uid} in the Realtime Database.
     * Role is set to empty string — user will assign it in RoleActivity.
     */
    private void saveUserToDatabase(String uid, String email, String displayName) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", uid);
        userData.put("email", email);
        userData.put("displayName", displayName);
        userData.put("role", "");          // role assigned later in RoleActivity
        userData.put("profilePic", "");
        userData.put("lastLogin", System.currentTimeMillis());

        databaseReference.child(uid).setValue(userData)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    signUpBtn.setEnabled(true);

                    // Sign out so user must log in explicitly
                    firebaseAuth.signOut();
                    showToast("Registration successful! Please log in.");
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    signUpBtn.setEnabled(true);
                    showToast("Failed to save user data: " + e.getMessage());
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
