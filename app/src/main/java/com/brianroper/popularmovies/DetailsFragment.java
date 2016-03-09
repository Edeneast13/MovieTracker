package com.brianroper.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class DetailsFragment extends Fragment {

    private String mTitle ="";
    private String mPosterPath;
    private String mOverview ="";
    private String mRating = "";
    private String mReleaseDate;
    private String mMovieId;
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private TextView mOverviewTextView;
    private ImageView mPosterImage;
    private Bitmap bitmap;

    public DetailsFragment() {
        // Required empty public constructor
    }

    //updates the views to display live data
    public void updateDetailViews(){

        mTitleTextView.setText(mTitle);
        mReleaseDateTextView.setText("Release Date: " + mReleaseDate);
        mRatingTextView.setText("Rating: " + mRating);
        mOverviewTextView.setText("Overview: " + mOverview);
        mPosterImage.setImageBitmap(bitmap);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = "http://api.themoviedb.org/3/movie/" + mMovieId + "?api_key=a0a454fc960bf4f69fa0adf5e13161cf";

        FetchDetailsTask detailTask = new FetchDetailsTask();
        detailTask.execute(url);

        try{

             /*TO DO: find out how to properly query poster url and use it to update imageviews */
            FetchPosterTask posterTask = new FetchPosterTask();
            bitmap = posterTask.execute(mPosterPath).get();

            //gets JSON data from themoviedb detail api and stores it
            JSONObject jsonObject = new JSONObject(url);
            mPosterPath = jsonObject.getString("poster_path");
            mOverview = jsonObject.getString("overview");
            mTitle = jsonObject.getString("original_title");
            mReleaseDate = jsonObject.getString("release_date");
            mRating = jsonObject.getString("vote_average");

            Log.i("PosterPath", "Posterpath: " + mPosterPath + " Overview:  " + mOverview
                    + " Title: " + mTitle + " ReleaseDate: " + mReleaseDate
                    + " Rating: " + mRating);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }

        catch(ExecutionException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTitleTextView = (TextView)getView().findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView)getView().findViewById(R.id.release_date);
        mRatingTextView = (TextView)getView().findViewById(R.id.rating);
        mOverviewTextView = (TextView)getView().findViewById(R.id.plot_overview);

        Intent i = getActivity().getIntent();
        if(i != null && i.hasExtra(Intent.EXTRA_TEXT)){

            mMovieId = i.getStringExtra(Intent.EXTRA_TEXT);
        }

        updateDetailViews();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

}
