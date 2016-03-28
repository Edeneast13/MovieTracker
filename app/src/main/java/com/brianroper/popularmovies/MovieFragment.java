package com.brianroper.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieFragment extends Fragment{

    ArrayList<String> movieIdArray = new ArrayList<String>();
    ArrayList<String> posterUrlArray = new ArrayList<String>();
    String movieId = "";
    String poster = "";
    final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
    final String POSTER_SIZE_PARAM = "w370";
    final String POPULAR_MOVIES_PARAM = "movie";
    final String TOP_RATED_PARAM = "/top-rated";
    String sortParameter ="";
    int count = 0;
    private GridView mGridView;
    private String mKey = String.valueOf(R.string.api_key);
    final String KEY_PARAM = "?";

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){

        int id = menu.getItemId();

        if(id == R.id.action_settings){

            getMovieDataFromApi();
            return true;
        }
        return super.onOptionsItemSelected(menu);
    }

    public String getPosterPathFromJson(String movieId){

        String posterUrl ="";

        try {
            FetchMovieTask posterPathTask = new FetchMovieTask();
            String jsonData = posterPathTask
                    .execute("https://api.themoviedb.org/3/movie/" + movieId + KEY_PARAM + mKey)
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

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortPref = sharedPreferences.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_popular));

            if(sortPref.equals(getString(R.string.pref_sort_rating))){

                sortParameter = POPULAR_MOVIES_PARAM+TOP_RATED_PARAM;
            }
            else{

                sortParameter = POPULAR_MOVIES_PARAM;
            }

            //retrieves html data from themoviedb.org and sets it to the htmlData variable
            FetchMovieTask movieTask = new FetchMovieTask();
            String htmlData = movieTask.execute("https://www.themoviedb.org/"+sortParameter).get();

            //splits the webpage source code to ignore unnecessary code
            String[] splitHtmlData = htmlData.split("<div class=\"pagination\">");

            //picks out movie id's from web page source code
            Pattern idPattern = Pattern.compile("id=\"movie_(.*?)\"");
            Matcher idMatcher = idPattern.matcher(splitHtmlData[0]);

            while(idMatcher.find()){

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
        catch(InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }
        String[] postersArray = new String[posterUrlArray.size()];
        postersArray = posterUrlArray.toArray(postersArray);

        GridViewAdapter adapter = new GridViewAdapter(getActivity(), getId(), postersArray);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getActivity(), DetailActivity.class);
                String movie ="";

                movie = movieIdArray.get(position);
                i.putExtra("MOVIEID", movie);
                startActivity(i);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.movie_gridview, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridview);

        getMovieDataFromApi();
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
}

