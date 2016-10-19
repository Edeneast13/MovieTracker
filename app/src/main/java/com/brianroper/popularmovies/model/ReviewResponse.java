package com.brianroper.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by brianroper on 10/19/16.
 */
public class ReviewResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("results")
    private List<Review> results;

    public ReviewResponse(String id, List<Review> results) {
        this.id = id;
        this.results = results;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
