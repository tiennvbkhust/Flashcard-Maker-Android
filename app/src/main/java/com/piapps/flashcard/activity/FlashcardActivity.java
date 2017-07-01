package com.piapps.flashcard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.gregacucnik.EditTextView;
import com.piapps.flashcard.R;
import com.piapps.flashcard.adapter.RVCardsAdapter;
import com.piapps.flashcard.adapter.RVSetLabelsAdapter;
import com.piapps.flashcard.db.CardDb;
import com.piapps.flashcard.db.FlashcardDb;
import com.piapps.flashcard.model.Card;
import com.piapps.flashcard.model.Flashcard;
import com.piapps.flashcard.model.Label;
import com.piapps.flashcard.util.Utils;
import com.piapps.flashcard.view.RecyclerListView;
import com.zookey.universalpreferences.UniversalPreferences;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;
import eltos.simpledialogfragment.input.SimpleInputDialog;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

import static android.view.View.GONE;

public class FlashcardActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener {

    public final static int PICK_LABELS_REQUEST = 1;
    public final static int PICK_DRAWING_REQUEST = 2;
    public static final String ENTER_FRONT_DIALOG = "enter_front_dialog";
    public static final int REQUEST_IMAGE_CAPTURE = 3;
    public static final int TAKE_PHOTO_CODE = 4;
    final static String COLOR_DIALOG = "color_dialog_tag";
    public static FlashcardActivity instance;
    public FlashcardDb flashcardDb;
    public CardDb cardDb;
    String TAG = "FLASH";
    Toolbar toolbar;
    @BindView(R.id.content_flashcard)
    RelativeLayout contentFlashcard;
    @BindView(R.id.editTextSetName)
    EditTextView editTextView;
    @BindView(R.id.fabPlay)
    FloatingActionButton fabPlay;
    @BindView(R.id.fabStudy)
    FloatingActionButton fabPlayStudy;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;
    @BindView(R.id.rvFlashcards)
    RecyclerListView rv;
    @BindView(R.id.rvLabels)
    RecyclerListView rvLabels;
    List<Label> labelList;
    RVSetLabelsAdapter rvSetLabelsAdapter;
    List<Card> list;
    RVCardsAdapter adapter;
    LinearLayoutManager lm;
    int[] colors = new int[17];
    String hexColor = "#FFFFE57F";
    int editingPosition = 0;
    boolean editingBack = false;
    Flashcard flashcard;
    String setId;
    LinearLayout noSets;
    boolean isNewlyCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        instance = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        setId = getIntent().getStringExtra("SET_ID");

        flashcardDb = new FlashcardDb(getApplicationContext());
        //get current flashcard from db
        if (setId != null)
            flashcard = flashcardDb.getFlashcard(setId);
            //if it does not exist, then create new one
        else {
            flashcard = new Flashcard(System.currentTimeMillis() + "", getString(R.string.untitled_set), "0", "", "#FFFFE57F", "0");
            isNewlyCreated = true;
        }

        String flashcardsTitle = flashcard.getTitle();
        editTextView.setText(flashcardsTitle);

        cardDb = new CardDb(getApplicationContext());
        if (setId != null)
            list = cardDb.getSetCards(setId);
        else
            list = new ArrayList<>();

        contentFlashcard.setClickable(true);
        contentFlashcard.setFocusable(true);
        lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setItemAnimator(new FadeInUpAnimator());
        rv.setLayoutManager(lm);

        adapter = new RVCardsAdapter(list);
        rv.setAdapter(adapter);

        labelList = new ArrayList<>();
        if (!flashcard.getLabel().equals("")) {
            List<String> s = Arrays.asList(flashcard.getLabel().split("___"));
            for (int i = 0; i < s.size(); i++) {
                labelList.add(new Label(s.get(i), R.drawable.ic_other_label_24dp));
            }
        }

        rvSetLabelsAdapter = new RVSetLabelsAdapter(labelList);
        rvLabels.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvLabels.setAdapter(rvSetLabelsAdapter);

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

        noSets = (LinearLayout) findViewById(R.id.no_sets);
        if (list.isEmpty())
            noSets.setVisibility(View.VISIBLE);
        else
            noSets.setVisibility(GONE);

