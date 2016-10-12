package com.brianroper.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by brianroper on 10/11/16.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private Context context;
    private List<Movie> movies;
    private int rowLayout;

    private LayoutInflater inflater;

    public MovieAdapter(Context context, int rowLayout, List<Movie> movies){
        this.context = context;
        this.movies = movies;
        this.rowLayout = rowLayout;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.gridview_item, parent, false);
        return new MovieViewHolder(root);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_PARAM = "w370";

        String posterUrl = BASE_POSTER_URL + POSTER_SIZE_PARAM + movies.get(position).getPosterPath();

        Picasso.with(context)
                .load(posterUrl)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.movie_item);
        }
    }
}
