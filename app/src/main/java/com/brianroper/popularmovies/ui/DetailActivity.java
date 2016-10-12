package com.brianroper.popularmovies.ui;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brianroper.popularmovies.model.Favorite;
import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.model.Review;
import com.brianroper.popularmovies.util.DbBitmapUtil;
import com.brianroper.popularmovies.util.NetworkUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;

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

        //Data
        private String mTitle = "";
        private String mPosterPath;
        private String mOverview = "";
        private String mRating = "";
        private String mReleaseDate;
        private String mMovieId;
        private String mTrailer = "";
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

        private Realm mRealm;

        public void DetailsFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View root = inflater.inflate(R.layout.fragment_details, container, false);

            initializeViews(root);
            initializeRealm();

            mKey = getString(R.string.api_key);

            setHasOptionsMenu(true);
            // Inflate the layout for this fragment
            return root;
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
         * updates the views in the ui with database data
         */
        public void updateDetailViews() {
            mTitleTextView.setText(mTitle);
            mReleaseDateTextView.setText(mReleaseDate + "  |");
            mRatingTextView.setText(mRating + "/10");
            mOverviewTextView.setText(mOverview);
            mTrailerTextView.setText("Play Trailer");
            mReviewTextView.setText(mReview);
        }

        /**
         * initialize realm for this thread
         */
        public void initializeRealm(){
            Realm.init(getActivity());
            mRealm = Realm.getDefaultInstance();
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

        public void storeFavoriteInRealm() {
            ImageView mPosterRef = mPosterImage;
            Bitmap posterBitmap = DbBitmapUtil.convertImageViewToBitmap(mPosterRef);
            byte[] posterByteArray = DbBitmapUtil.convertBitmapToByteArray(posterBitmap);

            Favorite favorite = new Favorite(mTitle,
                    mReleaseDate,
                    mRating,
                    mOverview,
                    mReview,
                    posterByteArray);

            final RealmResults<Favorite> fCollection = mRealm
                    .where(Favorite.class)
                    .findAll();

            mRealm.beginTransaction();
            final Favorite managedFavorite = mRealm.copyToRealm(favorite);
            mRealm.commitTransaction();
        }
    }
}
