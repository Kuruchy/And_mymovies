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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kuruchy.android.and_mymovies.data.FavoriteMoviesContract;
import com.squareup.picasso.Picasso;

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

    private ImageButton mPlayTrailer;

    private FloatingActionButton mFAB;

    private static String mTrailer;

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

        mPlayTrailer = (ImageButton) findViewById(R.id.b_trailer);

        mFAB = (FloatingActionButton) findViewById(R.id.favorite_action_button);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("movie_obj")) {
                final Movie mMovie = (Movie) intentThatStartedThisActivity.getParcelableExtra("movie_obj");
                this.setTitle(mMovie.getTitle());

                loadFetchTrailerMovieData(mMovie.getId());

                mOverview.setText(mMovie.getOverview());
                mOriginalTitle.setText(mMovie.getOriginal_title());
                mTitle.setText(mMovie.getTitle());
                mReleaseDate.setText(mMovie.getRelease_date());

                mVoteAverageNum.setText((mMovie.getVote_average() / 2) + " / " + mMovie.getVote_count() + " votes");
                mVoteAverageStar.setRating(mMovie.getVote_average().floatValue() / 2);

                Picasso.with(this.getBaseContext())
                        .load(mMovie.getPoster_path())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .resize(300, 500)
                        .into(mPosterImage);

                mPlayTrailer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //openWebPage(mMovie.getTrailer_path());
                        openWebPage(mTrailer);
                    }
                });

            }

        }
    }

    /**
     * Calls the Async Task that will fetch the trailer movie data.
     *
     * @param movieId The movie ID to run the Async Task.
     */
    private void loadFetchTrailerMovieData(Integer movieId) {
        new FetchTrailerMovieData().execute(movieId);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //TODO fix save data to send all data to content resolver
    public void saveData(String id){

        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, id);
        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI, contentValues);

        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        // Finish activity (this returns back to MainActivity)
        finish();

    }


    public String getmTrailer() {
        return mTrailer;
    }

    public static void setmTrailer(String _mTrailer) {
        mTrailer = _mTrailer;
    }
}