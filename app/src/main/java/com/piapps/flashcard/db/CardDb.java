package com.piapps.flashcard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.piapps.flashcard.model.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abduaziz on 2/18/17.
 */

public class CardDb extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "cardsManager";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Contacts table name
    private static final String TABLE_CARDS = "cards";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SET_ID = "set_id";
    private static final String KEY_FRONT = "front";
    private static final String KEY_BACK = "back";
    private static final String KEY_COLOR = "color";
    private static final String KEY_FRONT_IMAGE = "front_image";
    private static final String KEY_BACK_IMAGE = "back_image";
    private static final String KEY_FRONT_PATH = "front_path";
    private static final String KEY_BACK_PATH = "back_path";
    private static CardDb instance;


    private CardDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static CardDb getInstance(Context context) {
        if (instance == null) {
            instance = new CardDb(context);
        }
        return instance;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_CARDS +
                        "("
                        + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_SET_ID + " TEXT,"
                        + KEY_FRONT + " TEXT,"
                        + KEY_BACK + " TEXT,"
                        + KEY_COLOR + " TEXT,"
                        + KEY_FRONT_IMAGE + " TEXT,"
                        + KEY_BACK_IMAGE + " TEXT,"
                        + KEY_FRONT_PATH + " TEXT,"
                        + KEY_BACK_PATH + " TEXT" +
                        ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        // Create tables again
        onCreate(db);
    }

    //all CRUDs
    // Adding new card

    public void addCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, card.getId());
        values.put(KEY_SET_ID, card.getSetId());
        values.put(KEY_FRONT, card.getFront());
        values.put(KEY_BACK, card.getBack());
        values.put(KEY_COLOR, card.getColor() + "");
        values.put(KEY_FRONT_IMAGE, card.getFrontImage());
        values.put(KEY_BACK_IMAGE, card.getBackImage());
        values.put(KEY_FRONT_PATH, card.getFrontPath());
        values.put(KEY_BACK_PATH, card.getBackPath());

        // Inserting Row
        db.insert(TABLE_CARDS, null, values);
        db.close(); // Closing database connection
    }

    public Card getCard(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CARDS,
                new String[]{KEY_ID, KEY_SET_ID, KEY_FRONT,
                        KEY_BACK, KEY_COLOR, KEY_FRONT_IMAGE,
                        KEY_BACK_IMAGE, KEY_FRONT_PATH, KEY_BACK_PATH}, KEY_ID + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Card flashcard = null;
        if (cursor.getCount() > 0)
            flashcard = new Card(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8));
        // return flashcard
        cursor.close();
        return flashcard;
    }


    public List<Card> getAllCards() {
        List<Card> cardList = new ArrayList<>();
        //select all contacts
        String selectAll = "SELECT  * FROM " + TABLE_CARDS;
        //db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setId(cursor.getString(0));
                card.setSetId(cursor.getString(1));
                card.setFront(cursor.getString(2));
                card.setBack(cursor.getString(3));
                card.setColor(cursor.getString(4));
                card.setFrontImage(cursor.getString(5));
                card.setBackImage(cursor.getString(6));
                card.setFrontPath(cursor.getString(7));
                card.setBackPath(cursor.getString(8));
                // Adding card to list
                cardList.add(card);
            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        return cardList;
    }

    public List<Card> getSetCards(String setId) {
        List<Card> cardList = new ArrayList<Card>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CARDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CARDS,
                new String[]{KEY_ID, KEY_SET_ID, KEY_FRONT, KEY_BACK, KEY_COLOR, KEY_FRONT_IMAGE, KEY_BACK_IMAGE, KEY_FRONT_PATH, KEY_BACK_PATH},
                KEY_SET_ID + "=?",
                new String[]{setId}, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setId(cursor.getString(0));
                card.setSetId(cursor.getString(1));
                card.setFront(cursor.getString(2));
                card.setBack(cursor.getString(3));
                card.setColor(cursor.getString(4));
                card.setFrontImage(cursor.getString(5));
                card.setBackImage(cursor.getString(6));
                card.setFrontPath(cursor.getString(7));
                card.setBackPath(cursor.getString(8));
                // Adding card to list
                cardList.add(card);
            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        return cardList;
    }

    // Updating single card
    public int updateCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SET_ID, card.getSetId());
        values.put(KEY_FRONT, card.getFront());
        values.put(KEY_BACK, card.getBack());
        values.put(KEY_COLOR, card.getColor() + "");
        values.put(KEY_FRONT_IMAGE, card.getFrontImage());
        values.put(KEY_BACK_IMAGE, card.getBackImage());
        values.put(KEY_FRONT_PATH, card.getFrontPath());
        values.put(KEY_BACK_PATH, card.getBackPath());

        // updating row
        return db.update(TABLE_CARDS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(card.getId())});
    }

    // Deleting single card
    public void deleteCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARDS, KEY_ID + " = ?",
                new String[]{String.valueOf(card.getId())});
        db.close();
    }

}
