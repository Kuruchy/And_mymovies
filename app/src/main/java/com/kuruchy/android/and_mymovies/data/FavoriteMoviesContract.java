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

/*
* FavoriteMoviesContract
*
* Contract Class for defining the Database structure.
*
*/
public class FavoriteMoviesContract{

	// Content Authority
	public static final String CONTENT_AUTHORITY = "com.kuruchy.android.mymovies.app";

	// Base URI for accessing the database
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


	public static final class FavoriteMovieEntry implements BaseColumns{
		
		// Table name
		public static final String TABLE_FAVORITE_MOVIES = "favorite_movies";
		
		// ID column is alsways added
		public static final String _ID = "_id";		
		
		// Columns within the database table
		public static final String COLUMN_MOVIE_TITLE     = "movie_title";
		public static final String COLUMN_MOVIE_ORG_TITLE = "movie_org_title";
		public static final String COLUMN_MOVIE_ID        = "movie_id";
		public static final String COLUMN_POSTER_PATH     = "poster_path";
		public static final String COLUMN_POSTER_PIC      = "poster_pic";
		public static final String COLUMN_SYNOPSIS        = "synopsis";
		public static final String COLUMN_USER_RATING     = "user_rating";
		public static final String COLUMN_GLOBAL_RATING   = "global_rating";
		public static final String COLUMN_RELEASE_DATE    = "release_date";

		// Create content uri
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(TABLE_FAVORITE_MOVIES).build();

		// Create cursor of base type directory for multiple entries
		public static final String CONTENT_DIR_TYPE =
		ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITE_MOVIES;

		// Create cursor of base type item for single entry
		public static final String CONTENT_ITEM_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITE_MOVIES;

		// For building URIs on insertion
		public static Uri buildFavoriteMoviesUri(long id){
        		return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}