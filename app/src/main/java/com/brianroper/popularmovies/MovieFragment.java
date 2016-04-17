package com.brianroper.popularmovies;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MovieFragment extends Fragment{

    private ArrayList<String> movieIdArray = new ArrayList<String>();
    private ArrayList<String> posterUrlArray = new ArrayList<String>();
    private ArrayList<Bitmap> postersFromFavoritesArray = new ArrayList<Bitmap>();
    private ArrayList<String> titlesFromFavoritesArray = new ArrayList<String>();
    private String movieId = "";
    private String poster = "";
    private Boolean mTwoPane;
    final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
    final String BASE_JSON_REQUEST = "api.themoviedb.org";
    final String JSON_REQUEST_PARAM = "3";
    final String MOVIE_JSON_REQUEST = "movie";
    final String API_KEY_PARAM = "api_key";
    final String POSTER_SIZE_PARAM = "w370";
    final String POPULAR_MOVIES_PARAM = "movie";
    final String TOP_RATED_PARAM = "/top-rated";
    private String sortParameter ="";
    int count = 0;
    private GridView mGridView;
    private String mKey;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public String getPosterPathFromJson(String movieId){

        String posterUrl ="";

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http");
            builder.authority(BASE_JSON_REQUEST);
            builder.appendPath(JSON_REQUEST_PARAM);
            builder.appendPath(MOVIE_JSON_REQUEST);
            builder.appendPath(movieId);
            builder.appendQueryParameter(API_KEY_PARAM, mKey);
            String myUrl = builder.build().toString();

            FetchMovieTask posterPathTask = new FetchMovieTask();
            String jsonData = posterPathTask
                    .execute(myUrl)
                    .get();
            JSONObject jsonObject = new JSONObject(jsonData);
            String posterPath = jsonObject.getString("poster_path");
            posterUrl = BASE_POSTER_URL+POSTER_SIZE_PARAM+posterPath;
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return posterUrl;
    }

    public void getMovieDataFromApi(){

        try {
            //https://www.themoviedb.org/movie
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https");
            builder.authority("www.themoviedb.org");
            builder.appendPath(sortParameter);
            String myUrl = builder.build().toString();

            //retrieves html data from themoviedb.org and sets it to the htmlData variable
            FetchMovieTask movieTask = new FetchMovieTask();
            String htmlData = movieTask.execute(myUrl).get();

            if (htmlData != null) {

                //splits the webpage source code to ignore unnecessary code
                String[] splitHtmlData = htmlData.split("<div class=\"pagination\">");

                //picks out movie id's from web page source code
                Pattern idPattern = Pattern.compile("id=\"movie_(.*?)\"");
                Matcher idMatcher = idPattern.matcher(splitHtmlData[0]);

                while (idMatcher.find()) {

                    movieIdArray.add(idMatcher.group(1));
                }

                for (int i = 0; i < movieIdArray.size(); i++) {
                    count++;
                    movieIdArray.remove(count);
                }

                //creates new Movie objects that store movie id and poster url
                for (int i = 0; i < movieIdArray.size(); i++) {
                    Movie movie = new Movie();
                    movieId = movieIdArray.get(i);
                    movie.setId(movieId);
                    poster = getPosterPathFromJson(movie.getId());
                    movie.setPosterUrl(poster);
                    posterUrlArray.add(movie.getPosterUrl());
                }
            }
            else{
                Toast.makeText(getActivity(), "Network currently not available", Toast.LENGTH_LONG)
                        .show();
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String[] postersArray = new String[posterUrlArray.size()];
        postersArray = posterUrlArray.toArray(postersArray);

        String movie = movieIdArray.get(0);

        Bundle args = new Bundle();
        args.putString("movieId", movie);
        args.putString("status", "online");
        MovieFragment movieFragment = new MovieFragment();
        movieFragment.setArguments(args);

        GridViewAdapter adapter = new GridViewAdapter(getActivity(), getId(), postersArray);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String movie ="";
                movie = movieIdArray.get(position);

                SharedPreferences screenState = PreferenceManager.getDefaultSharedPreferences(getContext());
                mTwoPane = screenState.getBoolean("State",true);

                if(mTwoPane == false){

                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    i.putExtra("MOVIEID", movie);
                    i.putExtra("STATUS", "online");
                    startActivity(i);
                }

                else if(mTwoPane == true){

                    Bundle args = new Bundle();

                    DetailActivity.DetailsFragment detailsFragment = new DetailActivity.DetailsFragment();

                    args.putString("movieId", movie);
                    args.putString("status", "online");
                    detailsFragment.setArguments(args);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, detailsFragment)
                            .commit();
                }
            }
        });
    }

    //populates grid view with posters from the local database
    public void getPosterDataFromFavoritesDb(){

        String title = "";

        try {

            DBHandler dbHandler = new DBHandler(getContext());

            SQLiteDatabase db;
            Bitmap posterBitmap;

            db = dbHandler.getReadableDatabase();

            Cursor c = db.rawQuery("SELECT * FROM movies", null);

            int titleIndex = c.getColumnIndex("title");
            c.moveToFirst();

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

                title = c.getString(titleIndex);
                titlesFromFavoritesArray.add(title);
            }

            int poster = c.getColumnIndex("poster");
            c.moveToFirst();

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

                posterBitmap = DbBitmapUtil.convertByteArrayToBitmap(c.getBlob(poster));
                postersFromFavoritesArray.add(posterBitmap);
            }
            c.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{

            Bitmap[] postersArray = new Bitmap[postersFromFavoritesArray.size()];
            postersArray = postersFromFavoritesArray.toArray(postersArray);

            BitmapGridViewAdapter adapter = new BitmapGridViewAdapter(getActivity(), getId(), postersArray);
            mGridView.setAdapter(adapter);
            final Bitmap[] finalPostersArray = postersArray;

            Bitmap bitmap = finalPostersArray[0];
            byte[] bytes = DbBitmapUtil.convertBitmapToByteArray(bitmap);

            String posterBytes = Base64.encodeToString(bytes, Base64.DEFAULT);
            String t = titlesFromFavoritesArray.get(0);

            PreferenceManager.getDefaultSharedPreferences(getContext())
                    .edit()
                    .putString("POSTER", posterBytes)
                    .putString("TITLE", t)
                    .commit();

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final String finalTitle = titlesFromFavoritesArray.get(position);

                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    i.putExtra("STATUS", "offline");

                    Bitmap bitmap = finalPostersArray[position];
                    byte[] bytes = DbBitmapUtil.convertBitmapToByteArray(bitmap);

                    i.putExtra("POSTER", bytes);
                    i.putExtra("TITLE", finalTitle);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.movie_gridview, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridview);
        mKey = getString(R.string.api_key);

        ButterKnife.bind(getActivity());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = sharedPreferences.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_popular));

        if(sortPref.equals(getString(R.string.pref_sort_rating))){

            sortParameter = POPULAR_MOVIES_PARAM+TOP_RATED_PARAM;
            getMovieDataFromApi();
        }
        else if(sortPref.equals("popular")){

            sortParameter = POPULAR_MOVIES_PARAM;
            getMovieDataFromApi();
        }
        else if(sortPref.equals("favorites")){

            getPosterDataFromFavoritesDb();
        }
        return v;
    }

    //Custom Array Adapter
    public class GridViewAdapter extends ArrayAdapter{

        private Context context;
        private LayoutInflater inflater;
        private int id;
        private String[] imageURls;

        GridViewAdapter(Context context, int id, String[] imageUrls){

            super(context, R.layout.movie_gridview, imageUrls);

            this.context = context;
            this.id = id;
            this.imageURls = imageUrls;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if(convertView == null){

                convertView = inflater.inflate(R.layout.gridview_item, parent, false);
            }
            Picasso.with(context).load(imageURls[position]).fit().into((ImageView) convertView);
            return convertView;
        }
    }

    public class BitmapGridViewAdapter extends ArrayAdapter{

        private Context context;
        private LayoutInflater inflater;
        private int id;
        private Bitmap[] images;
        ImageView imageView;

        BitmapGridViewAdapter(Context context, int id, Bitmap[] images){

            super(context, R.layout.movie_gridview, images);

            this.context = context;
            this.id = id;
            this.images = images;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if(convertView == null){

                convertView = inflater.inflate(R.layout.gridview_item, parent, false);
                imageView = new ImageView(context);
            }
            else{
                imageView = (ImageView)convertView;
            }
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            imageView.setLayoutParams(layoutParams);

            imageView.setAdjustViewBounds(false);
            imageView.requestLayout();

            imageView.getLayoutParams().height = 1085;
            imageView.getLayoutParams().width = 1085;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(images[position]);

            return imageView;
        }
    }
}

