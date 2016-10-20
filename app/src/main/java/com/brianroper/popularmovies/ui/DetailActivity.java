package com.brianroper.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.brianroper.popularmovies.model.Review;
import com.brianroper.popularmovies.model.ReviewResponse;
import com.brianroper.popularmovies.model.Trailer;
import com.brianroper.popularmovies.model.TrailerResponse;
import com.brianroper.popularmovies.rest.ApiClient;
import com.brianroper.popularmovies.rest.ApiInterface;
import com.brianroper.popularmovies.util.Util;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private int mMovieId;
    private int mPosition;
    private String mKey;

    //views
    private Toolbar mToolbar;
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private TextView mOverviewTextView;
    private TextView mReviewTextView;
    private ImageView mPosterImage;
    private TextView mTrailerTextView;
    private FloatingActionButton mFloatingActionButton;
    private CardView mMainSurface;
    private CardView mOverViewSurface;
    private CardView mTrailerSurface;
    private CardView mReviewSurface;
    private ImageView mDetailHeaderImageView;
    private Context mContext;

    //youtube url params
    final String YOUTUBE_BASE_URL = "www.youtube.com";
    final String YOUTUBE_WATCH_PARAM = "watch";
    final String YOUTUBE_VIDEO_ID_QUERY_PARAM = "v";

    //realm
    private Realm mRealm;
    private Movie mCurrentMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        initializeRealm();
        initializeToolbarBehavior(mToolbar);

        returnApiKey();
        returnIntentExtras();

        apiRequestDetail(mMovieId, mKey);
        apiRequestTrailer(mMovieId, mKey);
        apiRequestReview(mMovieId, mKey);

        setFloatingActionButtonListener();
        setCardBackgroundColors();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * closes the current realm instance when the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    /**
     * initialize all the view in the fragment
     */
    public void initializeViews(){
        mTitleTextView = (TextView)findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView)findViewById(R.id.release_date);
        mRatingTextView = (TextView)findViewById(R.id.rating);
        mOverviewTextView = (TextView)findViewById(R.id.plot_overview);
        mPosterImage = (ImageView)findViewById(R.id.poster_thumbnail);
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.favorites_fab);
        mTrailerTextView = (TextView)findViewById(R.id.trailer_textview);
        mReviewTextView = (TextView)findViewById(R.id.review_textview);
        mMainSurface = (CardView)findViewById(R.id.main_surface);
        mOverViewSurface = (CardView)findViewById(R.id.overview_surface);
        mTrailerSurface = (CardView)findViewById(R.id.trailer_surface);
        mReviewSurface = (CardView)findViewById(R.id.review_surface);
        mToolbar = (Toolbar)findViewById(R.id.detail_toolbar);
        mDetailHeaderImageView = (ImageView)findViewById(R.id.detail_header);
    }

    /**
     * handles toolbar behavior
     */
    public void initializeToolbarBehavior(Toolbar toolbar){
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(getString(R.string.app_name));
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
        Intent detailIntent = getIntent();
        mPosition = detailIntent.getIntExtra("position", 0);
        mMovieId = detailIntent.getIntExtra("id", 0);
    }

    /**
     * initialize realm for this thread
     */
    public void initializeRealm() {
        mRealm.init(getApplicationContext());

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
        if (i.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivity(i);
        }
    }

    /**
     * add a favorite to realm db
     */
    public void updateRealm(final Movie movie){
        try {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Favorite favorite = realm.createObject(Favorite.class, movie.getId());
                    favorite.title = movie.getTitle();
                    favorite.rating = movie.getRating().toString();
                    favorite.overview = movie.getOverview();
                    favorite.releaseData = movie.getReleaseData();
                    favorite.poster = Util.convertBitmapToByteArray(Util.convertImageViewToBitmap(mPosterImage));
                    favorite.posterPath = movie.getPosterPath();
                    mFloatingActionButton.setImageResource(R.drawable.starempty);
                }
            });
        }catch(RealmPrimaryKeyConstraintException e){
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(Favorite.class).equalTo("id", movie.getId()).findFirst().deleteFromRealm();
                    mFloatingActionButton.setImageResource(R.drawable.starfull);
                }
            });
        }
    }

    /**
     * api call that retrieves movie detail based on id
     */
    public void apiRequestDetail(final int id, String key){

        if(key.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    "Please get your api key from themobiedb.org first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //test for active network connection before making api call
        if(Util.activeNetworkCheck(getApplicationContext()) == true){
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<Movie> call = apiService.getDetails(id, key);

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
        }else{Util.noNetworkMessage(getApplicationContext());}
    }

    /**
     * api call that retrieves trailer data based on movie id
     */
    public void apiRequestTrailer(final int id, String key){

        if(key.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    "Please get your api key from themobiedb.org first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(Util.activeNetworkCheck(getApplicationContext()) == true){
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<TrailerResponse> call = apiService.getVideos(id, key);

            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    TrailerResponse videos = response.body();
                    if(videos!=null){
                        Trailer trailer = videos.getResults().get(0);
                        setTrailerTextViewListener(trailer);
                    }
                }
                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    /**
     * api call that retrieves review data for movies based on id
     */
    public void apiRequestReview(final int id, String key){

        if(key.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    "Please get your api key from themobiedb.org first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(Util.activeNetworkCheck(getApplicationContext()) == true){
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ReviewResponse> call = apiService.getReviews(id, key);

            call.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    ReviewResponse reviews = response.body();
                    try {
                        Review review = reviews.getResults().get(0);
                        setReviewTextViewText(review);
                    }
                    catch (Exception e){mReviewSurface.setVisibility(View.GONE);}
                }
                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    /**
     * updates the views in the ui with database data
     */
    public void updateDetailViews(Movie movie) {
        mTitleTextView.setText(movie.getTitle());
        mReleaseDateTextView.setText(movie.getReleaseData());
        mOverviewTextView.setText(movie.getOverview());
        mRatingTextView.setText(movie.getRating().toString() + "/10");
        mTrailerTextView.setText("Trailer");

        //https://image.tmdb.org/t/p/w500/
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("image.tmdb.org");
        builder.appendPath("t");
        builder.appendPath("p");
        builder.appendPath("w500");

        Picasso.with(mContext)
                .load(builder.build().toString() + movie.getBackdropPath())
                .fit()
                .into(mDetailHeaderImageView);

        //poster url params
        final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_PARAM = "w370";

        Picasso.with(mContext)
                .load(BASE_POSTER_URL + POSTER_SIZE_PARAM + movie.getPosterPath())
                .into(mPosterImage);

        isFavoriteSaved();
    }

    /**
     * handles behavior of the trailer textview
     */
    public void setTrailerTextViewListener(final Trailer trailer) {
        mTrailerTextView.setText(trailer.getName());
        mTrailerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trailer != null) {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https");
                    builder.authority(YOUTUBE_BASE_URL);
                    builder.appendPath(YOUTUBE_WATCH_PARAM);
                    builder.appendQueryParameter(YOUTUBE_VIDEO_ID_QUERY_PARAM, trailer.getKey());

                    playVideoInYouTubeApp(builder.build().toString());
                } else {
                    mTrailerTextView.setText("");
                }
            }
        });
    }

    /**
     * sets the review text to match data retrieved from api
     */
    public void setReviewTextViewText(final Review review){
        mReviewTextView.setText("Author: " + review.getAuthor() + "\n" + review.getContent());
    }

    /**
     * sets card color background
     */
    public void setCardBackgroundColors(){
        mMainSurface.setCardBackgroundColor(getResources().getColor(R.color.cardview_light_background));
        mReviewSurface.setCardBackgroundColor(getResources().getColor(R.color.cardview_light_background));
        mTrailerSurface.setCardBackgroundColor(getResources().getColor(R.color.cardview_light_background));
        mOverViewSurface.setCardBackgroundColor(getResources().getColor(R.color.cardview_light_background));
    }

    /**
     * check for existing value in realm and adjusts fab resource accordingly
     */
    public void isFavoriteSaved(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Favorite favorite = realm.where(Favorite.class).equalTo("id", mMovieId).findFirst();
                if (favorite != null) {
                    if (favorite.getId() == mMovieId) {
                        mFloatingActionButton.setImageResource(R.drawable.starempty);
                    }
                } else {
                    mFloatingActionButton.setImageResource(R.drawable.starfull);
                }
            }
        });
    }
}
