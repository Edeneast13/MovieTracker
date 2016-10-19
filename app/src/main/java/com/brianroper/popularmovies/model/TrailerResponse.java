package com.brianroper.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by brianroper on 10/18/16.
 */
public class TrailerResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("results")
    private List<Trailer> results;

    public TrailerResponse(String id, List<Trailer> results) {
        this.id = id;
        this.results = results;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
