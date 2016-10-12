package com.brianroper.popularmovies.model;

/**
 * Created by brianroper on 3/31/16.
 */
public class Review {

    private String mAuthor;
    private String mContent;

    public Review() {

    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
