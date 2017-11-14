package com.piapps.flashcard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.piapps.flashcard.model.Flashcard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abduaziz on 2/18/17.
 */

public class TrashFlashcardDb extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "trashFlashcardsManager";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Contacts table name
    private static final String TABLE_FLASHCARDS = "trashFlashcards";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "name";
    private static final String KEY_COUNT = "count";
    private static final String KEY_LABEL = "label";
    private static final String KEY_COLOR = "color";
    private static final String KEY_USE_COUNT = "usecount";
    private static TrashFlashcardDb instance;

    private TrashFlashcardDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static TrashFlashcardDb getInstance(Context context) {
        if (instance == null)
            instance = new TrashFlashcardDb(context);
        return instance;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_FLASHCARDS +
                        "("
                        + KEY_ID + " TEXT,"
                        + KEY_TITLE + " TEXT,"
                        + KEY_COUNT + " TEXT,"
                        + KEY_LABEL + " TEXT,"
                        + KEY_COLOR + " TEXT,"
                        + KEY_USE_COUNT + " TEXT" +
                        ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARDS);
        // Create tables again
        onCreate(db);
    }

    //all CRUDs
    // Adding new flashcard
    public void addFlashcard(Flashcard flashcard) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, flashcard.getSetId());
        values.put(KEY_TITLE, flashcard.getTitle());
        values.put(KEY_COUNT, flashcard.getCount());
        values.put(KEY_LABEL, flashcard.getLabel());
        values.put(KEY_COLOR, flashcard.getColor());
        values.put(KEY_USE_COUNT, flashcard.getUseCount());

        boolean isThere = false;
        for (int i = 0; i < getAllFlashcards().size(); i++) {
            if (getAllFlashcards().get(i).getSetId().equals(flashcard.getSetId())) {
                isThere = true;
                break;
            }
        }
        // Inserting Row
        if (!isThere)
            db.insert(TABLE_FLASHCARDS, null, values);
        else
            updateFlashcard(flashcard);
        db.close(); // Closing database connection
    }

    public Flashcard getFlashcard(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FLASHCARDS,
                new String[]{KEY_ID, KEY_TITLE, KEY_COUNT, KEY_LABEL, KEY_COLOR, KEY_USE_COUNT}, KEY_ID + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();


        Flashcard flashcard = null;
        if (cursor != null)
            flashcard = new Flashcard(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5));

        cursor.close();
        // return flashcard
        return flashcard;
    }

    public List<Flashcard> getAllFlashcards() {
        List<Flashcard> flashcardList = new ArrayList<Flashcard>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FLASHCARDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Flashcard flashcard = new Flashcard();
                flashcard.setSetId(cursor.getString(0));
                flashcard.setTitle(cursor.getString(1));
                flashcard.setCount(cursor.getString(2));
                flashcard.setLabel(cursor.getString(3));
                flashcard.setColor(cursor.getString(4));
                flashcard.setUseCount(cursor.getString(5));
                // Adding flashcard to list
                flashcardList.add(flashcard);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return flashcardList;
    }

    // Getting flashcards count
    public int getFlashcardsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FLASHCARDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    // Updating single flashcard
    public int updateFlashcard(Flashcard contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, contact.getTitle());
        values.put(KEY_COUNT, contact.getCount());
        values.put(KEY_LABEL, contact.getLabel());
        values.put(KEY_COLOR, contact.getColor());
        values.put(KEY_USE_COUNT, contact.getUseCount());

        // updating row
        return db.update(TABLE_FLASHCARDS, values, KEY_ID + " = ?",
                new String[]{contact.getSetId()});
    }

    // Deleting single contact
    public void deleteFlashcard(Flashcard contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLASHCARDS, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getSetId())});
        db.close();
    }

}
