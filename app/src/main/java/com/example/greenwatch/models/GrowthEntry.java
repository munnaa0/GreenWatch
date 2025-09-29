package com.example.greenwatch.models;

import java.util.List;

public class GrowthEntry {
    
    private int dayNumber;
    private String date;
    private String status;
    private List<String> suggestions;
    private String photoUri;
    private String documentId; // Not stored in Firestore

    public GrowthEntry() {
        // Required for Firestore
    }

    public GrowthEntry(int dayNumber, String date, String status, List<String> suggestions, String photoUri) {
        this.dayNumber = dayNumber;
        this.date = date;
        this.status = status;
        this.suggestions = suggestions;
        this.photoUri = photoUri;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getFirstSuggestion() {
        if (suggestions != null && !suggestions.isEmpty()) {
            return suggestions.get(0);
        }
        return "";
    }

    public String getDisplayTitle() {
        return "Day " + dayNumber + " - " + date;
    }

    @Override
    public String toString() {
        return "GrowthEntry{" +
                "dayNumber=" + dayNumber +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", suggestions=" + suggestions +
                ", photoUri='" + photoUri + '\'' +
                '}';
    }
}