package com.brianroper.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by brianroper on 4/1/16.
 */
public class DBHandler extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "favorites.db";
    private final static String TABLE_MOVIES = "movies";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_TITLE = "title";
    private final static String COLUMN_RELEASE = "release date";
    private final static String COLUMN_RATING = "rating";
    private final static String COLUMN_OVERVIEW = "overview";
    private final static String COLUMN_REVIEW = "review";
    private final static String COLUMN_POSTER = "poster";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE_TABLE " + TABLE_MOVIES + "(" +
                COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_TITLE + "TEXT " +
                COLUMN_RELEASE + "TEXT " +
                COLUMN_RATING + "TEXT " +
                COLUMN_OVERVIEW + "TEXT "+
                COLUMN_REVIEW + "TEXT " +
                COLUMN_POSTER + "BLOB" +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
