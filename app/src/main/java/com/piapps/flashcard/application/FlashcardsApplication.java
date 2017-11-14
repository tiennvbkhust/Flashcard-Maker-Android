package com.piapps.flashcard.application;

import android.app.Application;
import android.content.Context;

import com.piapps.flashcard.db.CardDb;
import com.piapps.flashcard.db.FlashcardDb;
import com.piapps.flashcard.db.LabelsDb;
import com.piapps.flashcard.db.StatsDb;
import com.piapps.flashcard.db.TrashFlashcardDb;
import com.zookey.universalpreferences.UniversalPreferences;

/**
 * Created by abduaziz on 2/28/17.
 */

public class FlashcardsApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        UniversalPreferences.initialize(this);
        context = getApplicationContext();
        CardDb cardDb = CardDb.getInstance(getApplicationContext());
        FlashcardDb flashcardDb = FlashcardDb.getInstance(getApplicationContext());
        LabelsDb labelsDb = LabelsDb.getInstance(getApplicationContext());
        StatsDb statsDb = StatsDb.getInstance(getApplicationContext());
        TrashFlashcardDb trashFlashcardDb = TrashFlashcardDb.getInstance(getApplicationContext());
    }
}
