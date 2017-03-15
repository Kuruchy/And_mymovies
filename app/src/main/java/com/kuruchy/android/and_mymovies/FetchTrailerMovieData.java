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

import android.os.AsyncTask;
import android.util.Log;

import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseJsonUtils;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseNetworkUtils;

import java.net.URL;

/**
 * Fetch Trailer Movie Data Class.
 *
 * Extends from AsyncTask. Allowing to run a the movie list update on a background thread,
 * while publishing the results to the UI thread.
 */
public class FetchTrailerMovieData extends AsyncTask<Integer, Void, String> {

    @Override
    protected String doInBackground(Integer... params) {

        if (params.length == 0) {
            return null;
        }

        URL movieRequestURL = TheMovieDatabaseNetworkUtils.buildMovieTrailerUrl(params[0]);

        try {
            String jsonMovieTrailerResponse = TheMovieDatabaseNetworkUtils
                    .getResponseFromHttpUrl(movieRequestURL);

            String[] trailerUrlArray = TheMovieDatabaseJsonUtils.getVideoInfoFromJSONData(jsonMovieTrailerResponse);

            Log.v("TEST: ", trailerUrlArray[0]);



            return trailerUrlArray[0];

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String movieData) {
        if (movieData != null) {
            DetailActivity.setmTrailer(movieData);
        }
    }
}