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

package com.kuruchy.android.and_mymovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.kuruchy.android.and_mymovies.Movie;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseNetworkUtils;

/*
* MoviesContract
*
* Contract Class for defining the Database structure.
*
*/
public class MoviesContract {

        // The authority, which is how your code knows which Content Provider to access
        public static final String AUTHORITY = "com.kuruchy.android.and_mymovies";

        // The base content URI = "content://" + <authority>
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        // Define the possible paths for accessing data in this contract
        // This is the path for all the movues directory
        public static final String PATH_FAVORITE_MOVIES = TheMovieDatabaseNetworkUtils.FAVORITE;
        public static final String PATH_TOP_RATED_MOVIES = TheMovieDatabaseNetworkUtils.TOP_RATED;
        public static final String PATH_POPULAR_MOVIES = TheMovieDatabaseNetworkUtils.POPULAR;

        public static final class MovieEntry implements BaseColumns {

            // Movies content URI = base content URI + path
            public static final Uri CONTENT_FAVORITE_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();
            public static final Uri CONTENT_TOP_RATED_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED_MOVIES).build();
            public static final Uri CONTENT_POPULAR_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR_MOVIES).build();

            // Tables and column names
            public static final String FAVORITE_TABLE_NAME     = TheMovieDatabaseNetworkUtils.FAVORITE;
            public static final String TOP_RATED_TABLE_NAME    = TheMovieDatabaseNetworkUtils.TOP_RATED;
            public static final String MOST_POPULAR_TABLE_NAME = TheMovieDatabaseNetworkUtils.POPULAR;

            public static final String[] TABLE_NAMES = {FAVORITE_TABLE_NAME, TOP_RATED_TABLE_NAME, MOST_POPULAR_TABLE_NAME};

            // Since TaskEntry implements the interface "BaseColumns", it has an automatically produced
            // "_ID" column in addition to the two below
            // Columns within the database table
            public static final String COLUMN_MOVIE_TITLE      = "movie_title";
            public static final String COLUMN_MOVIE_ORG_TITLE  = "movie_org_title";
            public static final String COLUMN_MOVIE_ID         = "movie_id";
            public static final String COLUMN_POSTER_PATH      = "poster_path";
            //public static final String COLUMN_POSTER_PIC     = "poster_pic";
            public static final String COLUMN_SYNOPSIS         = "synopsis";
            public static final String COLUMN_USER_RATING      = "user_rating";
            public static final String COLUMN_GLOBAL_RATING    = "global_rating";
            public static final String COLUMN_RELEASE_DATE     = "release_date";
            public static final String COLUMN_TRAILER_PATH     = "trailer_path";
        }
}