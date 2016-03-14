package com.brianroper.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieFragment extends Fragment{

    private ImageView iv;
    ArrayList<String> movieIdArray = new ArrayList<String>();
    ArrayList<String> posterUrlArray = new ArrayList<String>();
    String posterUrl = "";
    String movieId = "";

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.movie_gridview, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.gridview);

        try {

            final String POPULAR_MOVIES_PARAM = "movie";
            final String HIGHEST_RATED_PARAM = "top-rated";
            final String BASE_MOVIE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY_PARAM = "?api_key=a0a454fc960bf4f69fa0adf5e13161cf";
            String fullMovieUrl = BASE_MOVIE_URL+movieId+API_KEY_PARAM;

            //retrieves html data from themoviedb.org and sets it to the htmlData variable
            FetchMovieTask movieTask = new FetchMovieTask();
            String htmlData = movieTask.execute("https://www.themoviedb.org/"+POPULAR_MOVIES_PARAM).get();

            //splits the webpage source code to ignore unnecessary code
            String[] splitHtmlData = htmlData.split("<div class=\"pagination\">");

            /*To Do: Fix page source split to properly populate movie id array list without double id's */
            //picks out movie id's from web page source code
            Pattern idPattern = Pattern.compile("id=\"movie_(.*?)\"");
            Matcher idMatcher = idPattern.matcher(splitHtmlData[0]);

            while(idMatcher.find()){

                movieIdArray.add(idMatcher.group(1));
            }

            Pattern posterUrlPattern = Pattern.compile("srcset=\"(.*?)1x");
            Matcher posterUrlMatcher = posterUrlPattern.matcher(splitHtmlData[0]);

            //picks out movie poster urls from web page source code
            while(posterUrlMatcher.find()){

                posterUrlArray.add(posterUrlMatcher.group(1));
            }
            
            //creates new Movie objects that store movie id and poster url
            for (int i = 0; i < movieIdArray.size(); i++) {
                Movie movie = new Movie();
                movieId = movieIdArray.get(i);
                movie.setId(movieId);
                Log.i("Movie", "Id: " + movie.getId().toString());
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

                String selectedItem = parent.getItemAtPosition(position).toString();

                Intent i = new Intent(getActivity(), DetailActivity.class);
                int arrayPosition = 0;
                String movie ="";

                if(position == 0){
                    movie = movieIdArray.get(0);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 1){
                    movie = movieIdArray.get(2);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 2){
                    movie = movieIdArray.get(4);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 3){
                    movie = movieIdArray.get(6);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 4){
                    movie = movieIdArray.get(8);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 5){
                    movie = movieIdArray.get(10);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 6){
                    movie = movieIdArray.get(12);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 7){
                    movie = movieIdArray.get(14);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 8){
                    movie = movieIdArray.get(16);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 9){
                    movie = movieIdArray.get(18);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 10){
                    movie = movieIdArray.get(20);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 11){
                    movie = movieIdArray.get(22);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 12){
                    movie = movieIdArray.get(24);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 13){
                    movie = movieIdArray.get(26);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 14){
                    movie = movieIdArray.get(28);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 15){
                    movie = movieIdArray.get(30);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 16){
                    movie = movieIdArray.get(32);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 17){
                    movie = movieIdArray.get(34);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 18){
                    movie = movieIdArray.get(36);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 19){
                    movie = movieIdArray.get(38);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
                else if(position == 20){
                    movie = movieIdArray.get(40);
                    i.putExtra("MOVIEID", movie);
                    startActivity(i);
                }
            }
        });
        return v;
    }

    //Custom Array Adapter
    public class GridViewAdapter extends ArrayAdapter{

        private Context context;
        private LayoutInflater inflator;
        private int id;
        private String[] imageURls;

        GridViewAdapter(Context context, int id, String[] imageUrls){

            super(context, R.layout.movie_gridview, imageUrls);

            this.context = context;
            this.id = id;
            this.imageURls = imageUrls;

            inflator = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if(convertView == null){

                convertView = inflator.inflate(R.layout.gridview_item, parent, false);
            }

            Picasso.with(context).load(imageURls[position]).fit().into((ImageView) convertView);

            return convertView;
        }
    }
}