        boolean userKnowsToAddFlashcards = UniversalPreferences.getInstance().get("userKnowsToAddFlashcards", false);
        if (!userKnowsToAddFlashcards) {
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.fabAdd), getString(R.string.create_flashcards), getString(R.string.lets_create_flashcards))
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
                            UniversalPreferences.getInstance().put("userKnowsToAddFlashcards", true);
                            onClickFabAdd();
                        }
                    });
        }
    }

    @OnClick(R.id.content_flashcard)
    public void onClickOther() {
        contentFlashcard.requestFocus();
    }

    @OnClick(R.id.fabPlay)
    public void onClickFab() {
        if (!list.isEmpty()) {
            //increment useCount
            flashcard.setUseCount((Integer.parseInt(flashcard.getUseCount()) + 1) + "");
            updateSet();
            //play activity
            Intent intent = new Intent(this, PlayActivity.class);
            intent.putExtra("SET_ID", flashcard.getSetId());
            intent.putExtra("COLOR", flashcard.getColor());
            intent.putExtra("TITLE", flashcard.getTitle());
            startActivity(intent);
        } else {
            Toast.makeText(instance, getString(R.string.empty_set), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.fabStudy)
    public void onClickFabStudy() {
        if (!list.isEmpty()) {
            //increment useCount
            flashcard.setUseCount((Integer.parseInt(flashcard.getUseCount()) + 1) + "");
            updateSet();
            //play activity
            Intent intent = new Intent(this, StudyActivity.class);
            intent.putExtra("SET_ID", flashcard.getSetId());
            intent.putExtra("COLOR", flashcard.getColor());
            intent.putExtra("TITLE", flashcard.getTitle());
            startActivity(intent);
        } else {
            Toast.makeText(instance, getString(R.string.empty_set), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.fabAdd)
    public void onClickFabAdd() {
        //// TODO: 2/16/17: possible crash [Called attach on a child which is not detached]
        int pos;
        if (lm.findFirstCompletelyVisibleItemPosition() != -1) {
            pos = lm.findFirstCompletelyVisibleItemPosition();
        } else {
            pos = lm.findFirstVisibleItemPosition() + 1;
        }
        Card addingCard = new Card(System.currentTimeMillis() + "", flashcard.getSetId(), getString(R.string.example_card_front),
                getString(R.string.example_card_back), flashcard.getColor(), "no", "no", "no", "no");
        list.add(pos, addingCard);
        adapter.notifyItemInserted(pos);
        adapter.notifyItemRangeChanged(lm.findFirstVisibleItemPosition(), lm.findLastVisibleItemPosition());
        rv.scrollToPosition(pos);
        noSets.setVisibility(GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateSet();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flashcard, menu);
        return true;
    }

    public void updateSet() {
        //update set title
        flashcard.setTitle(editTextView.getText() + "");
        if (flashcard.getTitle().isEmpty())
            flashcard.setTitle(getString(R.string.untitled_set));

        //update set count
        flashcard.setCount(list.size() + "");

        //update labels
        String lbls = "";
        for (int i = 0; i < labelList.size(); i++) {
            lbls += labelList.get(i).getTitle() + "___";
        }
        flashcard.setLabel(lbls);

        //if it exists, update it
        if (setId != null)
            flashcardDb.updateFlashcard(flashcard);
        else //else create a new set
            flashcardDb.addFlashcard(flashcard);

        //update its cards as well
        for (int i = 0; i < list.size(); i++) {
            if (cardDb.getCard(list.get(i).getId()) != null)
                cardDb.updateCard(list.get(i));
            else
                cardDb.addCard(list.get(i));
            noSets.setVisibility(GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_save_set) {
            updateSet();
            Toast.makeText(instance, getString(R.string.set_saved), Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        if (id == R.id.action_rate_us) {
            //// TODO: 2/18/17 do you really want to delete this set?
            //delete this set
            flashcardDb.deleteFlashcard(flashcard);
            //delete its cards as well
            for (int i = 0; i < list.size(); i++) {
                cardDb.deleteCard(list.get(i));
            }
            //notify user
            Toast.makeText(instance, getString(R.string.set_deleted), Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        if (id == R.id.action_add_label) {
            startActivityForResult(new Intent(this, AddLabelActivity.class), PICK_LABELS_REQUEST);
            return true;
        }
        if (id == R.id.action_stats) {
            Intent i = new Intent(this, StatisticsActivity.class);
            i.putExtra("SET_ID", setId);
            i.putExtra("SET", editTextView.getText().toString());
            if (!isNewlyCreated)
                startActivity(i);
            else
                Toast.makeText(instance, "No statistics available yet", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_color) {
            SimpleColorDialog.build()
                    .msg(getString(R.string.choose_set_color))
                    .colorPreset(colors[0])
                    .colors(colors)
                    .allowCustom(true)
                    .show(FlashcardActivity.this, COLOR_DIALOG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getEditingPosition() {
        return editingPosition;
    }

    public void setEditingPosition(int editingPosition) {
        this.editingPosition = editingPosition;
    }

    public boolean isEditingBack() {
        return editingBack;
    }

    public void setEditingBack(boolean editingBack) {
        this.editingBack = editingBack;
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (ENTER_FRONT_DIALOG.equals(dialogTag) && which == BUTTON_POSITIVE) {
            String text = extras.getString(SimpleInputDialog.TEXT);
            Log.d(TAG, "onResult: name = " + text);
            if (!editingBack) {
                Utils.deleteExistingImage(list.get(editingPosition).getFrontPath(), list.get(editingPosition).getFrontImage());
                list.get(editingPosition).setFrontImage("no");
                adapter.setFrontText(editingPosition, text);
                //bug Akmaljon fixed here
            } else {
                Utils.deleteExistingImage(list.get(editingPosition).getBackPath(), list.get(editingPosition).getBackImage());
                list.get(editingPosition).setBackImage("no");
                adapter.setBackText(editingPosition, text);
                //bug Akmaljon fixed here
            }
            adapter.notifyDataSetChanged();
            return true;
        }
        if (COLOR_DIALOG.equals(dialogTag) && which == BUTTON_POSITIVE) {
            int color = extras.getInt(SimpleColorDialog.COLOR);
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            flashcard.setColor(hexColor);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setColor(hexColor);
            }
            this.hexColor = hexColor;
            for (int j = 0; j < list.size(); j++) {
                list.get(j).setColor(hexColor);
            }
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 256;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == PICK_LABELS_REQUEST && data != null && data.getStringArrayListExtra("LABELS") != null) {
            List<String> labels = data.getStringArrayListExtra("LABELS");
            labelList.clear();
            for (int i = 0; i < labels.size(); i++) {
                labelList.add(new Label(labels.get(i), R.drawable.ic_other_label_24dp));
            }
            rvSetLabelsAdapter.notifyDataSetChanged();
        }
        if (requestCode == PICK_DRAWING_REQUEST) {
            String path = data.getStringExtra("path");
            String image = data.getStringExtra("image");
            Boolean isBack = data.getBooleanExtra("isBack", false);
            Bitmap bitmap = Utils.loadImageFromStorage(path, image);
            Log.d(TAG, "onActivityResult: editingPosition = " + editingPosition + "\neditingBack = " + isBack);
            if (bitmap != null) {
                if (!isBack)
                    adapter.updateImages(editingPosition, image, path);
                else
                    adapter.updateImagesBack(editingPosition, image, path);
                updateSet();
            } else
                Toast.makeText(instance, getString(R.string.could_not_load_image), Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //contentFlashcard.setBackground(new BitmapDrawable(imageBitmap));
            if (imageBitmap != null) {
                String image = System.currentTimeMillis() + ".jpg";
                String path = Utils.saveToInternalStorage(imageBitmap, getApplicationContext(), image);
                if (isEditingBack())
                    adapter.updateImagesBack(editingPosition, image, path);
                else
                    adapter.updateImages(editingPosition, image, path);
                updateSet();
            }
        }
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap yourSelectedImage = null;
            try {
                yourSelectedImage = decodeUri(selectedImage);
                //contentFlashcard.setBackground(new BitmapDrawable(yourSelectedImage));
                if (yourSelectedImage != null) {
                    String image = System.currentTimeMillis() + ".jpg";
                    String path = Utils.saveToInternalStorage(yourSelectedImage, getApplicationContext(), image);
                    if (isEditingBack())
                        adapter.updateImagesBack(editingPosition, image, path);
                    else
                        adapter.updateImages(editingPosition, image, path);
                    updateSet();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(instance, getString(R.string.could_not_load_image), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
