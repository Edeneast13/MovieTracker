package com.brianroper.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by brianroper on 4/19/16.
 */
public class NetworkUtil {

    static public boolean activeNetworkCheck(Context c){

        ConnectivityManager connectivityManager = (ConnectivityManager)c.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
