package com.brianroper.popularmovies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toolbar;

public class PlaceholderFragment extends Fragment {

    private ImageView mLogoImageView;
    private Toolbar mToolbar;

    public PlaceholderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_placeholder, container, false);

        mLogoImageView = (ImageView)v.findViewById(R.id.logo_image_view);
        mLogoImageView.setAlpha(0.4f);

        return v;
    }

}
