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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class GrowthHistoryActivity extends AppCompatActivity implements GrowthHistoryAdapter.OnItemClickListener {
    
    private static final String TAG = "GrowthHistoryActivity";
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private GrowthHistoryAdapter adapter;
    private List<GrowthEntry> growthEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_history);
        
        recyclerView = findViewById(R.id.recyclerViewHistory);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);
        
        growthEntries = new ArrayList<>();
        adapter = new GrowthHistoryAdapter(growthEntries, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        loadGrowthHistory();
    }

    private void loadGrowthHistory() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        
        FirebaseFirestore.getInstance().collection("growth_history")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(snapshots -> {
                growthEntries.clear();
                
                for (QueryDocumentSnapshot doc : snapshots) {
                    try {
                        GrowthEntry entry = doc.toObject(GrowthEntry.class);
                        entry.setDocumentId(doc.getId());
                        growthEntries.add(entry);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing document", e);
                    }
                }
                
                growthEntries.sort((e1, e2) -> Integer.compare(e1.getDayNumber(), e2.getDayNumber()));
                progressBar.setVisibility(ProgressBar.GONE);
                updateUI();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading growth history", e);
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(this, "Failed to load history", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(GrowthEntry entry) {
        Intent intent = new Intent(this, GrowthDetailActivity.class);
        intent.putExtra("day_number", entry.getDayNumber());
        intent.putExtra("date", entry.getDate());
        intent.putExtra("status", entry.getStatus());
        intent.putStringArrayListExtra("suggestions", new ArrayList<>(entry.getSuggestions()));
        intent.putExtra("photo_uri", entry.getPhotoUri());
        intent.putExtra("document_id", entry.getDocumentId());
        startActivity(intent);
    }
}