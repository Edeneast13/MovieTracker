package com.brianroper.popularmovies.database;

import android.graphics.Bitmap;

/**
 * Created by brianroper on 3/31/16.
 */
public class Favorites {

    private String mTitle;
    private String mReleaseDate;
    private String mRating;
    private String mOverview;
    private String mReview;
    private Bitmap mPoster;

    public Favorites() {

    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        mRating = rating;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getReview() {
        return mReview;
    }

    public void setReview(String review) {
        mReview = review;
    }

    public Bitmap getPoster() {
        return mPoster;
    }

    public void setPoster(Bitmap poster) {
        mPoster = poster;
    }
}
