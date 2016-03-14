package com.brianroper.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail, new DetailsFragment())
                    .commit();
        }
    }

    public static class DetailsFragment extends Fragment{

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
        private Bitmap mBitmap;
        final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_PARAM = "w370";

        public DetailsFragment() {
            // Required empty public constructor
        }

        //updates the views to display live data
        public void updateDetailViews(){

            mTitleTextView.setText(mTitle);
            mReleaseDateTextView.setText("Release Date: " + mReleaseDate);
            mRatingTextView.setText("Rating: " + mRating + "/10");
            mOverviewTextView.setText("Overview: " + mOverview);
            mPosterImage.setImageBitmap(mBitmap);
        }

        public Bitmap returnBitmapFromTask(String url){

            try {

                FetchPosterTask posterTask = new FetchPosterTask();
                Bitmap bitmap = posterTask.execute(url).get();
                return bitmap;
            }
            catch (ExecutionException e){
                e.printStackTrace();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mTitleTextView = (TextView)getActivity().findViewById(R.id.movie_title);
            mReleaseDateTextView = (TextView)getActivity().findViewById(R.id.release_date);
            mRatingTextView = (TextView)getActivity().findViewById(R.id.rating);
            mOverviewTextView = (TextView)getActivity().findViewById(R.id.plot_overview);
            mPosterImage = (ImageView)getActivity().findViewById(R.id.poster_thumbnail);

            Intent i = getActivity().getIntent();
            mMovieId = i.getStringExtra("MOVIEID");

            String url = "https://api.themoviedb.org/3/movie/" + mMovieId + "?api_key=a0a454fc960bf4f69fa0adf5e13161cf";
            Log.i("URLSTRING", url);

            try{

                FetchDetailsTask detailTask = new FetchDetailsTask();
                String jsonData = detailTask.execute(url).get();

                Log.i("JSONDATA", jsonData);

                JSONObject jsonObject = new JSONObject(jsonData);
                mPosterPath = jsonObject.getString("poster_path");
                mOverview = jsonObject.getString("overview");
                mTitle = jsonObject.getString("original_title");
                mReleaseDate = jsonObject.getString("release_date");
                mRating = jsonObject.getString("vote_average");

                Log.i("JOSNOBJECTS", "Posterpath: " + mPosterPath + " Overview:  " + mOverview
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

            mBitmap = returnBitmapFromTask(BASE_POSTER_URL+POSTER_SIZE_PARAM+mPosterPath);
            updateDetailViews();
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_details, container, false);
        }
    }

}
