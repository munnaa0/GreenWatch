package com.example.greenwatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenwatch.adapters.GrowthHistoryAdapter;
import com.example.greenwatch.models.GrowthEntry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GrowthHistoryActivity extends AppCompatActivity implements GrowthHistoryAdapter.OnItemClickListener {
    
    private static final String TAG = "GrowthHistoryActivity";
    private static final String FIRESTORE_COLLECTION = "growth_history";
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    
    private GrowthHistoryAdapter adapter;
    private List<GrowthEntry> growthEntries;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_history);
        
        db = FirebaseFirestore.getInstance();
        
        initializeViews();
        setupRecyclerView();
        loadGrowthHistory();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewHistory);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    private void setupRecyclerView() {
        growthEntries = new ArrayList<>();
        adapter = new GrowthHistoryAdapter(growthEntries, this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void loadGrowthHistory() {
        showLoading(true);
        
        db.collection(FIRESTORE_COLLECTION)
            .orderBy("dayNumber", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                growthEntries.clear();
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    GrowthEntry entry = document.toObject(GrowthEntry.class);
                    entry.setDocumentId(document.getId());
                    growthEntries.add(entry);
                }
                
                Log.d(TAG, "Loaded " + growthEntries.size() + " growth entries");
                
                showLoading(false);
                updateUI();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading growth history", e);
                showLoading(false);
                showError("Failed to load growth history: " + e.getMessage());
            });
    }

    private void updateUI() {
        if (growthEntries.isEmpty()) {
            recyclerView.setVisibility(RecyclerView.GONE);
            emptyStateText.setVisibility(TextView.VISIBLE);
            emptyStateText.setText("No growth history available.\nStart by capturing your first plant photo!");
        } else {
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            emptyStateText.setVisibility(TextView.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.GONE);
        recyclerView.setVisibility(show ? RecyclerView.GONE : RecyclerView.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(GrowthEntry entry) {
        Log.d(TAG, "Growth entry clicked: Day " + entry.getDayNumber());
        
        Intent intent = new Intent(this, GrowthDetailActivity.class);
        intent.putExtra("day_number", entry.getDayNumber());
        intent.putExtra("date", entry.getDate());
        intent.putExtra("status", entry.getStatus());
        intent.putStringArrayListExtra("suggestions", new ArrayList<>(entry.getSuggestions()));
        intent.putExtra("photo_uri", entry.getPhotoUri());
        intent.putExtra("document_id", entry.getDocumentId());
        
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // loadGrowthHistory(); // Optionally refresh data
    }
}