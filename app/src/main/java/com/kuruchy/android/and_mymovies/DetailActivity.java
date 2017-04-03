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
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kuruchy.android.and_mymovies.data.MoviesContract;

/**
 * Detail Activity Class.
 *
 * Creates the Detailed view for seeing the Movie's details.
 * Uses {@link "github.com/bumptech/glide/"} to simplify image control.
 */
public class DetailActivity extends AppCompatActivity {

    private ImageView mPosterImage;
    private ImageView mBackDropImage;

    private TextView mReleaseDate;

    private TextView mOverview;
    private TextView mOriginalTitle;
    private TextView mTitle;
    
    private TextView mVoteAverageNum;
    private RatingBar mVoteAverageStar;

    private Button mShowReviews;
    private TextView mMovieReviews;

    public static ImageView mTrailerThumbnail;
    private ImageButton mPlayTrailer;

    private FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPosterImage = (ImageView) findViewById(R.id.iv_poster_small);
        mBackDropImage = (ImageView) findViewById(R.id.iv_backdrop);

        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);

        mOverview = (TextView) findViewById(R.id.tv_overview);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);

        mVoteAverageNum = (TextView) findViewById(R.id.tv_vote_average);
        mVoteAverageStar = (RatingBar) findViewById(R.id.rb_vote_average);

        mShowReviews = (Button) findViewById(R.id.b_reviews);
        mMovieReviews = (TextView) findViewById(R.id.tv_reviews);
        mMovieReviews.setVisibility(View.INVISIBLE);

        mTrailerThumbnail = (ImageView) findViewById(R.id.iv_trailer);
        mPlayTrailer = (ImageButton) findViewById(R.id.b_trailer);

        mFAB = (FloatingActionButton) findViewById(R.id.favorite_action_button);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("movie_obj")) {
                final Movie mMovie = intentThatStartedThisActivity.getParcelableExtra("movie_obj");
                this.setTitle(mMovie.getTitle());

                loadFetchExtraMovieData(mMovie, getBaseContext());

                CollapsingToolbarLayout collapsingToolbar =
                        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(mMovie.getTitle());

                loadBackdrop(mMovie.getBackdrop_path());

                mOverview.setText(mMovie.getOverview());
                mOriginalTitle.setText(mMovie.getOriginal_title());
                mTitle.setText(mMovie.getTitle());
                mReleaseDate.setText(mMovie.getRelease_date());

                mVoteAverageNum.setText((mMovie.getVote_average() / 2) + " / " + mMovie.getVote_count() + " votes");
                mVoteAverageStar.setRating(mMovie.getVote_average().floatValue() / 2);

                loadTrailerImage(mMovie.getPoster_path());

                mShowReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMovieReviews.setText(mMovie.getReviews());
                        mMovieReviews.setVisibility(View.VISIBLE);
                        mShowReviews.setText(R.string.reviews_tag);
                    }
                });

                mPlayTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWebPage(mMovie.getTrailer_path());
                    }
                });

                mFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int movieId = mMovie.getId();
                        if(!movieInDatabase(movieId)){
                            saveMovie(mMovie, MoviesContract.MovieEntry.CONTENT_FAVORITE_URI);
                        }else{
                            Snackbar.make(view, "Already in Favorites!", Snackbar.LENGTH_LONG)
                                    .setAction("Delete", new DeleteListener()).show();
                        }
                    }
                });

                loadTrailerThumbnail(mMovie.getTrailer_thumbnail_path());

            }

        }
    }

    private void loadTrailerImage(String imagePath){
        Glide.with(this)
                .load(imagePath)
                .fitCenter()
                .into(mPosterImage);
    }

    // Load Backdrop image with Glide
    private void loadBackdrop(String imagePath) {
        Glide.with(this)
                .load(imagePath)
                .fitCenter()
                .into(mBackDropImage);
    }

    // Load Trailer Thumbnail image with Glide
    private void loadTrailerThumbnail(String imagePath) {
        Glide.with(this)
                .load(imagePath)
                .error(R.drawable.trailer_error)
                .centerCrop()
                .into(mTrailerThumbnail);
    }

    public class DeleteListener implements View.OnClickListener{
    @Override
        public void onClick(View v) {

            Toast.makeText(getBaseContext(), "Deleted!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Calls the Async Task that will fetch the extra data.
     *
     * @param movie The movie to run the Async Task.
     * @param context The context to run the Async Task.
     */
    public static void loadFetchExtraMovieData(Movie movie, Context context) {
        new FetchExtraMovieData().execute(movie, context);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // Check if the movie is in Content Provider
    public boolean movieInDatabase(int movieId){
        Cursor cursor = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_FAVORITE_URI, null, null, null, null);

        while (cursor.moveToNext()){
            if (movieId == cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID))){
                return true;
            }
        }
        return false;
    }

    // Returns a ContentValue from a Movie object
    public static ContentValues getContentValue(Movie mMovie){

        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        // Put the movie id and sinopsys into the ContentValues
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ORG_TITLE, mMovie.getOriginal_title().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPoster_path().toString());
        //contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PIC, mMovie.get());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, mMovie.getOverview().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_USER_RATING, 5.0);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_GLOBAL_RATING, mMovie.getVote_average());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getRelease_date().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TRAILER_PATH, mMovie.getTrailer_path().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TRAILER_THUMBNAIL_PATH, mMovie.getTrailer_thumbnail_path().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, mMovie.getBackdrop_path().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_REVIEWS, mMovie.getReviews().toString());

        return contentValues;
    }

    // Save data in Content Provider
    public void saveMovie(Movie mMovie, Uri mUri){

        // Create new empty ContentValues object
        ContentValues contentValues = getContentValue(mMovie);

        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(mUri, contentValues);

        // Finish activity (this returns back to MainActivity)
        finish();
    }
}
