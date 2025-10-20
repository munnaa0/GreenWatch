package com.example.greenwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvJoinedDate;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvJoinedDate = findViewById(R.id.tvJoinedDate);

        loadUserData();

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> 
            Toast.makeText(this, "Edit profile feature coming soon!", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser != null) {
            tvUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "No email");

            String cachedName = prefs.getString("cached_user_name", "Loading...");
            String cachedDate = prefs.getString("cached_registration_date", "Loading...");
            tvUserName.setText(cachedName);
            tvJoinedDate.setText(cachedDate);

            FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String fullName = doc.getString("fullName");
                        String registrationDate = doc.getString("registrationDate");
                        
                        tvUserName.setText(fullName != null ? fullName : "User");
                        tvJoinedDate.setText(registrationDate != null ? registrationDate : "Date not available");
                        
                        if (fullName != null) prefs.edit().putString("cached_user_name", fullName).apply();
                        if (registrationDate != null) prefs.edit().putString("cached_registration_date", registrationDate).apply();
                    }
                });
        }
    }
}
