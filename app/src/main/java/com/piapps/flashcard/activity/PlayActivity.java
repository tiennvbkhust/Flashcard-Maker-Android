package com.piapps.flashcard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.piapps.flashcard.R;
import com.piapps.flashcard.adapter.PlayViewPagerAdapter;
import com.piapps.flashcard.db.CardDb;
import com.piapps.flashcard.db.StatsDb;
import com.piapps.flashcard.fragment.CardFragment;
import com.piapps.flashcard.model.Card;
import com.piapps.flashcard.model.Stats;
import com.piapps.flashcard.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayActivity extends AppCompatActivity {


    @BindView(R.id.textTitle)
    TextView textTitle;

    @BindView(R.id.textCount)
    TextView textCount;

    String title;
    String setId = "";
    String color = "#555555";

    @BindView(R.id.playPager)
    CustomViewPager playPager;

    @BindView(R.id.fab_dont_know)
    FloatingActionButton fabDontKnow;

    @BindView(R.id.fab_right)
    FloatingActionButton fabKnow;

    CardDb cardDb;
    List<Card> cards;
    List<CardFragment> fragments;

    StatsDb statsDb;

    PlayViewPagerAdapter adapter;

    Stats stats;

    long startTime = 0;
    long endTime = 0;
    int[] ansList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        playPager.setPagingEnabled(false);
        startTime = System.currentTimeMillis();

        title = getIntent().getStringExtra("TITLE");
        setId = getIntent().getStringExtra("SET_ID");
        color = getIntent().getStringExtra("COLOR");

        stats = new Stats();
        stats.setSetId(setId);
        statsDb = StatsDb.getInstance(getApplicationContext());

        textTitle.setText(title);

        cardDb = CardDb.getInstance(getApplicationContext());
        cards = cardDb.getSetCards(setId);

        fragments = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            CardFragment cardFragment = new CardFragment();
            cardFragment.setCard(cards.get(i));
            fragments.add(cardFragment);
        }

        ansList = new int[fragments.size()];
        for (int i = 0; i < ansList.length; i++) {
            ansList[i] = -1;
        }

        adapter = new PlayViewPagerAdapter(getSupportFragmentManager(), fragments);
        playPager.setAdapter(adapter);

        playPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textCount.setText((position + 1) + " / " + cards.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.fab_right)
    public void onClickFabKnow() {
        if (ansList[playPager.getCurrentItem()] == -1)
            ansList[playPager.getCurrentItem()] = 1;
        if (playPager.getCurrentItem() + 1 < fragments.size())
            playPager.setCurrentItem(playPager.getCurrentItem() + 1);
        else {
            update();
            //Toast.makeText(this, "Last card", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Last card", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra("SET", title);
            intent.putExtra("SET_ID", setId);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.fab_dont_know)
    public void onClickFabDontKnow() {
        ansList[playPager.getCurrentItem()] = 0;
        fragments.get(playPager.getCurrentItem()).flipCard();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        update();
    }

    void update() {
        endTime = System.currentTimeMillis();
        int ansCount = 0;
        for (int i = 0; i < ansList.length; i++) {
            if (ansList[i] == 1)
                ansCount++;
        }
        long studyTime = endTime - startTime;
        stats.setTrueAnswers(ansCount + "");
        stats.setTimeSpent(studyTime + "");
        stats.setDate(endTime + "");
        Log.d("PLAY", "update() called");
        statsDb.addStats(stats);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
