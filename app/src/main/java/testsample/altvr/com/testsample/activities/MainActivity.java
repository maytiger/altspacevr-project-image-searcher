package testsample.altvr.com.testsample.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.fragments.PhotosFragment;
import testsample.altvr.com.testsample.service.ApiService;
import testsample.altvr.com.testsample.util.LogUtil;

public class MainActivity extends AppCompatActivity {
    private LogUtil log = new LogUtil(MainActivity.class);
    private ApiService mService;
    SearchView mSearchView;
    PhotosFragment photosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mService = new ApiService(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        photosFragment = PhotosFragment.newInstance();
        displayFragment(photosFragment, R.string.toolbar_main_title);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mService.searchPhotos(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(queryTextListener);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_guess) {
            LinearLayout input = (LinearLayout)findViewById(R.id.ll_input);
            input.setVisibility(View.VISIBLE);
            photosFragment.guessWord();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            mSearchView.clearFocus();
            mService.searchPhotos(query);
            return true;
        }
    };

    private void displayFragment(Fragment fragment, int title) {
        setTitle(title);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }


}
