package com.brianroper.popularmovies.util;

import android.content.Context;

import java.io.File;

/**
 * Created by brianroper on 7/4/16.
 */
public class DbUtil {

    public static boolean activeDb(Context context){

        boolean doesExists;

        File database = context.getDatabasePath("favorites.db");

        if(!database.exists()){

            doesExists = false;
        }
        else{

            doesExists = true;
        }

        return doesExists;
    }
}
