package com.piapps.flashcard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.piapps.flashcard.model.Stats;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abduaziz on 2/18/17.
 */

public class StatsDb extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "statsManager";

    // Contacts table name
    private static final String TABLE_STATS = "stats";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TRUE_ANSWERS = "trueanswers";
    private static final String KEY_TIME_SPENT = "timespent";
    private static final String KEY_DATE = "label";

    public StatsDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_STATS +
                        "("
                        + KEY_ID + " TEXT,"
                        + KEY_TRUE_ANSWERS + " TEXT,"
                        + KEY_TIME_SPENT + " TEXT,"
                        + KEY_DATE + " TEXT" +
                        ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
        // Create tables again
        onCreate(db);
    }

    //all CRUDs
    // Adding new stats
    public void addStats(Stats stats) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, stats.getSetId());
        values.put(KEY_TRUE_ANSWERS, stats.getTrueAnswers());
        values.put(KEY_TIME_SPENT, stats.getTimeSpent());
        values.put(KEY_DATE, stats.getDate());

        if (!isThere(stats.getDate(), stats.getSetId())) {
            db.insert(TABLE_STATS, null, values);
            Log.d("STATS_DB", "addStats: inserted new stats");
        } else {
            for (int i = 0; i < getSetCards(stats.getSetId()).size(); i++) {
                String date1 = DateFormat.getDateInstance().format(new Date(Long.parseLong(stats.getDate()))).toString();
                String date2 = DateFormat.getDateInstance().format(new Date(Long.parseLong(getSetCards(stats.getSetId()).get(i).getDate()))).toString();
                if (date1.equals(date2)) {
                    int c1 = Integer.parseInt(stats.getTrueAnswers());
                    int c2 = Integer.parseInt(getSetCards(stats.getSetId()).get(i).getTrueAnswers());
                    long time = Long.parseLong(getSetCards(stats.getSetId()).get(i).getTimeSpent());
                    long newtime = Long.parseLong(stats.getTimeSpent()) + time;
                    stats.setTimeSpent(newtime + "");
                    stats.setTrueAnswers(getSetCards(stats.getSetId()).get(i).getTrueAnswers());
                    updateStatsTime(stats, getSetCards(stats.getSetId()).get(i).getDate());
                    Log.d("STATS_DB", "addStats: updated stats time");
                    if (c1 > c2) {
                        updateStats(c1, stats, getSetCards(stats.getSetId()).get(i).getDate());
                        Log.d("STATS_DB", "addStats: updatedStats: c1 = " + c1 + ", c2 = " + c2);
                        break;
                    }
                }
            }
        }

        db.close(); // Closing database connection
    }


    // Updating single flashcard
    public int updateStatsTime(Stats stats, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRUE_ANSWERS, stats.getTrueAnswers());
        values.put(KEY_TIME_SPENT, stats.getTimeSpent());

        // updating row
        return db.update(TABLE_STATS, values, KEY_DATE + " = ?",
                new String[]{date});
    }

    // Updating single flashcard
    public int updateStats(int c1, Stats stats, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRUE_ANSWERS, c1 + "");
        values.put(KEY_TIME_SPENT, stats.getTimeSpent());

        // updating row
        return db.update(TABLE_STATS, values, KEY_DATE + " = ?",
                new String[]{date});
    }

    public boolean isThere(String l1, String setId) {
        for (int i = 0; i < getSetCards(setId).size(); i++) {
            if (DateFormat.getDateInstance().format(new Date(Long.parseLong(l1))).toString().equals
                    (DateFormat.getDateInstance().format(new Date(Long.parseLong(getSetCards(setId).get(i).getDate()))).toString()))
                return true;
        }
        return false;
    }

    public List<Stats> getSetCards(String setId) {
        List<Stats> cardList = new ArrayList<Stats>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_STATS,
                new String[]{KEY_ID, KEY_TRUE_ANSWERS, KEY_TIME_SPENT, KEY_DATE},
                KEY_ID + "=?",
                new String[]{setId}, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Stats stats = new Stats();
                stats.setSetId(cursor.getString(0));
                stats.setTrueAnswers(cursor.getString(1));
                stats.setTimeSpent(cursor.getString(2));
                stats.setDate(cursor.getString(3));
                // Adding card to list
                cardList.add(stats);
            } while (cursor.moveToNext());
        }

        // return contact list
        return cardList;
    }

    // Getting flashcards count
    public int getStatsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_STATS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    // Deleting single contact
    public void deleteFlashcard(Stats stats) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STATS, KEY_DATE + " = ?",
                new String[]{String.valueOf(stats.getDate())});
        db.close();
    }

}
