package com.piapps.flashcard.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.piapps.flashcard.R;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.welcome)
                .description(R.string.speed_up_learning)
                .image(R.drawable.page1)
                .background(R.color.colorDictionary)
                .backgroundDark(R.color.colorAccent)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.create_sets)
                .description(R.string.create_sets_desc)
                .image(R.drawable.page2)
                .background(R.color.colorTodo)
                .backgroundDark(R.color.colorAccent)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.create_flashcards)
                .description(R.string.create_flashcards_desc)
                .image(R.drawable.page3)
                .background(R.color.colorImportant)
                .backgroundDark(R.color.colorAccent)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.study_flashcards)
                .description(R.string.study_flashcards_desc)
                .image(R.drawable.page4)
                .background(R.color.colorOtherLabels)
                .backgroundDark(R.color.colorAccent)
                .build());

    }
}
