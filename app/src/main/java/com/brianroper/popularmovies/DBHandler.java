package com.brianroper.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by brianroper on 4/1/16.
 */
public class DBHandler extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 4;
    private final static String DATABASE_NAME = "favorites.db";

    public DBHandler(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE "+ MovieContract.MovieEntry.TABLE_NAME + "(" +
                MovieContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RELEASE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RATING + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, "+
                MovieContract.MovieEntry.COLUMN_REVIEW + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_POSTER + " BLOB" +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
