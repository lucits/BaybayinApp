package com.capstonearc.baybayinquizapp;

public class Users {

    private String userId;
    private String username;
    private String email;
    private int battleModeScore;
    private String profilePicUrl; // This field is optional

    // No-argument constructor
    public Users() {
        // Required for calls to DataSnapshot.getValue(User.class)
    }

    // Constructor with parameters
    public Users(String userId, String username, String email, int battleModeScore) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.battleModeScore = battleModeScore;
        this.profilePicUrl = ""; // Initialize if needed
    }

    // Getter and setter methods
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBattleModeScore() {
        return battleModeScore;
    }

    public void setBattleModeScore(int battleModeScore) {
        this.battleModeScore = battleModeScore;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
