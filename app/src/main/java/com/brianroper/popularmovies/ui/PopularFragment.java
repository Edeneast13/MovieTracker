package com.brianroper.popularmovies.ui;

import android.app.ActivityOptions;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.adapter.MovieAdapter;

import com.brianroper.popularmovies.model.Movie;
import com.brianroper.popularmovies.model.MovieResponse;
import com.brianroper.popularmovies.rest.ApiClient;
import com.brianroper.popularmovies.rest.ApiInterface;
import com.brianroper.popularmovies.util.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private Bundle mBundle;
    private String mKey;

    /**
     * constructor
     */
    public PopularFragment() {}

    /**
     * Life Cycle methods
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.movie_recycler, container, false);

        initializeViews(root);

        returnApiKey();
        handleAnimationTransitions();

        apiRequestPopular();

        return root;
    }

    /**
     * initialize all the views of the fragment
     */
    public void initializeViews(View root){
        mRecyclerView = (RecyclerView)root.findViewById(R.id.movie_recycler);
    }

    /**
     * handles the transitions of the fragment/activity
     */
    public void handleAnimationTransitions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mBundle = ActivityOptions.makeSceneTransitionAnimation(getActivity())
                    .toBundle();
        }
    }

    /**
     * retrieve api key
     */
    public void returnApiKey(){
        mKey = getString(R.string.api_key);
    }

    /**
     * returns 20 most popular movies from api
     */
    public void apiRequestPopular(){
        final String API_KEY = getString(R.string.api_key);

        if(API_KEY.isEmpty()){
            Toast.makeText(getActivity(),
                    "Please get your api key from themobiedb.org first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //test for network activity before making api call
        if(Util.activeNetworkCheck(getActivity()) == true){
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<MovieResponse> call = apiService.getPopular(API_KEY);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    mRecyclerView.setAdapter(new MovieAdapter(getActivity(),R.layout.movie_recycler, movies));
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.e("Response Error: ", t.toString());
                }
            });
        }else{Util.noNetworkMessage(getActivity());}
    }
}

