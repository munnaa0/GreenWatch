package com.example.greenwatch.models;

public class User {
    private String userId;
    private String fullName;
    private String email;
    private String registrationDate;

    public User() {}

    public User(String userId, String fullName, String email, String registrationDate) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
}
