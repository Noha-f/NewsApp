package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Noha Farid on 3/28/2018.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /**
     * Query URL
     */
    private String queryUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        queryUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (queryUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<News> news = QueryUtils.fetchNewsData(queryUrl);
        return news;
    }
}
