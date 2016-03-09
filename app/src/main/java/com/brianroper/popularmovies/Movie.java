package com.brianroper.popularmovies;

/**
 * Created by brianroper on 3/8/16.
 */
public class Movie {

    private String mId;
    private String mPosterUrl;

    public Movie(){

        this.mId = mId;
        this.mPosterUrl = mPosterUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        mPosterUrl = posterUrl;
    }
}
