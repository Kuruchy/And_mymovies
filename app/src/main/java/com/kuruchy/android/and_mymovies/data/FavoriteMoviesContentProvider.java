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
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/*
* FavoriteMoviesContentProvider
*
* Content Provider Class for accessing the Database.
*
*/
public class FavoriteMoviesContentProvider extends ContentProvider{

	private static final String LOG_TAG = FavoriteMoviesContentProvider.class.getSimpleName();
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private FavoriteMoviesDBHelper mOpenHelper;

	// Codes for the UriMatcher
	private static final int FAVORITE_MOVIE = 100;
	private static final int FAVORITE_MOVIE_WITH_ID = 101;

	private static UriMatcher buildUriMatcher(){

		// Building an UriMatcher eith NO_MATCH as the code
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;

		// Adding an UriMatcher for each type of URI needed
		matcher.addURI(authority, FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES, FAVORITE_MOVIE);
		matcher.addURI(authority, FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES + "/#", FAVORITE_MOVIE_WITH_ID);

		return matcher;	
	}

	@Override
	public boolean onCreate(){
		mOpenHelper = new FavoriteMoviesDBHelper(getContext());

		return true;
	}

	@Override
	public String getType(@NonNull Uri uri){

		final int match = sUriMatcher.match(uri);

		switch (match){
			case FAVORITE_MOVIE:
				return FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_DIR_TYPE;

			case FAVORITE_MOVIE_WITH_ID:
				return FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

		Cursor retCursor;

		switch(sUriMatcher.match(uri)){

			// All Favorite Movies selected
			case FAVORITE_MOVIE:  
				retCursor = mOpenHelper.getReadableDatabase().query(
						FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder);
				return retCursor;

			// Individual favorite movie based on Id selected
			case FAVORITE_MOVIE_WITH_ID:
				retCursor = mOpenHelper.getReadableDatabase().query(
						FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
						projection,
						FavoriteMoviesContract.FavoriteMovieEntry._ID + " = ?",
						new String[] {String.valueOf(ContentUris.parseId(uri))},
						null,
						null,
						sortOrder);
				return retCursor;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values){

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Uri returnUri;

		switch (sUriMatcher.match(uri)) {

			case FAVORITE_MOVIE:
				long _id = db.insert(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES, null, values);
				// Insert unless it is already contained in the database
				if (_id > 0) {
					returnUri = FavoriteMoviesContract.FavoriteMovieEntry.buildFavoriteMoviesUri(_id);
				} else {
					throw new android.database.SQLException("Failed to insert row into: " + uri);
				}
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		// Notify a change in the Content Resolver 
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs){

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int numDeleted;

		switch(match){

			case FAVORITE_MOVIE:
				// Delete command, it return the number of items deleted.
				numDeleted = db.delete(
						FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES, selection, selectionArgs);

				// Reset the _ID
				db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
						FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES + "'");
				break;

			case FAVORITE_MOVIE_WITH_ID:
				// Delete command, it return the number of items deleted.
				numDeleted = db.delete(
						FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
						FavoriteMoviesContract.FavoriteMovieEntry._ID + " = ?",
						new String[]{String.valueOf(ContentUris.parseId(uri))});

				// Reset the _ID
				db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + 
						FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES + "'");

				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		return numDeleted;
	}

	@Override
	public int bulkInsert(@NonNull Uri uri, ContentValues[] values){

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);

		switch(match){

			case FAVORITE_MOVIE:
				// Begin  registering the transactions
				db.beginTransaction();

				// Keep track of successful inserts
				int numInserted = 0;
				try{
					for(ContentValues value : values){
						if (value == null){
							throw new IllegalArgumentException("Cannot have null content values");
						}
						long _id = -1;
						try{
							_id = db.insertOrThrow(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
									null, value);
						}catch(SQLiteConstraintException e) {
							Log.w(LOG_TAG, "Attempting to insert " +
									value.getAsString(
											FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE)
									+ " but value is already in database.");
						}
						if (_id != -1){
							numInserted++;
						}
					}
					if(numInserted > 0){
						// If no errors, declare a successful transaction.
						// database will not populate if this is not called
						db.setTransactionSuccessful();
					}
				} finally {
					// All transactions occur at once
					db.endTransaction();
				}
				if (numInserted > 0){
					// If there was successful insertion, notify the Content Resolver
					getContext().getContentResolver().notifyChange(uri, null);
				}
				return numInserted;

			default:
				return super.bulkInsert(uri, values);
		}
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int numUpdated = 0;

		if (contentValues == null){
			throw new IllegalArgumentException("Cannot have null content values");
		}

		switch(sUriMatcher.match(uri)){

			case FAVORITE_MOVIE:
				numUpdated = db.update(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
						contentValues,
						selection,
						selectionArgs);
				break;

			case FAVORITE_MOVIE_WITH_ID:
				numUpdated = db.update(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
						contentValues,
						FavoriteMoviesContract.FavoriteMovieEntry._ID + " = ?",
						new String[] {String.valueOf(ContentUris.parseId(uri))});
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);

		}

		if (numUpdated > 0){
			// If there was successful updated, notify the Content Resolver
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return numUpdated;
	}

}