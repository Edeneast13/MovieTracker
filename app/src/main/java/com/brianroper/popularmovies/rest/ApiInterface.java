package com.brianroper.popularmovies.rest;

import com.brianroper.popularmovies.model.Movie;
import com.brianroper.popularmovies.model.MovieResponse;
import com.brianroper.popularmovies.model.ReviewResponse;
import com.brianroper.popularmovies.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by brianroper on 10/11/16.
 */
public interface ApiInterface {
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRated(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<Movie> getDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MovieResponse> getPopular(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getVideos(@Path("id")int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getReviews(@Path("id")int id, @Query("api_key") String apiKey);
}
