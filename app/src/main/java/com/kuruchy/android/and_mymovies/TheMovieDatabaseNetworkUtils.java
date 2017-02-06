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

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * The Movie Database Network Utils
 *
 * These utilities will be used to communicate with the movies database.
 * Basic utilities as build proper url and communicate via http.
 */
public final class TheMovieDatabaseNetworkUtils {

    private static final String W185 = "w185";
    private static final String W342 = "w342";
    private static final String W500 = "w500";

    private static final String QUALITY = W342;
    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String MOVIES_PICS_BASE_URL = "https://image.tmdb.org/t/p/" + QUALITY;

    private static final String language = "en-US";
    private static final String page = "1";

    private static String api_key;

    final static String QUERY_PARAM = "?";
    final static String API_KEY_PARAM = "api_key";
    final static String TOP_RATED = "top_rated";
    final static String POPULAR = "popular";
    static String SORTING_PARAM = TOP_RATED;
    final static String LANGUAGE_PARAM = "language";
    final static String PAGE_PARAM = "page";

    /**
     * Builds the URL used to talk to the movie server using a sorting method.
     *
     * @param sortingParam The sorting method
     * @return The URL to use to query the movies server.
     */
    public static URL buildUrl(String sortingParam) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + sortingParam + QUERY_PARAM).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, api_key)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the data from an HTTP petition.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The response data that is returned from the HTTP petition.
     * @throws IOException Problem with network and/or stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasNext = scanner.hasNext();
            if (hasNext) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            httpURLConnection.disconnect();
        }
    }

    public static void setApi_key(String api_key) {
        TheMovieDatabaseNetworkUtils.api_key = api_key;
    }
}