package com.example.greenwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GrowthDetailActivity extends AppCompatActivity {
    
    private static final String TAG = "GrowthDetailActivity";
    
    private ImageView photoImageView;
    private TextView dayDateText, statusText, suggestionsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_detail);
        
        photoImageView = findViewById(R.id.photoImageView);
        dayDateText = findViewById(R.id.dayDateText);
        statusText = findViewById(R.id.statusText);
        suggestionsText = findViewById(R.id.suggestionsText);
        
        Intent intent = getIntent();
        int dayNumber = intent.getIntExtra("day_number", 1);
        String date = intent.getStringExtra("date");
        String status = intent.getStringExtra("status");
        String photoUri = intent.getStringExtra("photo_uri");
        ArrayList<String> suggestions = intent.getStringArrayListExtra("suggestions");
        
        displayData(dayNumber, date, status, photoUri, suggestions != null ? suggestions : new ArrayList<>());
        
        findViewById(R.id.btnReturnHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void displayData(int dayNumber, String date, String status, String photoUri, List<String> suggestions) {
        dayDateText.setText("Day " + dayNumber + " - " + date);
        statusText.setText(status);
        setStatusColor(status);
        displaySuggestions(suggestions);
        loadPhoto(photoUri);
    }

    private void loadPhoto(String photoUri) {
        if (photoUri != null && !photoUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(photoUri);
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    if (bitmap != null) {
                        photoImageView.setImageBitmap(bitmap);
                        return;
                    }
                }
                photoImageView.setImageURI(uri);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
                photoImageView.setImageResource(R.drawable.default_plant_image);
            }
        } else {
            photoImageView.setImageResource(R.drawable.default_plant_image);
        }
    }

    private void setStatusColor(String status) {
        int color = 0xFF2196F3;
        if (status != null) {
            switch (status.toLowerCase()) {
                case "healthy":
                case "good growth":
                    color = 0xFF4CAF50;
                    break;
                case "water stress":
                case "nutrient deficiency":
                case "needs attention":
                    color = 0xFFFF9800;
                    break;
                case "pest stress":
                case "disease detected":
                    color = 0xFFF44336;
                    break;
            }
        }
        statusText.setTextColor(color);
    }

    private void displaySuggestions(List<String> suggestions) {
        if (suggestions != null && !suggestions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < suggestions.size(); i++) {
                sb.append("• ").append(suggestions.get(i));
                if (i < suggestions.size() - 1) sb.append("\n\n");
            }
            suggestionsText.setText(sb.toString());
        } else {
            suggestionsText.setText("No specific care suggestions available.");
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, GrowthHistoryActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (photoImageView != null) photoImageView.setImageDrawable(null);
    }
}