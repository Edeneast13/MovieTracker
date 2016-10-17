package com.brianroper.popularmovies.model;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by brianroper on 10/10/16.
 */
public class Favorite extends RealmObject{
    @NonNull
    public String title;
    @NonNull
    public String releaseData;
    @NonNull
    public String rating;
    @NonNull
    public String overview;
    public String review;
    @NonNull
    public byte[] poster;
    @PrimaryKey
    private int id;
    @NonNull
    public String posterPath;

    public Favorite() {}

    public Favorite(String title,
                    String releaseDate,
                    String rating,
                    String overview,
                    String review ,
                    byte[] poster,
                    int id){

        this.title = title;
        this.releaseData = releaseDate;
        this.rating = rating;
        this.overview = overview;
        this.review = review;
        this.poster = poster;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        title = title;
    }

    public String getReleaseDate() {
        return releaseData;
    }

    public void setReleaseDate(String releaseDate) {
        releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        rating = rating;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        overview = overview;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        review = review;
    }

    public byte[] getPoster() {
        return poster;
    }

    public void setPoster(byte[] poster) {
        poster = poster;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(@NonNull String posterPath) {
        this.posterPath = posterPath;
    }
}