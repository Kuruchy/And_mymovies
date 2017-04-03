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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kuruchy.android.and_mymovies.data.MoviesContract;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseJsonUtils;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseNetworkUtils;

import java.net.URL;

/**
 * Fetch Extra Movie Data Class.
 *
 * Extends from AsyncTask. Allowing to run a movie list update on a background thread,
 * while publishing the results to the UI thread.
 */
public class FetchExtraMovieData extends AsyncTask<Object, Void, Movie> {

    Context context;

    @Override
    protected Movie doInBackground(Object... params) {

        if (params.length == 0) {
            return null;
        }

        Movie mMovie = (Movie) params[0];
        context = (Context) params[1];
        int movieId = mMovie.getId();

        URL movieReviewRequestURL = TheMovieDatabaseNetworkUtils.buildMovieReviewUrl(movieId);
        URL movieTrailerRequestURL = TheMovieDatabaseNetworkUtils.buildMovieTrailerUrl(movieId);

        try {
            String jsonMovieReviewResponse = TheMovieDatabaseNetworkUtils
                    .getResponseFromHttpUrl(movieReviewRequestURL);

            String jsonMovieTrailerResponse = TheMovieDatabaseNetworkUtils
                    .getResponseFromHttpUrl(movieTrailerRequestURL);

            String[] reviewsArray = TheMovieDatabaseJsonUtils
                    .getReviewInfoFromJSONData(jsonMovieReviewResponse);

            String[] trailerUrlArray = TheMovieDatabaseJsonUtils
                    .getVideoInfoFromJSONData(jsonMovieTrailerResponse);

            String trailerId = TheMovieDatabaseNetworkUtils.extractYoutubeId(trailerUrlArray[0]);

            mMovie.setTrailer_path(trailerUrlArray[0]);
            mMovie.setTrailer_thumbnail_path("http://img.youtube.com/vi/" + trailerId + "/0.jpg");
            mMovie.setReviews(reviewsArray[0]);

            return mMovie;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Movie mMovie) {
        if (mMovie != null) {

            Glide.with(context).load(mMovie.getTrailer_thumbnail_path()).centerCrop().into(DetailActivity.mTrailerThumbnail);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.MovieEntry.COLUMN_TRAILER_PATH, mMovie.getTrailer_path());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_TRAILER_THUMBNAIL_PATH, mMovie.getTrailer_thumbnail_path());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_REVIEWS, mMovie.getReviews());


            // Update the content values via a ContentResolver
            Uri uriUpdate = MoviesContract.MovieEntry.CONTENT_FAVORITE_URI.buildUpon().appendPath("" + mMovie.getId()).build();
            Log.d("", uriUpdate.toString());
            int num = context.getContentResolver().update(uriUpdate, contentValues, null, new String[]{ });
        }
    }
}