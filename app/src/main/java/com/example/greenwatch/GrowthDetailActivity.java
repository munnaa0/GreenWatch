package com.example.greenwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class GrowthDetailActivity extends AppCompatActivity {
    
    private static final String TAG = "GrowthDetailActivity";
    
    private ImageView photoImageView;
    private TextView dayDateText;
    private TextView statusText;
    private TextView suggestionsText;
    private MaterialButton btnReturnHome;
    
    private int dayNumber;
    private String date;
    private String status;
    private List<String> suggestions;
    private String photoUri;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_detail);
        
        initializeViews();
        getDataFromIntent();
        displayGrowthDetails();
        setupButtonListeners();
    }
    private void initializeViews() {
        photoImageView = findViewById(R.id.photoImageView);
        dayDateText = findViewById(R.id.dayDateText);
        statusText = findViewById(R.id.statusText);
        suggestionsText = findViewById(R.id.suggestionsText);
        btnReturnHome = findViewById(R.id.btnReturnHome);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        dayNumber = intent.getIntExtra("day_number", 1);
        date = intent.getStringExtra("date");
        status = intent.getStringExtra("status");
        photoUri = intent.getStringExtra("photo_uri");
        documentId = intent.getStringExtra("document_id");
        
        // Get suggestions list
        ArrayList<String> suggestionsList = intent.getStringArrayListExtra("suggestions");
        suggestions = suggestionsList != null ? suggestionsList : new ArrayList<>();
        
        Log.d(TAG, "Received data - Day: " + dayNumber + ", Status: " + status);
    }

    private void setupButtonListeners() {
        btnReturnHome.setOnClickListener(v -> {
           Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayGrowthDetails() {
        // Load and display the photo
        loadPhoto();
        
        // Set day and date
        dayDateText.setText("Day " + dayNumber + " - " + date);
        
        // Set status with appropriate color
        statusText.setText(status);
        setStatusColor(status);
        
        // Format and display all suggestions
        displaySuggestions();
    }

    private void loadPhoto() {
        if (photoUri != null && !photoUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(photoUri);
                photoImageView.setImageURI(uri);
                // Check if image actually loaded, if not show placeholder
                if (photoImageView.getDrawable() == null) {
                    photoImageView.setImageResource(R.drawable.default_plant_image);
                }
                photoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (Exception e) {
                photoImageView.setImageResource(R.drawable.default_plant_image);
                photoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        } else {
            photoImageView.setImageResource(R.drawable.default_plant_image);
            photoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void setStatusColor(String status) {
        int color;
        switch (status.toLowerCase()) {
            case "healthy":
            case "good growth":
                color = 0xFF4CAF50; // Green
                break;
            case "water stress":
            case "nutrient deficiency":
            case "needs attention":
                color = 0xFFFF9800; // Orange
                break;
            case "pest stress":
            case "disease detected":
                color = 0xFFF44336; // Red
                break;
            default:
                color = 0xFF2196F3; // Blue
                break;
        }
        statusText.setTextColor(color);
    }

    private void displaySuggestions() {
        if (suggestions != null && !suggestions.isEmpty()) {
            StringBuilder suggestionsBuilder = new StringBuilder();
            
            for (int i = 0; i < suggestions.size(); i++) {
                suggestionsBuilder.append("• ").append(suggestions.get(i));
                if (i < suggestions.size() - 1) {
                    suggestionsBuilder.append("\n\n");
                }
            }
            
            suggestionsText.setText(suggestionsBuilder.toString());
        } else {
            suggestionsText.setText("No specific care suggestions available.");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, GrowthHistoryActivity.class);
        startActivity(intent);
        finish();
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