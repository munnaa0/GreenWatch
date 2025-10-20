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
        
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            String[] requiredPermissions = getRequiredPermissions();
            Log.d(TAG, "Requesting permissions: " + Arrays.toString(requiredPermissions));
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSIONS);
        }
        
        setupShutterButton();
    }

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
        
        tipsTextView.setText("Initializing camera...");
        shutterButton.setEnabled(false);
    }

    private void setupShutterButton() {
        shutterButton.setOnClickListener(v -> capturePhoto());
    }

    private String[] getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();
        
        permissions.add(Manifest.permission.CAMERA);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        
        return permissions.toArray(new String[0]);
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                Log.d(TAG, "Permission " + permissions[i] + " result: " + 
                    (grantResults[i] == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
            }
            
            if (allPermissionsGranted()) {
                Log.d(TAG, "All permissions granted, starting camera");
                startCamera();
            } else {
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

    private void startCamera() {
        Log.d(TAG, "Starting camera initialization...");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                Log.d(TAG, "Camera provider obtained successfully");
                
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();
                
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
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                }
                
                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                    Log.d(TAG, "Camera initialized successfully");
                    
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

    private void capturePhoto() {
        if (!allPermissionsGranted()) {
            showError("Permissions not granted. Please restart the app and grant all permissions.");
            return;
        }
        
        if (imageCapture == null) {
            showError("Camera not ready. Please try again.");
            return;
        }
        
        Log.d(TAG, "Starting photo capture...");
        
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "Plant_" + timeStamp + ".jpg";
        
        ImageCapture.OutputFileOptions outputFileOptions;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
            File photoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AI_Crop_Monitor");
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }
            File photoFile = new File(photoDir, filename);
            
            outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        }
        
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