package com.piapps.flashcard.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.piapps.flashcard.R;
import com.piapps.flashcard.fragment.AllSetsFragment;
import com.piapps.flashcard.fragment.DictionaryFragment;
import com.piapps.flashcard.fragment.ImportantFragment;
import com.piapps.flashcard.fragment.MostUsedFragment;
import com.piapps.flashcard.fragment.TodoFragment;
import com.zookey.universalpreferences.UniversalPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.fabAddSet)
    FloatingActionButton fab;
    int fragNum = 0;

    int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

/*        c = UniversalPreferences.getInstance().get("showAd", 0);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.study_interstitial));
        if (c % 5 == 0 && c != 0)
            requestNewInterstitial();
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (c % 5 == 0 && c != 0 && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        });
        c++;
        UniversalPreferences.getInstance().put("showAd", c);*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new AllSetsFragment()).commit();
            getSupportActionBar().setTitle(getString(R.string.menu_all_sets));
        }

        boolean isUserFirstTime = UniversalPreferences.getInstance().get("isUserFirstTime", true);
        if (isUserFirstTime) {
            UniversalPreferences.getInstance().put("isUserFirstTime", false);
            startActivity(new Intent(this, IntroActivity.class));
        }

        boolean userKnowsToAddSets = UniversalPreferences.getInstance().get("userKnowsToAddSets", false);
        if (!userKnowsToAddSets) {
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.fabAddSet), getString(R.string.create_sets), getString(R.string.lets_create_set))
                            // All options below are optional
                            .outerCircleColor(R.color.md_blue_500)      // Specify a color for the outer circle
                            .targetCircleColor(R.color.md_white_1000)   // Specify a color for the target circle
                            .titleTextSize(24)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.md_white_1000)      // Specify the color of the title text
                            .descriptionTextSize(20)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.md_red_500)  // Specify the color of the description text
                            .textColor(R.color.md_white_1000)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.md_black_1000)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(40),                  // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            UniversalPreferences.getInstance().put("userKnowsToAddSets", true);
                            onClickFab();
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //// TODO: 2/19/17: possible crash ->find another way to update the UI
        switch (fragNum) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new AllSetsFragment()).commit();
                getSupportActionBar().setTitle(getString(R.string.menu_all_sets));
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new MostUsedFragment()).commit();
                getSupportActionBar().setTitle(getString(R.string.menu_most_used));
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new ImportantFragment()).commit();
                getSupportActionBar().setTitle(getString(R.string.menu_important));
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new TodoFragment()).commit();
                getSupportActionBar().setTitle(getString(R.string.menu_todo));
                break;
            case 4:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new DictionaryFragment()).commit();
                getSupportActionBar().setTitle(getString(R.string.menu_dictionary));
                break;
        }
    }

    @OnClick(R.id.fabAddSet)
    public void onClickFab() {
        Intent i = new Intent(this, FlashcardActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rate_us) {
            String url = "https://play.google.com/store/apps/details?id=com.piapps.flashcard";
            Uri uri = Uri.parse(url);
            Intent i = new Intent(ACTION_VIEW, uri);
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivity(i);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_sets) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new AllSetsFragment()).commit();
            getSupportActionBar().setTitle(getString(R.string.menu_all_sets));
            fragNum = 0;
        }
        if (id == R.id.nav_starred) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new MostUsedFragment()).commit();
            getSupportActionBar().setTitle(getString(R.string.menu_most_used));
            fragNum = 1;
        } else if (id == R.id.nav_important) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new ImportantFragment()).commit();
            getSupportActionBar().setTitle(getString(R.string.menu_important));
            fragNum = 2;
        } else if (id == R.id.nav_todo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new TodoFragment()).commit();
            getSupportActionBar().setTitle(getString(R.string.menu_todo));
            fragNum = 3;
        } else if (id == R.id.nav_dictionary) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new DictionaryFragment()).commit();
            getSupportActionBar().setTitle(getString(R.string.menu_dictionary));
            fragNum = 4;
        } else if (id == R.id.nav_create_new_label) {
            startActivity(new Intent(this, AddLabelActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_help) {
            //// TODO: 2/14/17: share the app link
            startActivity(new Intent(this, IntroActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
