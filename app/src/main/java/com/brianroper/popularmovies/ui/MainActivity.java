package com.brianroper.popularmovies.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.brianroper.popularmovies.R;

public class MainActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.movie_frag_toolbar_title));

        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mPagerAdapter = new MoviePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        setTabLayout(mTabLayout, R.id.tab_layout, mViewPager, mPagerAdapter);

        activeNetworkCheck();

        if(findViewById(R.id.movie_detail_container) != null){

            mTwoPane = true;

            PlaceholderFragment placeholderFragment = new PlaceholderFragment();

            if(savedInstanceState == null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.movie_detail_container, placeholderFragment)
                        .commit();
            }
        }
        else{
            mTwoPane =false;
        }

       SharedPreferences screenState = PreferenceManager
               .getDefaultSharedPreferences(getApplicationContext());
        screenState.edit()
                .putBoolean("State", mTwoPane)
                .apply();
    }

    /*Checks to see if the device has an active network connection */
    public boolean activeNetworkCheck(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        return isConnected;
    }

    //populate tab layout using adapter
    public void setTabLayout(TabLayout tabLayout, int id, ViewPager viewPager,
                             PagerAdapter pagerAdapter){

        tabLayout = (TabLayout)findViewById(id);
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    /* Pager Adapter */
    private class MoviePagerAdapter extends FragmentStatePagerAdapter{

        public MoviePagerAdapter(FragmentManager fm){

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){

                case 0: {

                    return MovieFragment.newInstance(getString(R.string.pref_sort_popular), getApplicationContext());
                }

                case 1: {

                    return MovieFragment.newInstance(getString(R.string.pref_sort_rating), getApplicationContext());
                }

                case 2: {

                    return MovieFragment.newInstance(getString(R.string.pref_sort_favorites), getApplicationContext());
                }

                default: {

                    return MovieFragment.newInstance(getString(R.string.pref_sort_popular), getApplicationContext());
                }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch(position){

                case 0: {

                    return "popular";
                }

                case 1: {

                    return "top rated";
                }

                case 2: {

                    return "favorites";
                }
            }
            return "Tab " + position;
        }
    }
}

