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

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.kuruchy.android.and_mymovies.data.MoviesContract;

/**
 * Movie Class.
 *
 * Implements Parcelable in order to be able to pass it as a parameter between activities.
 * Defines all the properties a movie object has.
 */
public class Movie implements Parcelable {

    private String poster_path;
    private String overview;
    private String release_date;
    private String original_title;
    private String original_language;
    private String title;
    private String backdrop_path;
    private int id;
    private int vote_count;
    private Double vote_average;
    private Double popularity;
    private boolean video;
    private boolean adult;
    private int[] genre_ids;

    private String trailer_path;
    private Double vote_user;

    private Cursor mCursor;

    public Movie() {

    }

    public Movie(Cursor mCursor) {
        this.poster_path = mCursor.getString(MainActivity.INDEX_MOVIE_PATH);
        this.overview = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_SYNOPSIS));
        this.release_date = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
        this.original_title = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ORG_TITLE));
        this.original_language = "ELFIC";
        this.title = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE));
        this.backdrop_path = "";
        this.id = mCursor.getInt(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));
        this.vote_count = mCursor.getInt(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_USER_RATING));
        this.vote_average = mCursor.getDouble(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_GLOBAL_RATING));
        this.popularity = mCursor.getDouble(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_GLOBAL_RATING));
        this.trailer_path = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TRAILER_PATH));
        //this.vote_user = mCursor.getInt(mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_USER_RATING));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(poster_path);
        out.writeString(overview);
        out.writeString(release_date);
        out.writeString(original_title);
        out.writeString(original_language);
        out.writeString(title);
        out.writeString(backdrop_path);
        out.writeInt(id);
        out.writeInt(vote_count);
        out.writeDouble(vote_average);
        out.writeDouble(popularity);
        out.writeString(trailer_path);
        //out.writeDouble(vote_user);
    }

    public Movie(Parcel in){
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        id = in.readInt();
        vote_count = in.readInt();
        vote_average = in.readDouble();
        popularity = in.readDouble();
        trailer_path = in.readString();
        //vote_user = in.readDouble();
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int[] getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(int[] genre_ids) {
        this.genre_ids = genre_ids;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public String getTrailer_path() {
        return trailer_path;
    }

    public void setTrailer_path(String trailer_path) {
        this.trailer_path = trailer_path;
    }

    public Double getVote_user() {
        return vote_user;
    }

    public void setVote_user(Double vote_user) {
        this.vote_user = vote_user;
    }

}
