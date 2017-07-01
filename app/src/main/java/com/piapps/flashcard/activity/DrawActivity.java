package com.piapps.flashcard.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.piapps.flashcard.R;
import com.piapps.flashcard.util.Utils;
import com.rm.freedrawview.FreeDrawView;
import com.rm.freedrawview.PathDrawnListener;
import com.rm.freedrawview.PathRedoUndoCountChangeListener;
import com.rm.freedrawview.ResizeBehaviour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;
import static com.piapps.flashcard.activity.FlashcardActivity.COLOR_DIALOG;

public class DrawActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SimpleDialog.OnDialogResultListener {

    private static final String TAG = "DRAW";
    @BindView(R.id.draw_view)
    FreeDrawView drawView;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    int[] colors = new int[17];
    boolean isBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        isBack = getIntent().getBooleanExtra("isBack", false);

        colors[0] = ContextCompat.getColor(this, R.color.md_red_A100);
        colors[1] = ContextCompat.getColor(this, R.color.md_pink_A100);
        colors[2] = ContextCompat.getColor(this, R.color.md_purple_A100);
        colors[3] = ContextCompat.getColor(this, R.color.md_deep_purple_A100);
        colors[4] = ContextCompat.getColor(this, R.color.md_indigo_A100);
        colors[5] = ContextCompat.getColor(this, R.color.md_blue_A100);
        colors[6] = ContextCompat.getColor(this, R.color.md_blue_A200);
        colors[7] = ContextCompat.getColor(this, R.color.md_light_blue_A100);
        colors[8] = ContextCompat.getColor(this, R.color.md_cyan_200);
        colors[9] = ContextCompat.getColor(this, R.color.md_cyan_400);
        colors[10] = ContextCompat.getColor(this, R.color.md_teal_A400);
        colors[11] = ContextCompat.getColor(this, R.color.md_green_A100);
        colors[12] = ContextCompat.getColor(this, R.color.md_light_green_A200);
        colors[13] = ContextCompat.getColor(this, R.color.md_lime_A200);
        colors[14] = ContextCompat.getColor(this, R.color.md_yellow_200);
        colors[15] = ContextCompat.getColor(this, R.color.md_amber_A100);
        colors[16] = ContextCompat.getColor(this, R.color.md_orange_A100);

        drawView.setPaintColor(Color.BLACK);
        drawView.setPaintWidthPx(getResources().getDimensionPixelSize(R.dimen.paint_width_big));
        //mSignatureView.setPaintWidthPx(12);
        drawView.setPaintWidthDp(getResources().getDimension(R.dimen.paint_width_big));
        //mSignatureView.setPaintWidthDp(6);
        drawView.setPaintAlpha(255);// from 0 to 255
        drawView.setResizeBehaviour(ResizeBehaviour.CROP);// Must be one of ResizeBehaviour

        // This listener will be notified every time the path done and undone count changes
        drawView.setPathRedoUndoCountChangeListener(new PathRedoUndoCountChangeListener() {
            @Override
            public void onUndoCountChanged(int undoCount) {
                // The undoCount is the number of the paths that can be undone
                Log.d("DRAW", "onUndoCountChanged() called with: undoCount = [" + undoCount + "]");
            }

            @Override
            public void onRedoCountChanged(int redoCount) {
                // The redoCount is the number of path removed that can be redrawn
                Log.d("DRAW", "onRedoCountChanged() called with: redoCount = [" + redoCount + "]");
            }
        });
        // This listener will be notified every time a new path has been drawn
        drawView.setOnPathDrawnListener(new PathDrawnListener() {
            @Override
            public void onNewPathDrawn() {
                // The user has finished drawing a path
            }

            @Override
            public void onPathStart() {
                // The user has started drawing a path
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_redo) {
            drawView.redoLast();
            return true;
        }

        if (id == R.id.action_undo) {
            drawView.undoLast();
            return true;
        }

        if (id == R.id.action_ok) {
            // This will take a screenshot of the current drawn content of the view
            drawView.getDrawScreenshot(new FreeDrawView.DrawCreatorListener() {
                @Override
                public void onDrawCreated(Bitmap draw) {
                    // The draw Bitmap is the drawn content of the View
                    String imageName = System.currentTimeMillis()+".jpg";
                    String path = Utils.saveToInternalStorage(draw, getApplicationContext(), imageName);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("path", path);
                    returnIntent.putExtra("image",imageName);
                    returnIntent.putExtra("isBack",isBack);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

                @Override
                public void onDrawCreationError() {
                    // Something went wrong creating the bitmap, should never
                    // happen unless the async task has been canceled
                    Log.d("DRAW", "onDrawCreationError() called");
                }
            });
            return true;
        }

        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_color:
                SimpleColorDialog.build()
                        .msg(getString(R.string.choose_set_color))
                        .colorPreset(colors[0])
                        .colors(colors)
                        .allowCustom(true)
                        .show(DrawActivity.this, COLOR_DIALOG);
                break;
            case R.id.action_very_big:
                drawView.setPaintWidthDp(getResources().getDimension(R.dimen.paint_width_very_big));
                break;
            case R.id.action_big:
                drawView.setPaintWidthDp(getResources().getDimension(R.dimen.paint_width_big));
                break;
            case R.id.action_small:
                drawView.setPaintWidthDp(getResources().getDimension(R.dimen.paint_width_small));
                break;
        }
        return true;
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (COLOR_DIALOG.equals(dialogTag) && which == BUTTON_POSITIVE) {
            int color = extras.getInt(SimpleColorDialog.COLOR);
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            drawView.setPaintColor(Color.parseColor(hexColor));
            bottomNavigationView.getMenu().getItem(0).getIcon().setColorFilter(Color.parseColor(hexColor), PorterDuff.Mode.SRC_IN);
            return true;
        }
        return false;
    }

}
