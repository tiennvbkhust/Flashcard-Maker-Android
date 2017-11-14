package com.piapps.flashcard.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.piapps.flashcard.R;
import com.piapps.flashcard.db.CardDb;
import com.piapps.flashcard.db.FlashcardDb;
import com.piapps.flashcard.db.LabelsDb;
import com.piapps.flashcard.db.StatsDb;
import com.piapps.flashcard.db.TrashFlashcardDb;
import com.piapps.flashcard.model.Card;
import com.piapps.flashcard.util.DateUtils;
import com.piapps.flashcard.util.Prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DropboxActivity extends AppCompatActivity {

    final static private String APP_KEY = "p6t8mdrqksbj4d0";
    final static private String APP_SECRET = "jf2mxglpggfe2un";
    String TAG = "DropboxActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.auth)
    AppCompatButton buttonAuth;
    @BindView(R.id.parent)
    LinearLayout parent;

    @BindView(R.id.backup)
    LinearLayout linearLayoutBackup;

    @BindView(R.id.restore)
    LinearLayout linearLayoutRestore;

    @BindView(R.id.lastBackup)
    TextView textViewLastBackup;

    @BindView(R.id.lastBackupStatus)
    TextView textViewLastBackupStatus;

    @BindView(R.id.lastRestored)
    TextView textViewLastRestored;

    @BindView(R.id.lastRestoredStatus)
    TextView textViewLastRestoredStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String accessToken = Prefs.getString(getApplicationContext(), "accessToken", "no");

        Log.d(TAG, "onCreate: accessToken = " + accessToken);

        if (!accessToken.equals("no")) {
            //Store accessToken in SharedPreferences
            parent.setVisibility(View.VISIBLE);
            buttonAuth.setVisibility(View.GONE);

            String backupDate = Prefs.getString(getApplicationContext(), "backup_date", "no");

            if (!backupDate.equals("no")) {
                textViewLastBackup.setText("Last backup: " + DateUtils.getFullTime(Long.valueOf(backupDate)));
                textViewLastBackupStatus.setText("Click to back up anytime");
            } else {
                textViewLastBackup.setText("Click to backup");
                textViewLastBackupStatus.setText("Your data will be backed up on Dropbox");
            }

            String restoredDate = Prefs.getString(getApplicationContext(), "restoredDate", "no");

            if (!restoredDate.equals("no")) {
                textViewLastRestored.setText("Last restored: " + DateUtils.getFullTime(Long.valueOf(restoredDate)));
                textViewLastRestoredStatus.setText("Click to restore anytime");
            } else {
                textViewLastRestored.setText("Click to restore");
                textViewLastRestoredStatus.setText("Your data will be restored from Dropbox");
            }

        } else {
            parent.setVisibility(View.GONE);
            buttonAuth.setVisibility(View.VISIBLE);
        }

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

    @OnClick(R.id.backup)
    public void onClickBackup() {

        linearLayoutRestore.setEnabled(false);
        linearLayoutRestore.setAlpha(0.6f);

        File cardsDb = getApplicationContext().getDatabasePath(CardDb.DATABASE_NAME);
        File flashcardsDb = getApplicationContext().getDatabasePath(FlashcardDb.DATABASE_NAME);
        File labelsDb = getApplicationContext().getDatabasePath(LabelsDb.DATABASE_NAME);
        File statsDb = getApplicationContext().getDatabasePath(StatsDb.DATABASE_NAME);
        File trashDb = getApplicationContext().getDatabasePath(TrashFlashcardDb.DATABASE_NAME);

        Log.d("FLASHCARDS DBs", "cardsDb.getName():" + cardsDb.getName());
        Log.d("FLASHCARDS DBs", "flashcardsDb.getAbsolutePath():" + flashcardsDb.getAbsolutePath());
        Log.d("FLASHCARDS DBs", "labelsDb.getAbsolutePath():" + labelsDb.getAbsolutePath());
        Log.d("FLASHCARDS DBs", "statsDb.getAbsolutePath():" + statsDb.getAbsolutePath());
        Log.d("FLASHCARDS DBs", "trashDb.getAbsolutePath():" + trashDb.getAbsolutePath());

        List<File> list = new ArrayList<>();
        list.add(cardsDb);
        list.add(flashcardsDb);
        list.add(labelsDb);
        list.add(statsDb);
        list.add(trashDb);

        String accessToken = Prefs.getString(getApplicationContext(), "accessToken", "no");
        new AsyncBackup(DropboxClientFactory.getClient(accessToken), list).execute();
    }

    @OnClick(R.id.restore)
    public void onClickRestore() {
        linearLayoutBackup.setEnabled(false);
        linearLayoutBackup.setAlpha(0.6f);
        String accessToken = Prefs.getString(getApplicationContext(), "accessToken", "no");
        new AsyncRestore(DropboxClientFactory.getClient(accessToken)).execute();
    }

    @OnClick(R.id.auth)
    public void onClickSync() {
        Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.APP_KEY));
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        String accessToken = Auth.getOAuth2Token(); //generate Access Token
        Log.d(TAG, "onResume: accessToken = " + accessToken);

        if (accessToken != null) {
            //Store accessToken in SharedPreferences
            Prefs.saveToPrefs(getApplicationContext(), "accessToken", accessToken);
            parent.setVisibility(View.VISIBLE);

            String backupDate = Prefs.getString(getApplicationContext(), "backup_date", "no");

            if (!backupDate.equals("no")) {
                textViewLastBackup.setText("Last backup: " + DateUtils.getFullTime(Long.valueOf(backupDate)));
                textViewLastBackupStatus.setText("Click to back up anytime");
            } else {
                textViewLastBackup.setText("Click to backup");
                textViewLastBackupStatus.setText("Your data will be backed up on Dropbox");
            }

            String restoredDate = Prefs.getString(getApplicationContext(), "restoredDate", "no");

            if (!restoredDate.equals("no")) {
                textViewLastRestored.setText("Last restored: " + DateUtils.getFullTime(Long.valueOf(restoredDate)));
                textViewLastRestoredStatus.setText("Click to restore anytime");
            } else {
                textViewLastRestored.setText("Click to restore");
                textViewLastRestoredStatus.setText("Your data will be restored from Dropbox");
            }

            buttonAuth.setVisibility(View.GONE);
        } else {
            accessToken = Prefs.getString(getApplicationContext(), "accessToken", "no");
            if (accessToken.equals("no")) {
                parent.setVisibility(View.GONE);
                buttonAuth.setVisibility(View.VISIBLE);
            }
        }
    }

    private class AsyncBackup extends AsyncTask<Object, Object, Object> {

        private DbxClientV2 dbxClient;
        private List<File> files;

        public AsyncBackup(DbxClientV2 dbxClient, List<File> files) {
            this.dbxClient = dbxClient;
            this.files = files;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textViewLastBackup.setText("Backing up ...");
            textViewLastBackupStatus.setText("Uploading flashcards database");
        }

        @Override
        protected Object doInBackground(Object... objects) {

            for (int i = 0; i < files.size(); i++) {
                //upload to Dropbox
                try {
                    InputStream inputStream = new FileInputStream(files.get(i));

                    dbxClient.files().uploadBuilder("/" + files.get(i).getName())
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(inputStream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DbxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            Prefs.saveToPrefs(getApplicationContext(), "backup_date", System.currentTimeMillis() + "");
            textViewLastBackup.setText("Last backup: " + DateUtils.getFullTime(Long.valueOf(System.currentTimeMillis())));
            textViewLastBackupStatus.setText("Click to backup anytime");
            new AsyncBackupImages(dbxClient).execute();
        }
    }

    private class AsyncBackupImages extends AsyncTask {

        private DbxClientV2 dbxClient;

        public AsyncBackupImages(DbxClientV2 dbxClient) {
            this.dbxClient = dbxClient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textViewLastBackup.setText("Backing up ...");
            textViewLastBackupStatus.setText("Uploading images, please wait");
        }

        @Override
        protected String doInBackground(Object... objects) {

            List<Card> cards = CardDb.getInstance(getApplicationContext()).getAllCards();
            Card[] cardsArray = new Card[cards.size()];
            for (int i = 0; i < cards.size(); i++) {
                cardsArray[i] = cards.get(i);
            }

            List<File> files = new ArrayList<>();

            for (Card card : cards) {

                String frontImage = card.getFrontImage();
                String backImage = card.getBackImage();

                if (!frontImage.equals("no")) {
                    File f = new File(card.getFrontPath(), card.getFrontImage());
                    files.add(f);
                }
                if (!backImage.equals("no")) {
                    File f = new File(card.getBackPath(), card.getBackImage());
                    files.add(f);
                }
            }

            String rev = "";

            for (int i = 0; i < files.size(); i++) {
                //upload to Dropbox
                try {
                    InputStream inputStream = new FileInputStream(files.get(i));

                    dbxClient.files().uploadBuilder("/images/" + files.get(i).getName())
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(inputStream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DbxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return rev;
        }

        @Override
        protected void onPostExecute(Object s) {
            super.onPostExecute(s);
            Prefs.saveToPrefs(getApplicationContext(), "backup_date", System.currentTimeMillis() + "");
            textViewLastBackup.setText("Last backup: " + DateUtils.getFullTime(Long.valueOf(System.currentTimeMillis())));
            textViewLastBackupStatus.setText("Click to backup anytime");
            linearLayoutRestore.setEnabled(true);
            linearLayoutRestore.setAlpha(1.0f);
        }
    }

    private class AsyncRestore extends AsyncTask<Object, Object, List<File>> {

        private DbxClientV2 dbxClient;

        public AsyncRestore(DbxClientV2 dbxClient) {
            this.dbxClient = dbxClient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textViewLastRestored.setText("Restoring ...");
            textViewLastRestoredStatus.setText("Downloading flashcards database");
        }

        @Override
        protected List<File> doInBackground(Object... objects) {

            File cardsDb = getApplicationContext().getDatabasePath(CardDb.DATABASE_NAME);
            File flashcardsDb = getApplicationContext().getDatabasePath(FlashcardDb.DATABASE_NAME);
            File labelsDb = getApplicationContext().getDatabasePath(LabelsDb.DATABASE_NAME);
            File statsDb = getApplicationContext().getDatabasePath(StatsDb.DATABASE_NAME);
            File trashDb = getApplicationContext().getDatabasePath(TrashFlashcardDb.DATABASE_NAME);

            // Download the cardsDb.
            OutputStream outputStreamCards = null;
            try {
                outputStreamCards = new FileOutputStream(cardsDb);
                dbxClient.files().download("/" + CardDb.DATABASE_NAME)
                        .download(outputStreamCards);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Download the flashcardsDb.
            OutputStream outputStreamFlashcards = null;
            try {
                outputStreamFlashcards = new FileOutputStream(flashcardsDb);
                dbxClient.files().download("/" + FlashcardDb.DATABASE_NAME)
                        .download(outputStreamFlashcards);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Download the labels.
            OutputStream outputStreamLabels = null;
            try {
                outputStreamLabels = new FileOutputStream(labelsDb);
                dbxClient.files().download("/" + LabelsDb.DATABASE_NAME)
                        .download(outputStreamLabels);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Download the statsDb.
            OutputStream outputStreamStats = null;
            try {
                outputStreamStats = new FileOutputStream(statsDb);
                dbxClient.files().download("/" + StatsDb.DATABASE_NAME)
                        .download(outputStreamStats);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Download the trash ;-).
            OutputStream outputStreamTrash = null;
            try {
                outputStreamTrash = new FileOutputStream(trashDb);
                dbxClient.files().download("/" + TrashFlashcardDb.DATABASE_NAME)
                        .download(outputStreamTrash);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            super.onPostExecute(files);
            Prefs.saveToPrefs(getApplicationContext(), "restoredDate", System.currentTimeMillis() + "");
            textViewLastRestored.setText("Last restored: " + DateUtils.getFullTime(Long.valueOf(System.currentTimeMillis())));
            textViewLastRestoredStatus.setText("Click to restore anytime");

            new AsyncRestoreImages(dbxClient).execute();
        }
    }

    private class AsyncRestoreImages extends AsyncTask {

        private DbxClientV2 dbxClient;

        public AsyncRestoreImages(DbxClientV2 dbxClient) {
            this.dbxClient = dbxClient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textViewLastRestored.setText("Restoring ...");
            textViewLastRestoredStatus.setText("Downloading flashcards images");
        }

        @Override
        protected String doInBackground(Object... objects) {

            //download images
            try {
                List<Metadata> images = dbxClient.files().listFolder("/images").getEntries();
                for (int i = 0; i < images.size(); i++) {
                    Log.d(TAG, "DR IMAGES: " + images.get(i).toString());

                    // Download the image

                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    // path to /data/data/yourapp/app_data/imageDir
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    // Create imageDir
                    File mypath = new File(directory, images.get(i).getName());

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mypath);
                        dbxClient.files().download("/images/" + images.get(i).getName())
                                .download(fos);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DbxException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } catch (DbxException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object s) {
            super.onPostExecute(s);
            Prefs.saveToPrefs(getApplicationContext(), "restoredDate", System.currentTimeMillis() + "");
            textViewLastRestored.setText("Last restored: " + DateUtils.getFullTime(Long.valueOf(System.currentTimeMillis())));
            textViewLastRestoredStatus.setText("Click to restore anytime");
            linearLayoutBackup.setEnabled(true);
            linearLayoutBackup.setAlpha(1.0f);
        }
    }

}
