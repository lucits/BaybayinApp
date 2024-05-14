package com.capstonearc.baybayinquizapp;

public class Users {

    private String userId;
    private String username;
    private String email;
    private int battleModeScore;
    private String profile_picture;

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Users(String userId, String username, String email, int battleModeScore) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.battleModeScore = battleModeScore;
    }

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
    public String getProfilePicture() {
        return profile_picture;
    }

    public void setBattleModeScore(int battleModeScore) {
        this.battleModeScore = battleModeScore;
    }
}
