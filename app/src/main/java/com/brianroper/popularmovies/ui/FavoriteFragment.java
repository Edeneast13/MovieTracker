package com.brianroper.popularmovies.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.adapter.FavoriteAdapter;
import com.brianroper.popularmovies.model.Favorite;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Favorite> mFavorites;
    private Realm mRealm;

    public FavoriteFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.favorite_recycler, container, false);

        initializeViews(root);
        initializeRealm();

        updateUI();

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
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
        mRealm.init(getActivity());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();

        mRealm = Realm.getInstance(realmConfiguration);
    }
    public void updateUI(){
        RealmResults<Favorite> results = mRealm.where(Favorite.class).findAll();

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setAdapter(new FavoriteAdapter(getActivity(),
                R.layout.favorite_recycler, results));
    }
}
