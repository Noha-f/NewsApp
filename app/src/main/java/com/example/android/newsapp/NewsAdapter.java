package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Noha Farid on 3/29/2018.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    // Unwanted letter coming from the query
    private static final String DATE_TIME_SEPARATOR = "T";

    // To be added to indicate the time zone
    private static final String UTC = " UTC";


    public NewsAdapter(@NonNull Context context, @NonNull List<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        NewsViewHolder newsViewHolder;

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            newsViewHolder = new NewsViewHolder();

            // Find the ImageView in the list_item.xml layout for the image and store it in viewholder
            newsViewHolder.imageView = (ImageView) listItemView.findViewById(R.id.image_view);
            // Find the TextView in the list_item.xml layout for the section name and store it in viewholder
            newsViewHolder.sectionView = (TextView) listItemView.findViewById(R.id.section_view);
            // Find the TextView in the list_item.xml layout for the title and store it in viewholder
            newsViewHolder.titleView = (TextView) listItemView.findViewById(R.id.news_title_view);
            // Find the TextView in the list_item.xml layout for the date and store it in viewholder
            newsViewHolder.dateView = (TextView) listItemView.findViewById(R.id.date_view);
            // Find the TextView in the list_item.xml layout for the time and store it in viewholder
            newsViewHolder.timeView = (TextView) listItemView.findViewById(R.id.time_view);

            listItemView.setTag(newsViewHolder);
        } else {
            newsViewHolder = (NewsViewHolder) listItemView.getTag();
        }

        // Get the object located at this position in the list
        News currentDetails = getItem(position);

        // Get the image url at this position
        String imageUrl = currentDetails.getImageUrl();
        // Create a bitmap from the image url
        // Set the image at this position on the Image View
        DownloadImageTask task = new DownloadImageTask(newsViewHolder.imageView);
        task.execute(imageUrl);

        // Set the section name at this position on the section View
        newsViewHolder.sectionView.setText(currentDetails.getSectionName());

        // Set the title at this position on the title View
        newsViewHolder.titleView.setText(currentDetails.getNewsTitle());

        // Get the date/time string at this position
        String dateTimeString = currentDetails.getDate();

        String date;
        String timeZulu;
        String time;

        // Split the date and time then remove the unwanted letter and add the time zone
        String[] parts = dateTimeString.split(DATE_TIME_SEPARATOR);
        date = parts[0];
        timeZulu = parts[1];
        time = timeZulu.substring(1, timeZulu.length() - 1) + UTC;

        // Set the date at this position to the two location views
        newsViewHolder.dateView.setText(date);
        // Set the time at this position on the date View
        newsViewHolder.timeView.setText(time);


        //return the whole list item layout
        return listItemView;
    }


    private static class NewsViewHolder {
        public ImageView imageView;
        public TextView sectionView;
        public TextView titleView;
        public TextView dateView;
        public TextView timeView;

    }

    // Convert image url to Bitmap
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imgUrl;

        public DownloadImageTask(ImageView imgUrl) {
            this.imgUrl = imgUrl;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imgUrl.setImageBitmap(result);
        }

    }
}

