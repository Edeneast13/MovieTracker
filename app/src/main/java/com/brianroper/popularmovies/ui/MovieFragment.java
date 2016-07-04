package com.brianroper.popularmovies.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brianroper.popularmovies.Movie;
import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.async.FetchMovieTask;
import com.brianroper.popularmovies.database.DBHandler;
import com.brianroper.popularmovies.util.DbBitmapUtil;
import com.brianroper.popularmovies.util.NetworkUtil;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieFragment extends Fragment{

    private ArrayList<String> movieIdArray = new ArrayList<String>();
    private ArrayList<String> posterUrlArray = new ArrayList<String>();
    private ArrayList<Bitmap> postersFromFavoritesArray = new ArrayList<Bitmap>();
    private ArrayList<String> titlesFromFavoritesArray = new ArrayList<String>();
    private String[] posterArray;
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
    private TextView mEmptyView;
    private Bundle mBundle;

    public MovieFragment() {
        // Required empty public constructor
    }

    public static MovieFragment newInstance(){

        return new MovieFragment();
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

            if(NetworkUtil.activeNetworkCheck(getActivity())){

                FetchMovieTask posterPathTask = new FetchMovieTask();
                String jsonData = posterPathTask
                        .execute(myUrl)
                        .get();
                JSONObject jsonObject = new JSONObject(jsonData);
                String posterPath = jsonObject.getString("poster_path");
                posterUrl = BASE_POSTER_URL+POSTER_SIZE_PARAM+posterPath;
            }
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

        String htmlData = "";

        try {
            if(NetworkUtil.activeNetworkCheck(getActivity())) {
                //https://www.themoviedb.org/movie
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https");
                builder.authority("www.themoviedb.org");
                builder.appendPath(sortParameter);
                String myUrl = builder.build().toString();

                //retrieves html data from themoviedb.org and sets it to the htmlData variable
                FetchMovieTask movieTask = new FetchMovieTask();
                htmlData = movieTask.execute(myUrl).get();

            }
            if (htmlData != null) {

                //splits the htmldata on a different thread
                SplitPageRunnable runnable = new SplitPageRunnable(
                        htmlData,
                        movieId,
                        poster,
                        count,
                        movieIdArray,
                        posterUrlArray,
                        posterArray);

                runnable.run();

                posterArray = new String[posterUrlArray.size()];
                posterArray = posterUrlArray.toArray(posterArray);

                String movie = movieIdArray.get(0);

                Bundle args = new Bundle();
                args.putString("movieId", movie);
                args.putString("status", "online");
                MovieFragment movieFragment = new MovieFragment();
                movieFragment.setArguments(args);

                GridViewAdapter adapter = new GridViewAdapter(getActivity(), getId(), posterArray);
                mGridView.setAdapter(adapter);
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String movie ="";
                        movie = movieIdArray.get(position);

                        SharedPreferences screenState = PreferenceManager
                                .getDefaultSharedPreferences(getContext());

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
            db.close();
            dbHandler.close();

            Bitmap[] postersArray = new Bitmap[postersFromFavoritesArray.size()];
            postersArray = postersFromFavoritesArray.toArray(postersArray);

            BitmapGridViewAdapter adapter = new BitmapGridViewAdapter(getActivity(), getId(), postersArray);
            View emptyView = getActivity().findViewById(R.id.empty_textview);
            mGridView.setEmptyView(emptyView);
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
                        .apply();

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
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();

            for (int i = 0; i < 2; i++) {

                Toast.makeText(getActivity(), getString(R.string.no_favorites),
                        Toast.LENGTH_LONG).show();
            }

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());

            sharedPreferences
                    .edit()
                    .putString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular))
                    .apply();

            Intent favoritesFailedIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(favoritesFailedIntent);


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.movie_gridview, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridview);
        mKey = getString(R.string.api_key);
        mEmptyView = (TextView) v.findViewById(R.id.empty_textview);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            mBundle = ActivityOptions.makeSceneTransitionAnimation(getActivity())
                    .toBundle();
        }

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
            GridView.LayoutParams layoutParams = new GridView.LayoutParams(820, 820);
            imageView.setLayoutParams(layoutParams);

            /*imageView.setAdjustViewBounds(false);
            imageView.requestLayout();

            imageView.getLayoutParams().height = 1085;
            imageView.getLayoutParams().width = 1085;*/

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(images[position]);

            return imageView;
        }
}

    //class for splitting the downloaded data on another thread
    public class SplitPageRunnable implements Runnable{

        private String data;
        private String id;
        private String poster;
        private int count;
        private ArrayList<String> idArray;
        private ArrayList<String> posterUrlArray;
        private String[] posterArray;

        public SplitPageRunnable(String data, String id,
                                 String poster, int count,
                                 ArrayList<String> idArray,
                                 ArrayList<String> posterUrlArray,
                                 String[] posterArray) {
            this.data = data;
            this.id = id;
            this.poster = poster;
            this.count = count;
            this.idArray = idArray;
            this.posterUrlArray = posterUrlArray;
            this.posterArray = posterArray;
        }

        @Override
        public void run() {

            String [] splitData = data.split("<div class=\"pagination\">");

            Pattern idPattern = Pattern.compile("id=\"movie_(.*?)\"");
            Matcher idMatcher = idPattern.matcher(splitData[0]);

            while(idMatcher.find()){

                idArray.add(idMatcher.group(1));
            }

            for(int i = 0; i < idArray.size(); i++){
                count++;
                idArray.remove(count);
            }

            for(int i = 0; i < idArray.size(); i++){
                Movie movie = new Movie();
                movieId = idArray.get(i);
                movie.setId(movieId);
                poster = getPosterPathFromJson(movie.getId());
                movie.setPosterUrl(poster);
                posterUrlArray.add(movie.getPosterUrl());
            }

            posterArray = new String[posterUrlArray.size()];
            posterArray = posterUrlArray.toArray(posterArray);
        }
    }
}

