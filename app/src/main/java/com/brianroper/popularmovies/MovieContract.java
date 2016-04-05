package com.brianroper.popularmovies;

import android.provider.BaseColumns;

/**
 * Created by brianroper on 4/4/16.
 */
public class MovieContract {

    MovieContract(){

    }

    public static final class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE = "release";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_POSTER = "poster";
    }
}
