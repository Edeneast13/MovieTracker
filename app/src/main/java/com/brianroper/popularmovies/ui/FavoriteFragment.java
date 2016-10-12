package com.brianroper.popularmovies.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.adapter.FavoriteAdapter;
import com.brianroper.popularmovies.model.Favorite;
import com.brianroper.popularmovies.model.Movie;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private Realm mRealm;
    RealmResults<Favorite> mFavorites;
    private RecyclerView mRecyclerView;

    public FavoriteFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.favorite_recycler, container, false);

        initializeViews(root);
        initializeRealm();

        returnFavoritesFromRealm();

        updateUI();

        return root;
    }

    /**
     * initialize views of the activity/fragment
     */
    public void initializeViews(View root){
        mRecyclerView = (RecyclerView) root.findViewById(R.id.favorite_recycler);
    }

    /**
     * initialize realm for this thread
     */
    public void initializeRealm(){
        Realm.init(getActivity());

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        mRealm = Realm.getInstance(config);
    }

    public void returnFavoritesFromRealm(){
        mFavorites = mRealm.where(Favorite.class).findAll();
    }

    public void updateUI(){
        mRecyclerView.setAdapter(new FavoriteAdapter(getActivity(), R.layout.movie_recycler, mFavorites));
    }
}
