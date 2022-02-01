package com.bilki.mywardrobe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilki.mywardrobe.NewsModels.NewsHeadlines;
import com.squareup.picasso.Picasso;

public class ChosenNews extends AppCompatActivity {

    private NewsHeadlines headlines;
    private TextView newsTitle, newsAuthor, newsTime, newsContent;
    private ImageView newsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_news);

        newsTitle = (TextView) findViewById(R.id.news_chosen_title);
        newsAuthor = (TextView) findViewById(R.id.news_chosen_author);
        newsTime = (TextView) findViewById(R.id.news_chosen_time);
        newsContent = (TextView) findViewById(R.id.news_chosen_detail);
        newsImage = (ImageView) findViewById(R.id.news_chosen_image);

        headlines = (NewsHeadlines) getIntent().getSerializableExtra("news");

        newsTitle.setText(headlines.getTitle());
        newsAuthor.setText(headlines.getAuthor());
        newsTime.setText(headlines.getPublishedAt());
        newsContent.setText(headlines.getContent());
        Picasso.get().load(headlines.getUrlToImage()).fit().centerInside().into(newsImage);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(ChosenNews.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }
}