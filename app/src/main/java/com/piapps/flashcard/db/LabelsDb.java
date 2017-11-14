package com.piapps.flashcard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abduaziz on 2/18/17.
 */

public class LabelsDb extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "labelsManager";
    private static final int DATABASE_VERSION = 1;
    // Contacts table name
    private static final String TABLE_LABELS = "labels";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "name";
    private static LabelsDb instance;

    private LabelsDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static LabelsDb getInstance(Context context) {
        if (instance == null)
            instance = new LabelsDb(context);
        return instance;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_LABELS +
                        "("
                        + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_TITLE + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABELS);
        // Create tables again
        onCreate(db);
    }

    //all CRUDs
    // Adding new flashcard
    public void addFlashcard(String label) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, label);

        // Inserting Row
        db.insert(TABLE_LABELS, null, values);
        db.close(); // Closing database connection
    }

    public List<String> getAllFlashcards() {
        List<String> flashcardList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LABELS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                flashcardList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return flashcardList;
    }
}
