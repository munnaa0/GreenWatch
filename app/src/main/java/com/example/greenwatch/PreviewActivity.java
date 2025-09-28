package com.example.greenwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



/**
 * PreviewActivity displays the captured plant photo and provides options to recapture or submit.
 * Shows the saved image in full screen with action buttons at the bottom.
 */
public class PreviewActivity extends AppCompatActivity {
    
    private static final String TAG = "PreviewActivity";
    
    private ImageView photoImageView;
    private Button recaptureButton;
    private Button submitButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        
        initializeViews();
        loadImageFromIntent();
        setupButtons();
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        photoImageView = findViewById(R.id.photoImageView);
        recaptureButton = findViewById(R.id.recaptureButton);
        submitButton = findViewById(R.id.submitButton);
    }

    /**
     * Load the captured image from the intent extras
     */
    private void loadImageFromIntent() {
        String imageUriString = getIntent().getStringExtra("image_uri");
        
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            loadImageIntoView();
        } else {
            Log.e(TAG, "No image URI provided");
            showError("No image to display");
            finish();
        }
    }

    /**
     * Load the image into the ImageView using standard Android image loading
     */
    private void loadImageIntoView() {
        try {
            // Set the image URI directly to the ImageView
            photoImageView.setImageURI(imageUri);
            
            // Set scale type for proper image fitting
            photoImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoImageView.setAdjustViewBounds(true);
                    
            Log.d(TAG, "Image loaded successfully: " + imageUri);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage(), e);
            showError("Failed to load image");
        }
    }

    /**
     * Setup button click listeners
     */
    private void setupButtons() {
        // Recapture button - return to CameraActivity
        recaptureButton.setOnClickListener(v -> {
            Log.d(TAG, "Recapture button clicked");
            returnToCameraActivity();
        });
        
        // Submit button - confirm the saved image
        submitButton.setOnClickListener(v -> {
            Log.d(TAG, "Submit button clicked");
            confirmPhotoSubmission();
        });
    }

    /**
     * Return to CameraActivity for retaking the photo
     */
    private void returnToCameraActivity() {
        // Clear current activity and return to camera
        Intent intent = new Intent(this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Confirm photo submission and show success message
     */
    private void confirmPhotoSubmission() {
        // Show success message to user
        Toast.makeText(this, "Photo saved successfully in Pictures/AI_Crop_Monitor", Toast.LENGTH_LONG).show();
        
        // Close this activity and return to the calling activity (MainActivity)
        finish();
    }

    /**
     * Show error message to user
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear the image to free memory
        if (photoImageView != null) {
            photoImageView.setImageDrawable(null);
        }
    }
}