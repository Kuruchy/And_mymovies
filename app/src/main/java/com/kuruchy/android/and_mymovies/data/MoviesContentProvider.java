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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/*
* MoviesContentProvider
*
* Content Provider Class for accessing the Database.
*
*/
public class MoviesContentProvider extends ContentProvider{

	private static final String LOG_TAG = MoviesContentProvider.class.getSimpleName();
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private MoviesDBHelper mFavoriteMoviesDBHelper;

	// Codes for the UriMatcher
	private static final int FAVORITE_MOVIES     = 100;
	private static final int TOP_RATED_MOVIES    = 200;
	private static final int MOST_POPULAR_MOVIES = 300;
	private static final int FAVORITE_MOVIE_WITH_ID = 101;

	private static UriMatcher buildUriMatcher(){

		// Building an UriMatcher eith NO_MATCH as the code
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		// Adding an UriMatcher for each type of URI needed
		matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TOP_RATED_MOVIES, TOP_RATED_MOVIES);
        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_POPULAR_MOVIES, MOST_POPULAR_MOVIES);

		matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_FAVORITE_MOVIES + "/#", FAVORITE_MOVIE_WITH_ID);

		return matcher;	
	}

	@Override
	public boolean onCreate(){
		mFavoriteMoviesDBHelper = new MoviesDBHelper(getContext());

		return true;
	}

	@Override
	public String getType(@NonNull Uri uri){
		int match = sUriMatcher.match(uri);

		switch (match) {
			case FAVORITE_MOVIES:
				// directory
				return "vnd.android.cursor.dir" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_FAVORITE_MOVIES;
			case FAVORITE_MOVIE_WITH_ID:
				// single item type
				return "vnd.android.cursor.item" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_FAVORITE_MOVIES;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

		// Get access to underlying database (read-only for query)
		final SQLiteDatabase db = mFavoriteMoviesDBHelper.getReadableDatabase();

		// Write URI match code and set a variable to return a Cursor
		int match = sUriMatcher.match(uri);
		Cursor retCursor;

		// Query for the tasks directory and write a default case
		switch (match) {
			// Query for the tasks directory
			case FAVORITE_MOVIES:
				retCursor =  db.query(MoviesContract.MovieEntry.FAVORITE_TABLE_NAME,
						projection,
                        selection,
						selectionArgs,
						null,
						null,
						sortOrder);
				break;
            case TOP_RATED_MOVIES:
                retCursor =  db.query(MoviesContract.MovieEntry.TOP_RATED_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOST_POPULAR_MOVIES:
                retCursor =  db.query(MoviesContract.MovieEntry.MOST_POPULAR_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
			// Default exception
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		// Set a notification URI on the Cursor and return that Cursor
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);

		// Return the desired Cursor
		return retCursor;
	}

	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values){

        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = mFavoriteMoviesDBHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        long id;

        switch (match) {
            case FAVORITE_MOVIES:
                // Insert new values into the database
                // Inserting values into tasks table
                id = db.insert(MoviesContract.MovieEntry.FAVORITE_TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_FAVORITE_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case TOP_RATED_MOVIES:
                // Insert new values into the database
                // Inserting values into tasks table
                id = db.insert(MoviesContract.MovieEntry.TOP_RATED_TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_TOP_RATED_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case MOST_POPULAR_MOVIES:
                // Insert new values into the database
                // Inserting values into tasks table
                id = db.insert(MoviesContract.MovieEntry.MOST_POPULAR_TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_POPULAR_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs){

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mFavoriteMoviesDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case FAVORITE_MOVIES:
                tasksDeleted = db.delete(MoviesContract.MovieEntry.FAVORITE_TABLE_NAME, null, null);
                break;
            case TOP_RATED_MOVIES:
                tasksDeleted = db.delete(MoviesContract.MovieEntry.TOP_RATED_TABLE_NAME, null, null);
                break;
            case MOST_POPULAR_MOVIES:
                tasksDeleted = db.delete(MoviesContract.MovieEntry.MOST_POPULAR_TABLE_NAME, null, null);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(MoviesContract.MovieEntry.FAVORITE_TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        //Keep track of if an update occurs
        int tasksUpdated;

        // match code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITE_MOVIE_WITH_ID:
                //update a single task by getting the id
                String id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = mFavoriteMoviesDBHelper.getWritableDatabase().update(MoviesContract.MovieEntry.FAVORITE_TABLE_NAME, contentValues, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
	}

}