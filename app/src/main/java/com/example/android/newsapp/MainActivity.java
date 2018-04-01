package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Constant value for the earthquake loader ID
    private static final int NEWS_LOADER_ID = 1;

    private static final String USGS_REQUEST_URL = "https://content.guardianapis.com/tone/minutebyminute?";

    //"http://content.guardianapis.com/tone/minutebyminute?show-fields=thumbnail&page-size=30&order-by=newest&api-key=dc6d0742-16c9-4608-b3cd-65f745b1632e";

    // Adapter for the list of earthquakes
    private NewsAdapter adapter;

    // TextView that is displayed when the list is empty
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new NewsAdapter(this, new ArrayList<News>());

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        emptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);


        // Set the adapter on the ListView so the list can be populated in the user interface
        newsListView.setAdapter(adapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news object that was clicked on
                News currentNews = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getWebUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_topic_key)) ||
                key.equals(getString(R.string.settings_order_by_key))){
            // Clear the ListView as a new query will be kicked off
            adapter.clear();

            // Hide the empty state text view as the loading indicator will be displayed
            emptyStateTextView.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String topic = sharedPrefs.getString(
                getString(R.string.settings_topic_key),
                getString(R.string.settings_topic_default));

        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String apiKey = "dc6d0742-16c9-4608-b3cd-65f745b1632e";

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("section", topic);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", "50");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("api-key", apiKey);

        Log.v(LOG_TAG, "url is " + uriBuilder.toString());

        // Return the completed uri `http://content.guardianapis.com/tone/minutebyminute?show-fields=thumbnail&page-size=30&order-by=newest&api-key=dc6d0742-16c9-4608-b3cd-65f745b1632e
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        emptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        adapter.clear();

        // If there is a valid list of News, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    }

