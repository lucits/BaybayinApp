package com.capstonearc.baybayinquizapp.Domain;

public class LessonsDomain {
    private int id;
    private String title;
    private String subtitle;
    private String picture;

    public LessonsDomain(int id, String title, String subtitle, String picture) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}