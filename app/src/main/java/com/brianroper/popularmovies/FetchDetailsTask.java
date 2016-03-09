package com.brianroper.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by brianroper on 3/9/16.
 */
public class FetchDetailsTask extends AsyncTask<String, Void, String> {

    HttpURLConnection urlConnection = null;
    String result = null;
    BufferedReader bufferedReader = null;

    @Override
    protected String doInBackground(String... urls) {

        try {

            URL url = new URL(urls[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(inputStream);

            bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                stringBuffer.append(line + "/n");
            }

            while (stringBuffer.length() == 0) {

                //empty string no reason to parse
                Log.i("StringBuffer", "Empty");
                return null;
            }

            result = stringBuffer.toString();

            return null;
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e ){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onPostExecute(String url){

        try{

            JSONObject jsonObject = new JSONObject(url);
            String posterPath = jsonObject.getString("poster_path");
            String overview = jsonObject.getString("overview");
            String title = jsonObject.getString("original_title");
            String releaseDate = jsonObject.getString("release_date");
            String rating = jsonObject.getString("vote_average");

            Log.i("PosterPath", "Posterpath: " + posterPath + " Overview:  " + overview
                            + " Title: " + title + " ReleaseDate: " + releaseDate
                            + " Rating: " + rating);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }
}
