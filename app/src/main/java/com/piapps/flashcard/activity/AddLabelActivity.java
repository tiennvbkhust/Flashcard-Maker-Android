package com.piapps.flashcard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.piapps.flashcard.R;
import com.piapps.flashcard.adapter.RVLabelsAdapter;
import com.piapps.flashcard.db.LabelsDb;
import com.piapps.flashcard.model.CLabel;
import com.piapps.flashcard.model.Label;
import com.piapps.flashcard.view.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.input.SimpleInputDialog;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class AddLabelActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener {

    private static final String LABEL_DIALOG = "label_dialog";
    @BindView(R.id.rvLabels)
    RecyclerListView rv;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    RVLabelsAdapter adapter;
    List<CLabel> list;

    LabelsDb labelsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        list = new ArrayList<>();
        list.add(new CLabel(getString(R.string.menu_important), R.drawable.ic_important_24dp,false));
        list.add(new CLabel(getString(R.string.menu_todo), R.drawable.ic_todo_24dp,false));
        list.add(new CLabel(getString(R.string.menu_dictionary), R.drawable.ic_dictionary_24dp,false));

        labelsDb = LabelsDb.getInstance(getApplicationContext());

        for (int i = 0; i < labelsDb.getAllFlashcards().size(); i++) {
            list.add(new CLabel(labelsDb.getAllFlashcards().get(i),R.drawable.ic_other_label_24dp,false));
        }

        adapter = new RVLabelsAdapter(list);
        rv.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        SimpleInputDialog.build()
                .msg(R.string.add_new_label)
                .allowEmpty(false)
                .hint(R.string.add_label_hint)
                .show(AddLabelActivity.this, LABEL_DIALOG);
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (LABEL_DIALOG.equals(dialogTag) && which == BUTTON_POSITIVE) {
            String text = extras.getString(SimpleInputDialog.TEXT);
            list.add(new CLabel(text, R.drawable.ic_other_label_24dp,true));
            labelsDb.addFlashcard(text);
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_labels, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_ok) {
            //add labels
            ArrayList<String> labels = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isChecked())
                    labels.add(list.get(i).getTitle());
            }
            Intent returnIntent = new Intent();
            returnIntent.putStringArrayListExtra("LABELS",labels);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
