package com.brianroper.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.model.Favorite;
import com.brianroper.popularmovies.ui.DetailActivity;
import com.brianroper.popularmovies.util.DbBitmapUtil;

import java.util.List;

/**
 * Created by brianroper on 10/12/16.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>{

    private Context context;
    private List<Favorite> movies;
    private int rowLayout;

    private LayoutInflater inflater;

    public FavoriteAdapter(Context context, int rowLayout, List<Favorite> movies){
        this.context = context;
        this.movies = movies;
        this.rowLayout = rowLayout;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.gridview_item, parent, false);

        final FavoriteViewHolder favoriteViewHolder = new FavoriteViewHolder(root);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = favoriteViewHolder.getAdapterPosition();
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detailIntent.putExtra("id", movies.get(position).getId());
                context.startActivity(detailIntent);
            }
        });
        return favoriteViewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        holder.mImageView
                .setImageBitmap(
                        DbBitmapUtil.convertByteArrayToBitmap(
                                movies.get(position).getPoster()));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.movie_item);
        }
    }
}
