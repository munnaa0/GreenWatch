package com.example.greenwatch.models;

import java.util.List;

public class GrowthEntry {
    
    private String userId;
    private int dayNumber;
    private String date;
    private String status;
    private List<String> suggestions;
    private String photoUri;
    private String documentId;

    public GrowthEntry() {}

    public GrowthEntry(String userId, int dayNumber, String date, String status, List<String> suggestions, String photoUri) {
        this.userId = userId;
        this.dayNumber = dayNumber;
        this.date = date;
        this.status = status;
        this.suggestions = suggestions;
        this.photoUri = photoUri;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getDayNumber() { return dayNumber; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getFirstSuggestion() {
        return (suggestions != null && !suggestions.isEmpty()) ? suggestions.get(0) : "";
    }

    public String getDisplayTitle() {
        return "Day " + dayNumber + " - " + date;
    }
}