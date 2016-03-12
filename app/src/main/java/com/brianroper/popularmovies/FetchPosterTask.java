package com.brianroper.popularmovies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by brianroper on 3/8/16.
 */
public class FetchPosterTask extends AsyncTask<String, Void, Bitmap>{

    private HttpURLConnection urlConnection;
    private Bitmap mPoster;

    @Override
    protected Bitmap doInBackground(String... urls) {

        try{

            URL url = new URL(urls[0]);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            mPoster = BitmapFactory.decodeStream(inputStream);

            return mPoster;

        }
        catch (MalformedURLException e){

            e.printStackTrace();
        }
        catch(IOException e){

            e.printStackTrace();
        }
        finally {

            if (urlConnection != null) {

                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Bitmap bitmap){

    }
}
