package com.brianroper.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.model.Movie;
import com.brianroper.popularmovies.ui.DetailActivity;
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

        final MovieViewHolder movieViewHolder = new MovieViewHolder(root);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = movieViewHolder.getAdapterPosition();
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detailIntent.putExtra("id", movies.get(position).getId());
                context.startActivity(detailIntent);
            }
        });
        return movieViewHolder;
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
