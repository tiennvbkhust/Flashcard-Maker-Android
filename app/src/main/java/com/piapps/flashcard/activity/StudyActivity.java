package com.piapps.flashcard.activity;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyActivity extends AppCompatActivity {

    @BindView(R.id.textTitle)
    TextView textTitle;

    @BindView(R.id.textCount)
    TextView textCount;

    String title;
    String setId = "";
    String color = "#555555";

    @BindView(R.id.playPager)
    ViewPager playPager;

    @BindView(R.id.fab_shuffle)
    FloatingActionButton fabDontKnow;

    CardDb cardDb;
    List<Card> cards;
    List<CardFragment> fragments;

    StatsDb statsDb;

    PlayViewPagerAdapter adapter;

    Stats stats;

    long startTime = 0;
    long endTime = 0;

    // InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_study);
        ButterKnife.bind(this);
        startTime = System.currentTimeMillis();

//        interstitialAd = new InterstitialAd(this);
//        interstitialAd.setAdUnitId(getString(R.string.study_interstitial));
//        requestNewInterstitial();

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

        textCount.setText("1 / " + fragments.size());

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

//    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//        interstitialAd.loadAd(adRequest);
//    }

    @OnClick(R.id.fab_flip)
    public void onClickFabFlip() {
        fragments.get(playPager.getCurrentItem()).flipCard();
    }

    @OnClick(R.id.fab_shuffle)
    public void onClickFab() {
        int r = (int) (Math.random() * 1000 % fragments.size());
        if (r == playPager.getCurrentItem()) {
            if (r + 1 < fragments.size())
                r++;
            else if (r - 1 >= 0)
                r--;
        }
        playPager.setCurrentItem(r);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (interstitialAd.isLoaded()) {
//            interstitialAd.show();
//        }
        update();
    }

    void update() {
        endTime = System.currentTimeMillis();
        long studyTime = endTime - startTime;
        stats.setTrueAnswers("0");
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
