package com.example.greenwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenwatch.models.GrowthEntry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;



public class PreviewActivity extends AppCompatActivity {
    
    private static final String TAG = "PreviewActivity";
    private static final String FIRESTORE_COLLECTION = "growth_history";
    
    private FirebaseFirestore db;
    
    private static final String[] HEALTH_STATUSES = {
        "Healthy", "Nutrient Deficiency", "Water Stress", "Pest Stress", 
        "Disease Detected", "Good Growth", "Needs Attention"
    };
    
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
    private Button recaptureButton;
    private Button submitButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        
        db = FirebaseFirestore.getInstance();
        
        initializeViews();
        loadImageFromIntent();
        setupButtons();
    }

    private void initializeViews() {
        photoImageView = findViewById(R.id.photoImageView);
        recaptureButton = findViewById(R.id.recaptureButton);
        submitButton = findViewById(R.id.submitButton);
    }

    private void loadImageFromIntent() {
        String imageUriString = getIntent().getStringExtra("image_uri");
        
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            loadImageIntoView();
        } else {
            showError("No image to display");
            finish();
        }
    }

    private void loadImageIntoView() {
        try {
            photoImageView.setImageURI(imageUri);
            photoImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoImageView.setAdjustViewBounds(true);
        } catch (Exception e) {
            showError("Failed to load image");
        }
    }

    private void setupButtons() {
        recaptureButton.setOnClickListener(v -> {
            returnToCameraActivity();
        });
        
        submitButton.setOnClickListener(v -> {
            v.setEnabled(false); // Prevent multiple clicks
            saveToFirestore();
        });
    }

    private void returnToCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void saveToFirestore() {
        Toast.makeText(this, "Saving growth data...", Toast.LENGTH_SHORT).show();
        
        String currentDate = getCurrentDate();
        
        String healthStatus = generateRandomHealthStatus();
        List<String> suggestions = generateRandomSuggestions(healthStatus);
        
        getNextDayNumber(dayNumber -> {
            GrowthEntry growthEntry = new GrowthEntry(
                dayNumber,
                currentDate,
                healthStatus,
                suggestions,
                imageUri.toString()
            );
            
            final boolean[] saveCompleted = {false};
            
            db.collection(FIRESTORE_COLLECTION)
                .add(growthEntry)
                .addOnSuccessListener(documentReference -> {
                    if (!saveCompleted[0]) {
                        saveCompleted[0] = true;
                        String documentId = documentReference.getId();
                        runOnUiThread(() -> {
                            onSaveSuccess(dayNumber, currentDate, healthStatus, suggestions, imageUri.toString(), documentId);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (!saveCompleted[0]) {
                        saveCompleted[0] = true;
                        runOnUiThread(() -> {
                            String tempDocId = "temp_" + System.currentTimeMillis();
                            onSaveSuccess(dayNumber, currentDate, healthStatus, suggestions, imageUri.toString(), tempDocId);
                        });
                    }
                });
            
            // 3 second timeout
            new android.os.Handler().postDelayed(() -> {
                if (!saveCompleted[0]) {
                    saveCompleted[0] = true;
                    runOnUiThread(() -> {
                        String tempDocId = "temp_" + System.currentTimeMillis();
                        onSaveSuccess(dayNumber, currentDate, healthStatus, suggestions, imageUri.toString(), tempDocId);
                    });
                }
            }, 3000); // 3 second timeout
        });
    }

    private void getNextDayNumber(DayNumberCallback callback) {
        db.collection(FIRESTORE_COLLECTION)
            .orderBy("dayNumber", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                int nextDayNumber = 1; // Default for first entry
                
                if (!queryDocumentSnapshots.isEmpty()) {
                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                    GrowthEntry lastEntry = document.toObject(GrowthEntry.class);
                    nextDayNumber = lastEntry.getDayNumber() + 1;
                }
                
                callback.onDayNumberRetrieved(nextDayNumber);
            })
            .addOnFailureListener(e -> {
                callback.onDayNumberRetrieved(1);
            });
    }

    private String generateRandomHealthStatus() {
        Random random = new Random();
        return HEALTH_STATUSES[random.nextInt(HEALTH_STATUSES.length)];
    }

    private List<String> generateRandomSuggestions(String healthStatus) {
        Random random = new Random();
        
        int statusIndex = 0;
        for (int i = 0; i < HEALTH_STATUSES.length; i++) {
            if (HEALTH_STATUSES[i].equals(healthStatus)) {
                statusIndex = i;
                break;
            }
        }
        
        return CARE_SUGGESTIONS.get(statusIndex);
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void onSaveSuccess(int dayNumber, String date, String status, List<String> suggestions, String photoUri, String documentId) {
        boolean isFirebaseSaved = !documentId.startsWith("temp_");
        String message = "Day " + dayNumber + " data prepared!\nStatus: " + status;
        if (isFirebaseSaved) {
            message += "\n✓ Saved to Firebase";
        } else {
            message += "\n⚠ Firebase save failed (check connection)";
        }
        
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        Intent intent = new Intent(this, GrowthDetailActivity.class);
        intent.putExtra("day_number", dayNumber);
        intent.putExtra("date", date);
        intent.putExtra("status", status);
        intent.putStringArrayListExtra("suggestions", new ArrayList<>(suggestions));
        intent.putExtra("photo_uri", photoUri);
        intent.putExtra("document_id", documentId);
        
        startActivity(intent);
        finish();
    }

    private void onSaveFailure(String errorMessage) {
        Toast.makeText(this, "Failed to save growth data: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private interface DayNumberCallback {
        void onDayNumberRetrieved(int dayNumber);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear image to free memory
        if (photoImageView != null) {
            photoImageView.setImageDrawable(null);
        }
    }
}