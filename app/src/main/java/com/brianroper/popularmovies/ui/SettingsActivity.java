package com.brianroper.popularmovies.ui;


import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            getFragmentManager().beginTransaction()
                    .addToBackStack("settings")
                    .replace(android.R.id.content, new MyPreferenceFragment())
                    .commit();
    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() >0){

            finish();
            getFragmentManager().popBackStack();
        }
        else{
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
