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

package com.kuruchy.android.and_mymovies.utilities;

import com.kuruchy.android.and_mymovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * The Movie Database Jason Utils
 * Utility functions to handle TheMovieDatabase JSON data.
 */
public final class TheMovieDatabaseJsonUtils {

    /**
     * This method parses JSON and returns an array of Movies
     *
     * @param movieListJSONString JSON string returned from server
     * @return Array of Movies
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Movie[] getMovieArrayFromJSONData(String movieListJSONString)
            throws JSONException, IOException {

        /* Movie information. Each movie info is an element of the "results" array */
        final String TMD_RESULTS = "results";
        final String TMD_POSTER_PATH = "poster_path";
        final String TMD_ADULT = "adult";
        final String TMD_OVERVIEW = "overview";
        final String TMD_RELEASE_DATE = "release_date";
        final String TMD_GENRE_IDS = "genre_ids";
        final String TMD_ID = "id";
        final String TMD_ORIGINAL_TITLE = "original_title";
        final String TMD_ORIGINAL_LANGUAGE = "original_language";
        final String TMD_TITLE = "title";
        final String TMD_BACKDROP_PATH = "backdrop_path";
        final String TMD_POPULARITY = "popularity";
        final String TMD_VOTE_COUNT = "vote_count";
        final String TMD_VIDEO = "video";
        final String TMD_VOTE_AVERAGE = "vote_average";

        Movie[] parsedMoviesData = null;

        JSONObject movieJson = new JSONObject(movieListJSONString);

        JSONArray movieArray = movieJson.getJSONArray(TMD_RESULTS);

        parsedMoviesData = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject movieObject = movieArray.getJSONObject(i);

            Movie movie = new Movie();

            movie.setPoster_path(TheMovieDatabaseNetworkUtils.MOVIES_PICS_BASE_URL + movieObject.getString(TMD_POSTER_PATH));
            movie.setAdult(movieObject.getBoolean(TMD_ADULT));
            movie.setOverview(movieObject.getString(TMD_OVERVIEW));
            movie.setRelease_date(movieObject.getString(TMD_RELEASE_DATE));
            //movie.setGenre_ids = (movieObject.getInt(TMD_GENRE_IDS));
            movie.setId(movieObject.getInt(TMD_ID));
            movie.setOriginal_title(movieObject.getString(TMD_ORIGINAL_TITLE));
            movie.setOriginal_language(movieObject.getString(TMD_ORIGINAL_LANGUAGE));
            movie.setTitle(movieObject.getString(TMD_TITLE));
            movie.setBackdrop_path(movieObject.getString(TMD_BACKDROP_PATH));
            movie.setPopularity(movieObject.getDouble(TMD_POPULARITY));
            movie.setVote_count(movieObject.getInt(TMD_VOTE_COUNT));
            movie.setVideo(movieObject.getBoolean(TMD_VIDEO));
            movie.setVote_average(movieObject.getDouble(TMD_VOTE_AVERAGE));

            parsedMoviesData[i] = movie;
        }

        return parsedMoviesData;
    }

    /**
     * This method parses JSON and returns an array of urls for each trailer
     *
     * @param movieTrailerListJSONString JSON string returned from server
     * @return Array of Trailers
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String[] getVideoInfoFromJSONData(String movieTrailerListJSONString)
            throws JSONException {

        final String TMD_RESULTS = "results";
        final String TMD_TRAILER_ID = "id";
        final String TMD_TRAILER_ISO_639_1 = "iso_639_1";
        final String TMD_TRAILER_ISO_3166_1 = "iso_3166_1";
        final String TMD_TRAILER_KEY = "key";
        final String TMD_TRAILER_NAME = "name";
        final String TMD_TRAILER_SITE = "site";
        final String TMD_TRAILER_SIZE = "size";
        final String TMD_TRAILER_TYPE = "type";

        String[] parsedTrailerData = null;

        JSONObject movieTrailerJson = new JSONObject(movieTrailerListJSONString);

        JSONArray movieTrailerArray = movieTrailerJson.getJSONArray(TMD_RESULTS);

        parsedTrailerData = new String[movieTrailerArray.length()];

        for (int i = 0; i < movieTrailerArray.length(); i++) {

            JSONObject movieObject = movieTrailerArray.getJSONObject(i);

            String movieTrailerURL = new String();

            movieTrailerURL += TheMovieDatabaseNetworkUtils.MOVIES_TRAILER_YOUTUBE_BASE_URL +
                    movieObject.getString(TMD_TRAILER_KEY);


            parsedTrailerData[i] = movieTrailerURL;
        }

        return parsedTrailerData;
    }

}