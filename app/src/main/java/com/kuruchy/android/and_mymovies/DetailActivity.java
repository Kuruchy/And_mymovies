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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("movie_obj")) {
                Movie mMovie = (Movie) intentThatStartedThisActivity.getParcelableExtra("movie_obj");

                mOverview.setText(mMovie.getOverview());
                mOriginalTitle.setText(mMovie.getOriginal_title());
                mTitle.setText(mMovie.getTitle());
                mReleaseDate.setText(mMovie.getRelease_date());

                mVoteAverageNum.setText((mMovie.getVote_average()/2)+" / " + mMovie.getVote_count() + " votes");
                mVoteAverageStar.setRating(mMovie.getVote_average().floatValue()/2);

                Picasso.with(this.getBaseContext())
                        .load(mMovie.getPoster_path())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .resize(300, 500)
                        .into(mPosterImage);
            }
        }
    }
}