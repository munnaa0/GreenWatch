package com.example.greenwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenwatch.models.GrowthEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PreviewActivity extends AppCompatActivity {
    
    private static final String[] HEALTH_STATUSES = {"Healthy", "Nutrient Deficiency", "Water Stress", 
        "Pest Stress", "Disease Detected", "Good Growth", "Needs Attention"};
    
    private static final List<List<String>> CARE_SUGGESTIONS = Arrays.asList(
        Arrays.asList("Continue current care routine", "Maintain watering schedule"),
        Arrays.asList("Add fertilizer", "Check soil nutrients", "Consider organic compost"),
        Arrays.asList("Increase watering frequency", "Check soil moisture", "Improve drainage"),
        Arrays.asList("Inspect for pests", "Apply organic pesticide", "Remove affected leaves"),
        Arrays.asList("Isolate plant", "Apply fungicide treatment", "Improve air circulation"),
        Arrays.asList("Ensure adequate sunlight", "Maintain optimal temperature", "Regular monitoring"),
        Arrays.asList("Adjust care routine", "Monitor closely", "Check environmental conditions")
    );
    
    private ImageView photoImageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        
        photoImageView = findViewById(R.id.photoImageView);
        
        String imageUriString = getIntent().getStringExtra("image_uri");
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            photoImageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "No image to display", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        findViewById(R.id.recaptureButton).setOnClickListener(v -> {
            startActivity(new Intent(this, CameraActivity.class));
            finish();
        });
        
        findViewById(R.id.submitButton).setOnClickListener(v -> {
            v.setEnabled(false);
            saveToFirestore();
        });
    }

    private void saveToFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(new Date());
        String healthStatus = HEALTH_STATUSES[new Random().nextInt(HEALTH_STATUSES.length)];
        int statusIndex = Arrays.asList(HEALTH_STATUSES).indexOf(healthStatus);
        List<String> suggestions = CARE_SUGGESTIONS.get(statusIndex);
        
        FirebaseFirestore.getInstance().collection("growth_history")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(snapshots -> {
                int maxDay = 0;
                for (QueryDocumentSnapshot doc : snapshots) {
                    GrowthEntry entry = doc.toObject(GrowthEntry.class);
                    if (entry.getDayNumber() > maxDay) maxDay = entry.getDayNumber();
                }
                int dayNumber = maxDay + 1;
                
                GrowthEntry growthEntry = new GrowthEntry(userId, dayNumber, currentDate, healthStatus, suggestions, imageUri.toString());
                
                FirebaseFirestore.getInstance().collection("growth_history")
                    .add(growthEntry)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Day " + dayNumber + " saved!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, GrowthDetailActivity.class);
                        intent.putExtra("day_number", dayNumber);
                        intent.putExtra("date", currentDate);
                        intent.putExtra("status", healthStatus);
                        intent.putStringArrayListExtra("suggestions", new ArrayList<>(suggestions));
                        intent.putExtra("photo_uri", imageUri.toString());
                        intent.putExtra("document_id", ref.getId());
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.submitButton).setEnabled(true);
                    });
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (photoImageView != null) photoImageView.setImageDrawable(null);
    }
}