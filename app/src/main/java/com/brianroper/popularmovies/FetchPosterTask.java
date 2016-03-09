package com.brianroper.popularmovies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by brianroper on 3/8/16.
 */
public class FetchPosterTask extends AsyncTask<String, Void, Bitmap>{

    @Override
    protected Bitmap doInBackground(String... urls) {

        try{

            URL url = new URL(urls[0]);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            Bitmap mPoster = BitmapFactory.decodeStream(inputStream);

            return mPoster;

        }
        catch (MalformedURLException e){

            e.printStackTrace();
        }
        catch(IOException e){

            e.printStackTrace();
        }
        return null;
    }
}
