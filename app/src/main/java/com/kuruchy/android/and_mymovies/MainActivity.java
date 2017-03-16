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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.kuruchy.android.and_mymovies.data.MoviesContract;
import com.kuruchy.android.and_mymovies.data.MoviesDBHelper;
import com.kuruchy.android.and_mymovies.sync.MoviesSyncUtils;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseNetworkUtils;

/**
 * Main Activity Class.
 *
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler {

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * movie data.
     */
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
            MoviesContract.MovieEntry.COLUMN_TRAILER_PATH
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
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
    private int NUMBER_OF_COLUMNS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TheMovieDatabaseNetworkUtils.setApi_key(getBaseContext().getString(R.string.mdb_id));

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);

        MoviesDBHelper dbHelper = new MoviesDBHelper(this);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            NUMBER_OF_COLUMNS = 6;
        }

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(this, NUMBER_OF_COLUMNS);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);

        mRecyclerView.setAdapter(mMovieAdapter);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIE_LOADER, null, this);

        MoviesSyncUtils.startImmediateSync(this);

        //loadFetchMovieData(TheMovieDatabaseNetworkUtils.SORTING_PARAM);



    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_FAVORITE_MOVIE_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri favoriteMovieQueryUri = MoviesContract.MovieEntry.CONTENT_FAVORITE_URI;
                /* Sort order: Ascending by date */
                String sortOrder = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " ASC";
                //Log.v("FAV", favoriteMovieQueryUri.toString());

                return new CursorLoader(this,
                        favoriteMovieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        sortOrder);

            case ID_POPULAR_MOVIE_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri popularMovieQueryUri = MoviesContract.MovieEntry.CONTENT_POPULAR_URI;
                //Log.v("MOS", popularMovieQueryUri.toString());

                return new CursorLoader(this,
                        popularMovieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            case ID_TOP_RATED_MOVIE_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri topRatedMovieQueryUri = MoviesContract.MovieEntry.CONTENT_TOP_RATED_URI;
                //Log.v("TOP", topRatedMovieQueryUri.toString());

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

    /**
     * Calls the Async Task that will fetch the movie data.
     *
     * @param sorting The sorting method to run the Async Task.
     */
    private void loadFetchMovieData(String sorting) {
        new FetchMovieData().execute(sorting);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        Log.v("DEBUG", movie.getTitle());
        intentToStartDetailActivity.putExtra("movie_obj", (Parcelable) movie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.sort_by_popular:
                TheMovieDatabaseNetworkUtils.SORTING_PARAM = TheMovieDatabaseNetworkUtils.POPULAR;
                mMovieAdapter = new MovieAdapter(this, this);
                mRecyclerView.setAdapter(mMovieAdapter);
                MoviesSyncUtils.startImmediateSync(this);
                getSupportLoaderManager().restartLoader(ID_POPULAR_MOVIE_LOADER, null, this);
                return true;

            case R.id.sort_by_top:
                TheMovieDatabaseNetworkUtils.SORTING_PARAM = TheMovieDatabaseNetworkUtils.TOP_RATED;
                mMovieAdapter = new MovieAdapter(this, this);
                mRecyclerView.setAdapter(mMovieAdapter);
                MoviesSyncUtils.startImmediateSync(this);
                getSupportLoaderManager().restartLoader(ID_TOP_RATED_MOVIE_LOADER, null, this);
                return true;

            case R.id.sort_by_favorite:
                TheMovieDatabaseNetworkUtils.SORTING_PARAM = TheMovieDatabaseNetworkUtils.FAVORITE;
                mMovieAdapter = new MovieAdapter(this, this);
                mRecyclerView.setAdapter(mMovieAdapter);
                getSupportLoaderManager().restartLoader(ID_FAVORITE_MOVIE_LOADER, null, this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public static MovieAdapter getmMovieAdapter() {
        return mMovieAdapter;
    }
}
