package com.brianroper.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brianroper.popularmovies.model.Favorite;
import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.model.Movie;
import com.brianroper.popularmovies.rest.ApiClient;
import com.brianroper.popularmovies.rest.ApiInterface;
import com.brianroper.popularmovies.util.Util;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.detail_frag_toolbar_title));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    //.addToBackStack("settings")
                    .add(R.id.movie_detail_container, new DetailsFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class DetailsFragment extends Fragment {

        private int mMovieId;
        private int mPosition;
        private String mReview = "";
        private String mKey;

        //views
        private TextView mTitleTextView;
        private TextView mReleaseDateTextView;
        private TextView mRatingTextView;
        private TextView mOverviewTextView;
        private TextView mReviewTextView;
        private ImageView mPosterImage;
        private TextView mTrailerTextView;
        private FloatingActionButton mFloatingActionButton;
        private Context mContext;
        //urls
        final String REVIEW_JSON_REQUEST = "reviews";
        final String TRAILER_JSON_REQUEST = "videos";
        final String YOUTUBE_BASE_URL = "www.youtube.com";
        final String YOUTUBE_WATCH_PARAM = "watch";
        final String YOUTUBE_VIDEO_ID_QUERY_PARAM = "v";
        final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_PARAM = "w370";

        private Realm mRealm;

        private Movie mCurrentMovie;

        public void DetailsFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View root = inflater.inflate(R.layout.fragment_details, container, false);

            initializeViews(root);
            initializeRealm();

            returnApiKey();
            returnIntentExtras();

            apiRequestDetail(mMovieId);

            setHasOptionsMenu(true);

            setFloatingActionButtonListener();

            return root;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mRealm.close();
        }

        /**
         * initialize all the view in the fragment
         */
        public void initializeViews(View root){
            mTitleTextView = (TextView) root.findViewById(R.id.movie_title);
            mReleaseDateTextView = (TextView) root.findViewById(R.id.release_date);
            mRatingTextView = (TextView) root.findViewById(R.id.rating);
            mOverviewTextView = (TextView) root.findViewById(R.id.plot_overview);
            mPosterImage = (ImageView) root.findViewById(R.id.poster_thumbnail);
            mFloatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.favorites_fab);
            mTrailerTextView = (TextView) root.findViewById(R.id.trailer_textview);
            mReviewTextView = (TextView) root.findViewById(R.id.review_textview);
        }

        /**
         * listener for floating action button
         */
        public void setFloatingActionButtonListener(){
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateRealm(mCurrentMovie);
                }
            });
        }

        /**
         * retrieve api key
         */
        public void returnApiKey(){
            mKey = getString(R.string.api_key);
        }

        /**
         * return intent extras from previous activity
         */
        public void returnIntentExtras(){
            Intent detailIntent = getActivity().getIntent();
            mPosition = detailIntent.getIntExtra("position", 0);
            mMovieId = detailIntent.getIntExtra("id", 0);
        }

        /**
         * initialize realm for this thread
         */
        public void initializeRealm() {
            mRealm.init(getActivity());

            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();

            mRealm = Realm.getInstance(realmConfiguration);
        }

        /**
         * begins an intent that opens the youtube app
         * to watch the selected movie trailer
         */
        public void playVideoInYouTubeApp(String url) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setPackage("com.google.android.youtube");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(i);
            }
        }

        /**
         * add a favorite to realm db
         */
        public void updateRealm(final Movie movie){
            mRealm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm) {
                    Favorite favorite = realm.createObject(Favorite.class, movie.getId());
                    favorite.title = movie.getTitle();
                    favorite.rating = movie.getRating().toString();
                    favorite.overview = movie.getOverview();
                    favorite.releaseData = movie.getReleaseData();
                    favorite.poster = Util.convertBitmapToByteArray(Util.convertImageViewToBitmap(mPosterImage));
                    favorite.posterPath = movie.getPosterPath();
                }
            });
        }

        /**
         * api call that retrieves movie detail based on id
         */
        public void apiRequestDetail(final int id){
            final String API_KEY = getString(R.string.api_key);

            if(API_KEY.isEmpty()){
                Toast.makeText(getActivity(),
                        "Please get your api key from themobiedb.org first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<Movie> call = apiService.getDetails(id, API_KEY);

            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    mCurrentMovie = response.body();
                    updateDetailViews(mCurrentMovie);
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    Log.e("Response Error: ", t.toString());
                }
            });
        }

        /**
         * updates the views in the ui with database data
         */
        public void updateDetailViews(Movie movie) {
            mTitleTextView.setText(movie.getTitle());
            mReleaseDateTextView.setText(movie.getReleaseData());
            mOverviewTextView.setText(movie.getOverview());
            mRatingTextView.setText(movie.getRating().toString());
            mTrailerTextView.setText("Trailer");

            Picasso.with(mContext)
                    .load(BASE_POSTER_URL + POSTER_SIZE_PARAM + movie.getPosterPath())
                    .into(mPosterImage);
        }
    }
}
