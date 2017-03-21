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
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kuruchy.android.and_mymovies.data.MoviesContract;
import com.kuruchy.android.and_mymovies.utilities.TheMovieDatabaseNetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

/**
 * Detail Activity Class.
 *
 * Creates the Detailed view for seeing the Movie's details.
 * Uses {@link "square.github.io/picasso/"} to simplify image control.
 */
public class DetailActivity extends AppCompatActivity {

    private ImageView mPosterImage;
    private TextView mReleaseDate;

    private TextView mOverview;
    private TextView mOriginalTitle;
    private TextView mTitle;
    
    private TextView mVoteAverageNum;
    private RatingBar mVoteAverageStar;

    private Button mShowReviews;
    private TextView mMovieReviews;

    private ImageView mTrailerThumb;
    private ImageButton mPlayTrailer;

    private FloatingActionButton mFAB;

    public static String mTrailer;
    public static String mReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterImage = (ImageView) findViewById(R.id.iv_poster_small);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);

        mOverview = (TextView) findViewById(R.id.tv_overview);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);

        mVoteAverageNum = (TextView) findViewById(R.id.tv_vote_average);
        mVoteAverageStar = (RatingBar) findViewById(R.id.rb_vote_average);

        mShowReviews = (Button) findViewById(R.id.b_reviews);
        mMovieReviews = (TextView) findViewById(R.id.tv_reviews);
        mMovieReviews.setVisibility(View.INVISIBLE);

        mTrailerThumb = (ImageView) findViewById(R.id.iv_trailer);
        mPlayTrailer = (ImageButton) findViewById(R.id.b_trailer);

        mFAB = (FloatingActionButton) findViewById(R.id.favorite_action_button);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("movie_obj")) {
                final Movie mMovie = intentThatStartedThisActivity.getParcelableExtra("movie_obj");
                this.setTitle(mMovie.getTitle());

                loadFetchTrailerMovieData(mMovie.getId());
                loadFetchReviewMovieData(mMovie.getId());

                mOverview.setText(mMovie.getOverview());
                mOriginalTitle.setText(mMovie.getOriginal_title());
                mTitle.setText(mMovie.getTitle());
                mReleaseDate.setText(mMovie.getRelease_date());

                mVoteAverageNum.setText((mMovie.getVote_average() / 2) + " / " + mMovie.getVote_count() + " votes");
                mVoteAverageStar.setRating(mMovie.getVote_average().floatValue() / 2);

                reloadTrailerThumb();

                Picasso.with(this.getBaseContext())
                        .load(mMovie.getPoster_path())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .resize(300, 500)
                        .into(mPosterImage);

                mShowReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMovieReviews.setText(mReviews);
                        mMovieReviews.setVisibility(View.VISIBLE);
                        mShowReviews.setText(R.string.reviews_tag);
                        //mShowReviews.setVisibility(View.INVISIBLE);
                    }
                });

                mPlayTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //openWebPage(mMovie.getTrailer_path());
                        openWebPage(mTrailer);
                    }
                });

                mFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveMovie(mMovie, MoviesContract.MovieEntry.CONTENT_FAVORITE_URI);
                    }
                });

            }

        }
    }

    private void reloadTrailerThumb() {
        String trailerId = null;

        try {
            trailerId = TheMovieDatabaseNetworkUtils.extractYoutubeId(mTrailer);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String img_url="http://img.youtube.com/vi/" + trailerId + "/0.jpg";

        Picasso.with(this.getBaseContext())
                .load(img_url)
                .placeholder(R.drawable.placeholder)
                .into(mTrailerThumb);
    }

    /**
     * Calls the Async Task that will fetch the trailer movie data and the reviews.
     *
     * @param movieId The movie ID to run the Async Task.
     */
    public static void loadFetchTrailerMovieData(Integer movieId) {
        new FetchTrailerMovieData().execute(movieId);
    }

    /**
     * Calls the Async Task that will fetch the reviews.
     *
     * @param movieId The movie ID to run the Async Task.
     */
    public static void loadFetchReviewMovieData(Integer movieId) {
        new FetchReviewMovieData().execute(movieId);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //TODO fix save data to send all data to content resolver
    public void saveMovie(Movie mMovie, Uri mUri){

        // Insert new task data via a ContentResolver
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
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TRAILER_PATH, mTrailer);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_REVIEWS, mReviews);

        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(mUri, contentValues);

        // Display the URI that's returned with a Toast
        //if(uri != null) {
        //    Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        //}

        // Finish activity (this returns back to MainActivity)
        finish();

    }

    public static String getmReview() {
        return mReviews;
    }

    public static void setmReview(String mReview) {
        DetailActivity.mReviews = mReview;
    }

    public String getmTrailer() {
        return mTrailer;
    }

    public static void setmTrailer(String _mTrailer) {
        mTrailer = _mTrailer;
    }
}