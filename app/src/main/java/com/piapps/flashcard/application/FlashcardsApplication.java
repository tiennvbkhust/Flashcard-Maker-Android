package com.piapps.flashcard.application;

import android.app.Application;
import android.content.Context;

import com.zookey.universalpreferences.UniversalPreferences;

/**
 * Created by abduaziz on 2/28/17.
 */

public class FlashcardsApplication extends Application{

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        UniversalPreferences.initialize(this);
        context = getApplicationContext();
    }
}
