package com.brianroper.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
        getSupportActionBar().setTitle(getString(R.string.detail_frag_toolbar_title));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack("settings")
                    .add(R.id.movie_detail_container, new DetailsFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    
    public static class DetailsFragment extends Fragment{

        //Data
        private String mTitle ="";
        private String mPosterPath;
        private String mOverview ="";
        private String mRating = "";
        private String mReleaseDate;
        private String mMovieId;
        private String mTrailer ="";
        private String mTrailerUrl = "";
        private String mReviewUrl = "";
        private String mReview = "";
        private String mAuthor = "";
        private String mContent = "";
        private byte[] mBitmapFromFavorites;
        private String mTitleFromFavorites = "";
        private String mStatus = "";
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
        private String mKey;
        final String API_KEY_PARAM = "api_key";
        final String BASE_JSON_REQUEST = "api.themoviedb.org";
        final String JSON_REQUEST_PARAM = "3";
        final String MOVIE_JSON_REQUEST = "movie";
        final String REVIEW_JSON_REQUEST = "reviews";
        final String TRAILER_JSON_REQUEST = "videos";
        final String YOUTUBE_BASE_URL = "www.youtube.com";
        final String YOUTUBE_WATCH_PARAM = "watch";
        final String YOUTUBE_VIDEO_ID_QUERY_PARAM = "v";
        final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_PARAM = "w370";

        public void DetailsFragment(){

        }

        //updates the views to display live data
        public void updateDetailViews(){

            mTitleTextView.setText(mTitle);
            mReleaseDateTextView.setText("Release Date: " + mReleaseDate);
            mRatingTextView.setText("Rating: " + mRating + "/10");
            mOverviewTextView.setText("Overview: " + mOverview);
            mTrailerTextView.setText("Play Trailer");
            mReviewTextView.setText(mReview);
        }

        public void retrieveTrailerJson(String url) {

            if (NetworkUtil.activeNetworkCheck(getActivity())) {
                try {

                    FetchDetailsTask trailerTask = new FetchDetailsTask();
                    String trailerData = trailerTask.execute(url).get();
                    //key param from json
                    JSONObject trailerJsonObject = new JSONObject(trailerData);
                    JSONArray trailerJsonArray = trailerJsonObject.getJSONArray("results");

                    for (int i = 0; i < trailerJsonArray.length(); i++) {

                        JSONObject arrayElement = trailerJsonArray.getJSONObject(i);
                        String key = arrayElement.getString("key");
                        mTrailer = key;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        public void retrieveReviewJson(String url) {

            if (NetworkUtil.activeNetworkCheck(getActivity())) {
                try {

                    FetchDetailsTask reviewTask = new FetchDetailsTask();
                    String reviewData = reviewTask.execute(url).get();

                    JSONObject reviewJsonObject = new JSONObject(reviewData);
                    JSONArray reviewJsonArray = reviewJsonObject.getJSONArray("results");

                    for (int i = 0; i < reviewJsonArray.length(); i++) {

                        JSONObject arrayElement = reviewJsonArray.getJSONObject(i);
                        String author = arrayElement.getString("author");
                        String content = arrayElement.getString("content");

                        Review review = new Review();
                        review.setAuthor(author);
                        review.setContent(content);

                        mAuthor = review.getAuthor();
                        mContent = review.getContent();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        public void playVideoInYouTubeApp(String url){

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setPackage("com.google.android.youtube");
            i.setData(Uri.parse(url));

            if(i.resolveActivity(getActivity().getPackageManager()) != null){

                startActivity(i);
            }
        }

        public void populateDetailViewOnline(){

            if(NetworkUtil.activeNetworkCheck(getActivity())) {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https");
                builder.authority(BASE_JSON_REQUEST);
                builder.appendPath(JSON_REQUEST_PARAM);
                builder.appendPath(MOVIE_JSON_REQUEST);
                builder.appendPath(mMovieId);
                builder.appendQueryParameter(API_KEY_PARAM, mKey);

                String myUrl = builder.build().toString();

                Uri.Builder trailerRequest = new Uri.Builder();
                trailerRequest.scheme("https");
                trailerRequest.authority(BASE_JSON_REQUEST);
                trailerRequest.appendPath(JSON_REQUEST_PARAM);
                trailerRequest.appendPath(MOVIE_JSON_REQUEST);
                trailerRequest.appendPath(mMovieId);
                trailerRequest.appendPath(TRAILER_JSON_REQUEST);
                trailerRequest.appendQueryParameter(API_KEY_PARAM, mKey);

                mTrailerUrl = trailerRequest.build().toString();

                Uri.Builder reviewRequest = new Uri.Builder();
                reviewRequest.scheme("https");
                reviewRequest.authority(BASE_JSON_REQUEST);
                reviewRequest.appendPath(JSON_REQUEST_PARAM);
                reviewRequest.appendPath(MOVIE_JSON_REQUEST);
                reviewRequest.appendPath(mMovieId);
                reviewRequest.appendPath(REVIEW_JSON_REQUEST);
                reviewRequest.appendQueryParameter(API_KEY_PARAM, mKey);

                mReviewUrl = reviewRequest.build().toString();

                try {

                    FetchDetailsTask detailTask = new FetchDetailsTask();
                    String jsonData = detailTask.execute(myUrl).get();

                    JSONObject jsonObject = new JSONObject(jsonData);
                    mPosterPath = jsonObject.getString("poster_path");
                    mOverview = jsonObject.getString("overview");
                    mTitle = jsonObject.getString("original_title");
                    mReleaseDate = jsonObject.getString("release_date");
                    mRating = jsonObject.getString("vote_average");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                retrieveReviewJson(mReviewUrl);

                mReview = "Author: " + mAuthor + "\n" + mContent;

                Picasso.with(getContext()).load(BASE_POSTER_URL + POSTER_SIZE_PARAM + mPosterPath).into(mPosterImage);
                updateDetailViews();

                DBHandler dbHandler = new DBHandler(getContext());

                SQLiteDatabase db;

                db = dbHandler.getReadableDatabase();

                try {
                    Cursor c = db.rawQuery("SELECT * FROM movies WHERE title = \"" + mTitle + "\"", null);
                    c.moveToFirst();
                    int titleIndex = c.getColumnIndex("title");
                    String title = c.getString(titleIndex);

                    if (title.equals(mTitle)) {

                        mFloatingActionButton.setImageResource(R.drawable.starempty);
                    } else {

                        mFloatingActionButton.setImageResource(R.drawable.starfull);
                    }
                    c.close();
                }
                catch (CursorIndexOutOfBoundsException e){
                    e.printStackTrace();

                    mFloatingActionButton.setImageResource(R.drawable.starfull);
                }


                mTrailerTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            retrieveTrailerJson(mTrailerUrl);

                            Uri.Builder videoBuilder = new Uri.Builder();
                            videoBuilder.scheme("https");
                            videoBuilder.authority(YOUTUBE_BASE_URL);
                            videoBuilder.appendPath(YOUTUBE_WATCH_PARAM);
                            videoBuilder.appendQueryParameter(YOUTUBE_VIDEO_ID_QUERY_PARAM, mTrailer);

                            String fullYoutubeUrl = videoBuilder.build().toString();

                            playVideoInYouTubeApp(fullYoutubeUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DBHandler dbHandler = new DBHandler(getContext());

                        SQLiteDatabase db;
                        Cursor c = null;

                        try {
                            db = dbHandler.getReadableDatabase();

                            c = db.rawQuery("SELECT * FROM movies WHERE title = \"" + mTitle + "\"", null);
                            c.moveToFirst();
                            int titleIndex = c.getColumnIndex("title");
                            String title = c.getString(titleIndex);

                            if (title.equals(mTitle)) {

                                mFloatingActionButton.setImageResource(R.drawable.starfull);

                                db = dbHandler.getWritableDatabase();

                                db.delete("movies", "title == " + "\"" + mTitle + "\"", null);

                                c.close();

                                Toast.makeText(getActivity(), getString(R.string.favorites_remove_toast),
                                        Toast.LENGTH_LONG).show();
                            } else if (!(title.equals(mTitle))) {

                                mFloatingActionButton.setImageResource(R.drawable.starempty);

                                db = dbHandler.getWritableDatabase();

                                ImageView mPosterRef = mPosterImage;
                                Bitmap posterBitmap = DbBitmapUtil.convertImageViewToBitmap(mPosterRef);
                                byte[] posterByteArray = DbBitmapUtil.convertBitmapToByteArray(posterBitmap);

                                ContentValues values = new ContentValues();
                                values.put("title", mTitle);
                                values.put("release", mReleaseDate);
                                values.put("rating", mRating);
                                values.put("overview", mOverview);
                                values.put("review", mReview);
                                values.put("poster", posterByteArray);
                                Log.i("POSTER BYTE ARRAY: ", posterByteArray.toString());

                                db.insertWithOnConflict("movies", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                                c.close();

                                Toast.makeText(getActivity(), "Saved to Favorites", Toast.LENGTH_LONG).show();
                            }
                        } catch (CursorIndexOutOfBoundsException e) {
                            db = dbHandler.getWritableDatabase();

                            ImageView mPosterRef = mPosterImage;
                            Bitmap posterBitmap = DbBitmapUtil.convertImageViewToBitmap(mPosterRef);
                            byte[] posterByteArray = DbBitmapUtil.convertBitmapToByteArray(posterBitmap);

                            ContentValues values = new ContentValues();
                            values.put("title", mTitle);
                            values.put("release", mReleaseDate);
                            values.put("rating", mRating);
                            values.put("overview", mOverview);
                            values.put("review", mReview);
                            values.put("poster", posterByteArray);
                            Log.i("POSTER BYTE ARRAY: ", posterByteArray.toString());

                            db.insertWithOnConflict("movies", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                            mFloatingActionButton.setImageResource(R.drawable.starempty);

                            c.close();

                            Toast.makeText(getActivity(), "Saved to Favorites", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        public void populateDetailViewOffline(){

            DBHandler dbHandler = new DBHandler(getContext());

            SQLiteDatabase db;
            db = dbHandler.getReadableDatabase();

            Cursor c = db.rawQuery("SELECT * FROM movies WHERE title = \"" + mTitle + "\"", null);

            if(c != null && c.moveToFirst()){

                int releaseIndex = c.getColumnIndex("release");
                c.moveToFirst();
                mReleaseDate = c.getString(releaseIndex);

                int ratingIndex = c.getColumnIndex("rating");
                c.moveToFirst();
                mRating = c.getString(ratingIndex);

                int overviewIndex = c.getColumnIndex("overview");
                c.moveToFirst();
                mOverview = c.getString(overviewIndex);

                int reviewIndex = c.getColumnIndex("review");
                c.moveToFirst();
                mReview = c.getString(reviewIndex);

                c.close();
            }

            Bitmap bitmap = DbBitmapUtil.convertByteArrayToBitmap(mBitmapFromFavorites);
            mPosterImage.setImageBitmap(bitmap);

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
                    mFloatingActionButton.getLayoutParams();

            layoutParams.setAnchorId(View.NO_ID);
            mFloatingActionButton.setLayoutParams(layoutParams);
            mFloatingActionButton.setVisibility(View.GONE);

            updateDetailViews();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_details, container, false);

            mTitleTextView = (TextView)v.findViewById(R.id.movie_title);
            mReleaseDateTextView = (TextView)v.findViewById(R.id.release_date);
            mRatingTextView = (TextView)v.findViewById(R.id.rating);
            mOverviewTextView = (TextView)v.findViewById(R.id.plot_overview);
            mPosterImage = (ImageView) v.findViewById(R.id.poster_thumbnail);
            mFloatingActionButton = (FloatingActionButton)getActivity().findViewById(R.id.favorites_fab);
            mTrailerTextView = (TextView)v.findViewById(R.id.trailer_textview);
            mReviewTextView = (TextView)v.findViewById(R.id.review_textview);

            mKey = getString(R.string.api_key);

            Intent i = getActivity().getIntent();

            Bundle args = getArguments();

            if(args != null){

                mMovieId = args.getString("movieId");
                mStatus = args.getString("status");
                Log.i("movieId: ", mMovieId);
                Log.i("status: ", mStatus);
            }
            else if(args == null){

                try{

                    mMovieId = i.getStringExtra("MOVIEID");
                    mStatus = i.getStringExtra("STATUS");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

           if (mStatus.equals("online")){

                populateDetailViewOnline();
            }
            else if(mStatus.equals("offline")){

               try{

                   if(mBitmapFromFavorites == null){

                       String bytes = PreferenceManager.getDefaultSharedPreferences(getContext())
                               .getString("POSTER", "");

                       byte[] array = Base64.decode(bytes, Base64.DEFAULT);

                       mBitmapFromFavorites = array;
                   }
                   else{

                       mBitmapFromFavorites = i.getByteArrayExtra("POSTER");
                   }

                   if(mTitleFromFavorites == null){

                       String title = PreferenceManager.getDefaultSharedPreferences(getContext())
                               .getString("TITLE", "");

                       mTitleFromFavorites = title;
                       Log.i("TITLE", mTitle);
                   }
                   else{

                       mTitleFromFavorites = i.getStringExtra("TITLE");
                       Log.i("TITLE", mTitle);
                   }
               }
               catch (Exception e){
                   e.printStackTrace();
               }

                mTitle = mTitleFromFavorites;
                populateDetailViewOffline();
            };

            setHasOptionsMenu(true);
            // Inflate the layout for this fragment
            return v;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }
}
