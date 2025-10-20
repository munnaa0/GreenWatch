package com.example.greenwatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenwatch.R;
import com.example.greenwatch.models.GrowthEntry;
import java.util.List;

public class GrowthHistoryAdapter extends RecyclerView.Adapter<GrowthHistoryAdapter.GrowthViewHolder> {
    
    private final List<GrowthEntry> growthEntries;
    private final OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(GrowthEntry entry);
    }

    public GrowthHistoryAdapter(List<GrowthEntry> growthEntries, OnItemClickListener clickListener) {
        this.growthEntries = growthEntries;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public GrowthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_growth_history, parent, false);
        return new GrowthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrowthViewHolder holder, int position) {
        holder.bind(growthEntries.get(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return growthEntries.size();
    }

    static class GrowthViewHolder extends RecyclerView.ViewHolder {
        
        private final CardView cardView;
        private final TextView dayDateText, statusText, suggestionText;

        public GrowthViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            dayDateText = itemView.findViewById(R.id.dayDateText);
            statusText = itemView.findViewById(R.id.statusText);
            suggestionText = itemView.findViewById(R.id.suggestionText);
        }

        public void bind(GrowthEntry entry, OnItemClickListener clickListener) {
            dayDateText.setText(entry.getDisplayTitle());
            statusText.setText(entry.getStatus());
            setStatusColor(entry.getStatus());
            
            String firstSuggestion = entry.getFirstSuggestion();
            if (!firstSuggestion.isEmpty()) {
                suggestionText.setText(firstSuggestion);
                suggestionText.setVisibility(View.VISIBLE);
            } else {
                suggestionText.setVisibility(View.GONE);
            }
            
            cardView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onItemClick(entry);
            });
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
    }
}