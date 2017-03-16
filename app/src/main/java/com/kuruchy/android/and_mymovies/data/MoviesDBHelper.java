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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
* MoviesDBHelper
*
* Database Helper Class for accessing the Database.
*
*/
public class MoviesDBHelper extends SQLiteOpenHelper {
	public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

	//name & version
	private static final String DATABASE_NAME = "movies.db";
	private static final int DATABASE_VERSION = 4;

	public MoviesDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create the database
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String tableName : MoviesContract.MovieEntry.TABLE_NAMES){
            final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                    tableName +
                    "(" +
                    MoviesContract.MovieEntry._ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE +
                    " TEXT NOT NULL, " +
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ORG_TITLE +
                    " TEXT NOT NULL, " +
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ID +
                    " INTEGER NOT NULL, " +
                    MoviesContract.MovieEntry.COLUMN_POSTER_PATH +
                    " TEXT NOT NULL, " +
                    //MoviesContract.MovieEntry.COLUMN_POSTER_PIC +
                    //" TEXT NOT NULL, " +
                    MoviesContract.MovieEntry.COLUMN_SYNOPSIS +
                    " TEXT NOT NULL, " +
                    MoviesContract.MovieEntry.COLUMN_USER_RATING +
                    " TEXT NOT NULL, " +
                    MoviesContract.MovieEntry.COLUMN_GLOBAL_RATING +
                    " TEXT NOT NULL, " +
                    MoviesContract.MovieEntry.COLUMN_RELEASE_DATE +
                    " TEXT NOT NULL, " +
                    MoviesContract.MovieEntry.COLUMN_TRAILER_PATH +
                    " TEXT NOT NULL);";

            sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        }
	}

	// Upgrade database when version is changed.
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
				newVersion + ". OLD DATA WILL BE DESTROYED");
		// Drop the table
        for(String tableName: MoviesContract.MovieEntry.TABLE_NAMES){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
            sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tableName + "'");
        }
		// Re-create database
		onCreate(sqLiteDatabase);
	}
}