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
package com.kuruchy.android.and_mymovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.kuruchy.android.and_mymovies.DetailActivity;
import com.kuruchy.android.and_mymovies.Movie;
import com.kuruchy.android.and_mymovies.data.MoviesContract;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseJsonUtils;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseNetworkUtils;

import java.net.URL;

public class MoviesSyncTask {

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncMovieData(Context context, String sortingParam) {

        try {

            URL movieRequestURL = TheMovieDatabaseNetworkUtils.buildMovieUrl(sortingParam);

            Uri mUri;

            switch (sortingParam){
                case TheMovieDatabaseNetworkUtils.FAVORITE:
                    mUri = MoviesContract.MovieEntry.CONTENT_FAVORITE_URI;
                    break;
                case TheMovieDatabaseNetworkUtils.POPULAR:
                    mUri = MoviesContract.MovieEntry.CONTENT_POPULAR_URI;
                    break;
                case TheMovieDatabaseNetworkUtils.TOP_RATED:
                    mUri = MoviesContract.MovieEntry.CONTENT_TOP_RATED_URI;
                    break;
                default:
                    mUri = null;
                    break;
            }

            String jsonMovieResponse = TheMovieDatabaseNetworkUtils
                    .getResponseFromHttpUrl(movieRequestURL);

            Movie[] movieDataArray = TheMovieDatabaseJsonUtils
                    .getMovieArrayFromJSONData(jsonMovieResponse);

            ContentValues[] moviesValues = new ContentValues[movieDataArray.length];

            int i =0;
            for (Movie mMovie : movieDataArray){

                ContentValues contentValues = new ContentValues();

                // Put the movie id and sinopsys into the ContentValues
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle().toString());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ORG_TITLE, mMovie.getOriginal_title().toString());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPoster_path().toString());
                //contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PIC, mMovie.get());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, mMovie.getOverview().toString());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_USER_RATING, 5.0);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_GLOBAL_RATING, mMovie.getVote_average());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getRelease_date().toString());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_TRAILER_PATH, "www.youtube.com");

                moviesValues[i] = contentValues;
                i++;
            }

            /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null. We need to check for those cases here to prevent any
             * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
             * there isn't any to insert.
             */
            if (moviesValues != null && moviesValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver movieContentResolver = context.getContentResolver();

                /* Delete old data because we don't need to keep multiple data */
                movieContentResolver.delete(
                        mUri,
                        null,
                        null);

                /* Insert our new weather data into Sunshine's ContentProvider */
                movieContentResolver.bulkInsert(
                        mUri,
                        moviesValues);
            }

            /* If the code reaches this point, we have successfully performed our sync */

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }

}