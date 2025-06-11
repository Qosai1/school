package com.example.finalproject;

import java.util.Date;

public class EnhancedAdvertisement {
    private String title;
    private String description;
    private int imageResource;
    private String category;
    private Date publishDate;
    private boolean isSaved;
    private String url;
    public EnhancedAdvertisement(String title, String description, int imageResource) {
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
        this.category = "عام";
        this.publishDate = new Date();
        this.isSaved = false;
    }

    public EnhancedAdvertisement(String title, String description, int imageResource,
                                 String category, Date publishDate) {
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
        this.category = category;
        this.publishDate = publishDate;
        this.isSaved = false;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}