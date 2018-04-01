package com.example.android.newsapp;

/**
 * Created by Noha Farid on 3/28/2018.
 */

public class News {
    // Image
    private String imageUrl;

    // Title
    private String sectionName;

    // Title
    private String newsTitle;

    // Date
    private String date;

    // url
    private String webUrl;

    // Constructor
    public News(String thumbnailUrl, String sectionInput, String titleInput, String dateInput, String urlInput) {
        imageUrl = thumbnailUrl;
        sectionName = sectionInput;
        newsTitle = titleInput;
        date = dateInput;
        webUrl = urlInput;
    }

    // Get the image url
    public String getImageUrl() {
        return imageUrl;
    }

    // Get the section name
    public String getSectionName() {
        return sectionName;
    }

    // Get the news title
    public String getNewsTitle() {
        return newsTitle;
    }

    // Get the date
    public String getDate() {
        return date;
    }

    // Get the web url
    public String getWebUrl() {
        return webUrl;
    }
}
