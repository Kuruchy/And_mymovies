/*
 * MIT License
 *
 * Copyright (c) 2017.  Bruno Retolaza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.kuruchy.android.and_mymovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.kuruchy.android.and_mymovies.data.MoviesContract;
import com.kuruchy.android.and_mymovies.sync.MoviesSyncUtils;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseNetworkUtils;

/**
 * Main Activity Class.
 *
 * Implements LoadManager and MovieAdapterOnClickHandler
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler,
        NavigationView.OnNavigationItemSelectedListener {

    // A constant to save and restore the sorting method that is being used
    private static final String SORTING_PARAM_EXTRA = "sorting_param";

    // The columns of data that we are interested in displaying within our MainActivity's list of
    // movie data.
    public static final String[] MAIN_MOVIE_PROJECTION = {
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_SYNOPSIS,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ORG_TITLE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_USER_RATING,
            MoviesContract.MovieEntry.COLUMN_GLOBAL_RATING,
            MoviesContract.MovieEntry.COLUMN_GLOBAL_RATING,
            MoviesContract.MovieEntry.COLUMN_TRAILER_PATH,
            MoviesContract.MovieEntry.COLUMN_TRAILER_THUMBNAIL_PATH,
            MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MoviesContract.MovieEntry.COLUMN_REVIEWS
    };

    // The indices of the values are stored in the array of Strings above to more quickly be able to
    // access the data from our query.
    public static final int INDEX_MOVIE_PATH = 0;
    public static final int INDEX_SYNOPSIS = 1;
    public static final int INDEX_RELEASE_DATE = 2;
    public static final int INDEX_MOVIE_ID = 3;
    public static final int INDEX_MOVIE_TITLE = 4;

    private static final int ID_FAVORITE_MOVIE_LOADER = 144;
    private static final int ID_TOP_RATED_MOVIE_LOADER = 143;
    private static final int ID_POPULAR_MOVIE_LOADER = 142;

    private RecyclerView mRecyclerView;
    private static MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TheMovieDatabaseNetworkUtils.setApi_key(getBaseContext().getString(R.string.mdb_id));

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(this, calculateNoOfColumns(this));

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);

        mRecyclerView.setAdapter(mMovieAdapter);

        // Menus and Toolbars

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // If a savedInstanceState exist load the sorting param from there, if not use top rated
        if (savedInstanceState != null) {
            int sortingParam = savedInstanceState.getInt(SORTING_PARAM_EXTRA);

            // Creates a Loader if one doesn't already exist, and starts it. Otherwise
            // the last created loader is re-used.
            getSupportLoaderManager().initLoader(sortingParam, null, this);
        }else {
            getSupportLoaderManager().initLoader(ID_TOP_RATED_MOVIE_LOADER, null, this);
        }

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);

        // Starts the Sync task MoviesSyncIntentService
        MoviesSyncUtils.startImmediateSync(this);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()
        Toast.makeText(getBaseContext(), "Loading More " + offset, Toast.LENGTH_LONG).show();

    }

    // Returns the number of columns due to the display
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 100;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns>=2?noOfColumns:2;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_FAVORITE_MOVIE_LOADER:
                // URI for all favorite rows of movies data in our movie table
                Uri favoriteMovieQueryUri = MoviesContract.MovieEntry.CONTENT_FAVORITE_URI;

                // Sorting by id, ascendant
                String sortOrder = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " ASC";

                return new CursorLoader(this,
                        favoriteMovieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        sortOrder);

            case ID_POPULAR_MOVIE_LOADER:
                // URI for all popular rows of movies data in our movie table
                Uri popularMovieQueryUri = MoviesContract.MovieEntry.CONTENT_POPULAR_URI;

                return new CursorLoader(this,
                        popularMovieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            case ID_TOP_RATED_MOVIE_LOADER:
                // URI for top rated rows of movies data in our movie table
                Uri topRatedMovieQueryUri = MoviesContract.MovieEntry.CONTENT_TOP_RATED_URI;

                return new CursorLoader(this,
                        topRatedMovieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie_obj", movie);
        startActivity(intentToStartDetailActivity);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int sortingParam;
        if(TheMovieDatabaseNetworkUtils.SORTING_PARAM == TheMovieDatabaseNetworkUtils.FAVORITE){
            sortingParam = ID_FAVORITE_MOVIE_LOADER;
        }else if (TheMovieDatabaseNetworkUtils.SORTING_PARAM == TheMovieDatabaseNetworkUtils.TOP_RATED){
            sortingParam = ID_TOP_RATED_MOVIE_LOADER;
        }else {
            sortingParam = ID_POPULAR_MOVIE_LOADER;
        }

        outState.putInt(SORTING_PARAM_EXTRA, sortingParam);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {
            TheMovieDatabaseNetworkUtils.SORTING_PARAM = TheMovieDatabaseNetworkUtils.FAVORITE;
            mMovieAdapter = new MovieAdapter(this, this);
            mRecyclerView.setAdapter(mMovieAdapter);
            getSupportLoaderManager().restartLoader(ID_FAVORITE_MOVIE_LOADER, null, this);
        } else if (id == R.id.nav_top_rated) {
            TheMovieDatabaseNetworkUtils.SORTING_PARAM = TheMovieDatabaseNetworkUtils.TOP_RATED;
            mMovieAdapter = new MovieAdapter(this, this);
            mRecyclerView.setAdapter(mMovieAdapter);
            MoviesSyncUtils.startImmediateSync(this);
            getSupportLoaderManager().restartLoader(ID_TOP_RATED_MOVIE_LOADER, null, this);
        } else if (id == R.id.nav_most_popular) {
            TheMovieDatabaseNetworkUtils.SORTING_PARAM = TheMovieDatabaseNetworkUtils.POPULAR;
            mMovieAdapter = new MovieAdapter(this, this);
            mRecyclerView.setAdapter(mMovieAdapter);
            MoviesSyncUtils.startImmediateSync(this);
            getSupportLoaderManager().restartLoader(ID_POPULAR_MOVIE_LOADER, null, this);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
