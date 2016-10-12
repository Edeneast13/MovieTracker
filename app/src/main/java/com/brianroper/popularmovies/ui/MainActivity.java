package com.brianroper.popularmovies.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.brianroper.popularmovies.adapter.MovieAdapter;
import com.brianroper.popularmovies.model.Movie;
import com.brianroper.popularmovies.model.MovieResponse;
import com.brianroper.popularmovies.R;
import com.brianroper.popularmovies.rest.ApiClient;
import com.brianroper.popularmovies.rest.ApiInterface;
import com.brianroper.popularmovies.util.DbUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SharedPreferences mSharedPreferences;
    private Toolbar mToolbar;

    /**
     * Life Cycle methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        handleToolbarBehavior(mToolbar);

        updateUI();
    }

    /**
     * initialize all views of the activity
     */
    public void initializeViews(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
    }

    /**
     * handles the behavior of the toolbar
     */
    public void handleToolbarBehavior(Toolbar toolbar){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.movie_frag_toolbar_title));
    }

    /**
     * populates tab layout using view pager
     */
    public void setTabLayout(TabLayout tabLayout, int id, ViewPager viewPager,
                             PagerAdapter pagerAdapter){
        tabLayout = (TabLayout)findViewById(id);
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * updates current ui elements
     */
    public void updateUI(){
        mPagerAdapter = new MoviePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        setTabLayout(mTabLayout, R.id.tab_layout, mViewPager, mPagerAdapter);
    }

    /**
     * retrieves shared preferences
     */
    public void getSharedPreferences(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    /**
     * pager adapter for main view
     */
    private class MoviePagerAdapter extends FragmentStatePagerAdapter{

        public MoviePagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:{
                    return new PopularFragment();
                }
                case 1:{
                    return new RatedFragment();
                }
                case 2:{
                    return new FavoriteFragment();
                }
            }
            return new PopularFragment();
        }

        /**
         * if the user has not selected any favorites only two tabs in the layout are displayed
         */
        @Override
        public int getCount() {
            if(DbUtil.activeDb(getApplicationContext()) == true){
                return 3;
            }
            else{
                return 2;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0: {
                    return getString(R.string.main_tab_popular_title);
                }
                case 1: {
                    return getString(R.string.main_tab_rating_title);
                }
                case 2: {
                    return getString(R.string.main_tab_favorite_title);
                }
            }
            return "Tab " + position;
        }
    }
}

