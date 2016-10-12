package com.brianroper.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brianroper on 3/8/16.
 */
public class Movie {

    @SerializedName("title")
    private String title;
    @SerializedName("release_date")
    private String releaseData;
    @SerializedName("overview")
    private String overview;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("id")
    private int id;
    @SerializedName("vote_average")
    private Double rating;
    @SerializedName("backdrop_path")
    private String backdropPath;

    public Movie(String title,
                    String releaseDate,
                    Double rating,
                    String overview,
                    String backdropPath,
                    String posterPath,
                    int id){
        this.title = title;
        this.releaseData = releaseDate;
        this.rating = rating;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.posterPath = posterPath;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseData() {
        return releaseData;
    }

    public void setReleaseData(String releaseData) {
        this.releaseData = releaseData;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}
