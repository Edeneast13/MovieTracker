package com.brianroper.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
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
        final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_PARAM = "w370";
        private Context mContext;
        private String mKey = String.valueOf(R.string.api_key);
        final String KEY_PARAM = "?";

        public DetailsFragment() {
            // Required empty public constructor
        }

        //updates the views to display live data
        public void updateDetailViews(){

            mTitleTextView.setText(mTitle);
            mReleaseDateTextView.setText("Release Date: " + mReleaseDate);
            mRatingTextView.setText("Rating: " + mRating + "/10");
            mOverviewTextView.setText("Overview: " + mOverview);
            Picasso.with(mContext).load(BASE_POSTER_URL+POSTER_SIZE_PARAM+mPosterPath).into(mPosterImage);
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

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https");
            builder.authority("api.themoviedb.org");
            builder.appendPath("3");
            builder.appendPath("movie");
            builder.appendPath(mMovieId);

            String myUrl = builder.build().toString();
            myUrl = myUrl + KEY_PARAM + mKey;

            try{

                FetchDetailsTask detailTask = new FetchDetailsTask();
                String jsonData = detailTask.execute(myUrl).get();

                JSONObject jsonObject = new JSONObject(jsonData);
                mPosterPath = jsonObject.getString("poster_path");
                mOverview = jsonObject.getString("overview");
                mTitle = jsonObject.getString("original_title");
                mReleaseDate = jsonObject.getString("release_date");
                mRating = jsonObject.getString("vote_average");
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

            updateDetailViews();
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_details, container, false);
        }
    }

}
