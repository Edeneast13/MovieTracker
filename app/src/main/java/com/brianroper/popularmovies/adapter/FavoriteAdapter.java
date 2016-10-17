package com.brianroper.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.model.Favorite;
import com.brianroper.popularmovies.ui.DetailActivity;
import com.brianroper.popularmovies.util.Util;
import com.squareup.picasso.Picasso;

import io.realm.RealmResults;

/**
 * Created by brianroper on 10/12/16.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>{

    private Context context;
    private RealmResults<Favorite> favorites;
    private int rowLayout;
    private int mPosition;

    private LayoutInflater inflater;

    public FavoriteAdapter(Context context, int rowLayout, RealmResults<Favorite> favorites){
        this.context = context;
        this.favorites = favorites;
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
                detailIntent.putExtra("id", favorites.get(position).getId());
                context.startActivity(detailIntent);
            }
        });
        return favoriteViewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {

        final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_PARAM = "w370";

        if(Util.activeNetworkCheck(context) == true){
            Picasso.with(context)
                    .load(BASE_POSTER_URL + POSTER_SIZE_PARAM + favorites.get(position).posterPath)
                    .into(holder.mImageView);
        }
        else{
            holder.mImageView
                    .setImageBitmap(Util.convertByteArrayToBitmap(favorites.get(position).getPoster()));

            holder.mImageView.getLayoutParams().width = Util.returnScreenWidth(context);
        }
    }

    @Override
    public int getItemCount() {
        Log.i("favorites size: ", favorites.size() + "");
        return favorites.size();
    }

    @Override
    public long getItemId(int position) {
        mPosition = position;
        return super.getItemId(position);
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.movie_item);
        }
    }
}
