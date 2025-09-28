package com.example.greenwatch;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


//CameraActivity handles photo capture using CameraX API.
//Provides live camera preview with capture functionality and saves images to external storage.

public class CameraActivity extends AppCompatActivity {
    
    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    
    private PreviewView previewView;
    private ImageButton shutterButton;
    private TextView tipsTextView;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private FrameLayout previewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        initializeViews();
        
        // Check permissions and start camera
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            String[] requiredPermissions = getRequiredPermissions();
            Log.d(TAG, "Requesting permissions: " + Arrays.toString(requiredPermissions));
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSIONS);
        }
        
        setupShutterButton();
    }


//     * Initialize UI components

    private void initializeViews() {
        previewContainer = findViewById(R.id.previewContainer);
        shutterButton = findViewById(R.id.shutterButton);
        tipsTextView = findViewById(R.id.tipsTextView);

        previewView = new PreviewView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        previewView.setLayoutParams(layoutParams);
        previewContainer.addView(previewView);
        
        // Set helpful tips for users and disable shutter until camera is ready
        tipsTextView.setText("Initializing camera...");
        shutterButton.setEnabled(false);
    }


//     * Setup shutter button click listener

    private void setupShutterButton() {
        shutterButton.setOnClickListener(v -> capturePhoto());
    }


//     * Get required permissions based on Android version

    private String[] getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();
        
        // Camera permission is always required
        permissions.add(Manifest.permission.CAMERA);
        
        // Storage permissions depend on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES for reading images
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Android 9 and below need WRITE_EXTERNAL_STORAGE
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // Android 10-12 doesn't need storage permissions for MediaStore operations
        
        return permissions.toArray(new String[0]);
    }


//     * Check if all required permissions are granted

    private boolean allPermissionsGranted() {
        String[] requiredPermissions = getRequiredPermissions();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted: " + permission);
                return false;
            }
        }
        return true;
    }


//     * Handle permission request results

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // Log the results for debugging
            for (int i = 0; i < permissions.length; i++) {
                Log.d(TAG, "Permission " + permissions[i] + " result: " + 
                    (grantResults[i] == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
            }
            
            if (allPermissionsGranted()) {
                Log.d(TAG, "All permissions granted, starting camera");
                startCamera();
            } else {
                // Show specific message about which permissions are missing
                String[] requiredPermissions = getRequiredPermissions();
                StringBuilder missingPermissions = new StringBuilder();
                for (String permission : requiredPermissions) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        if (missingPermissions.length() > 0) {
                            missingPermissions.append(", ");
                        }
                        missingPermissions.append(getPermissionName(permission));
                    }
                }
                
                String message = "Missing permissions: " + missingPermissions.toString() + 
                    ". Please grant these permissions in Settings to use the camera.";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                Log.e(TAG, message);
                finish();
            }
        }
    }

    private String getPermissionName(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                return "Camera";
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "Storage";
            case Manifest.permission.READ_MEDIA_IMAGES:
                return "Media Access";
            default:
                return permission.substring(permission.lastIndexOf('.') + 1);
        }
    }


      // Initialize and start the camera with CameraX

    private void startCamera() {
        Log.d(TAG, "Starting camera initialization...");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                cameraProvider = cameraProviderFuture.get();
                Log.d(TAG, "Camera provider obtained successfully");
                
                // Set up the view finder use case to display camera preview
                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                
                // Set up the capture use case to allow users to take photos
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();
                
                // Select back camera as a default, fall back to front if not available
                CameraSelector cameraSelector;
                try {
                    if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                        Log.d(TAG, "Using back camera");
                    } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                        Log.d(TAG, "Back camera not available, using front camera");
                    } else {
                        Log.e(TAG, "No cameras available");
                        showError("No cameras available on this device");
                        return;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error checking camera availability", e);
                    // Default to back camera if we can't check availability
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                }
                
                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll();
                    
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                    Log.d(TAG, "Camera initialized successfully");
                    
                    // Update UI to indicate camera is ready
                    runOnUiThread(() -> {
                        tipsTextView.setText("Camera ready! Ensure good lighting and keep the plant centered in frame");
                        shutterButton.setEnabled(true);
                    });
                    
                } catch (Exception exc) {
                    Log.e(TAG, "Use case binding failed", exc);
                    showError("Failed to start camera: " + exc.getMessage());
                }
                
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
                showError("Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }


     // Capture photo and save to external storage

    private void capturePhoto() {
        // Check if we still have permissions
        if (!allPermissionsGranted()) {
            showError("Permissions not granted. Please restart the app and grant all permissions.");
            return;
        }
        
        // Get a stable reference of the modifiable image capture use case
        if (imageCapture == null) {
            showError("Camera not ready. Please try again.");
            return;
        }
        
        Log.d(TAG, "Starting photo capture...");
        
        // Create time stamped name for the image file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "Plant_" + timeStamp + ".jpg";
        
        // Create output file options
        ImageCapture.OutputFileOptions outputFileOptions;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore for Android 10+
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AI_Crop_Monitor");
            
            outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                    getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
            ).build();
        } else {
            // Use direct file access for older Android versions
            File photoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AI_Crop_Monitor");
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }
            File photoFile = new File(photoDir, filename);
            
            outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        }
        
        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                        String errorMsg;
                        switch (exception.getImageCaptureError()) {
                            case ImageCapture.ERROR_FILE_IO:
                                errorMsg = "Failed to save photo. Check storage permissions.";
                                break;
                            case ImageCapture.ERROR_CAPTURE_FAILED:
                                errorMsg = "Photo capture failed. Please try again.";
                                break;
                            case ImageCapture.ERROR_CAMERA_CLOSED:
                                errorMsg = "Camera was closed. Please restart the app.";
                                break;
                            default:
                                errorMsg = "Failed to capture photo. Please try again.";
                                break;
                        }
                        showError(errorMsg);
                    }
                    
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Uri savedUri = output.getSavedUri();
                        if (savedUri != null) {
                            Log.d(TAG, "Photo saved successfully: " + savedUri);
                            Toast.makeText(CameraActivity.this, "Photo captured successfully!", Toast.LENGTH_SHORT).show();
                            // Launch PreviewActivity with the saved image URI
                            launchPreviewActivity(savedUri);
                        } else {
                            Log.e(TAG, "Photo saved but URI is null");
                            showError("Photo captured but failed to get file location");
                        }
                    }
                }
        );
    }


    private void launchPreviewActivity(Uri imageUri) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("image_uri", imageUri.toString());
        startActivity(intent);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}
