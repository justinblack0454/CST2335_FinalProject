package com.example.cst2335_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.cst2335_finalproject.SoccerActivity.Match;

import java.util.ArrayList;

public class MySoccerOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "FavouritesDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "Favourites";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_DATE = "DATE";
    public final static String COL_ID = "_id";
    public final static String COL_TEAM_1 = "TEAM1";
    public final static String COL_TEAM_2 = "TEAM2";
    public final static String COL_URL = "URL";

    public final String[] columns = {COL_ID, COL_TITLE, COL_DATE, COL_TEAM_1, COL_TEAM_2, COL_URL};

    public MySoccerOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    public ArrayList<Match> getAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Match> favouriteMatches = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, columns, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            favouriteMatches.add(new Match(c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)));
            c.moveToNext();
        }
        return favouriteMatches;
    }

    public void add(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    public void removeMatch(Match match) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,  COL_ID + "= ?" + match.getId(), null);

    }
    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " text,"
                + COL_DATE + " text,"
                + COL_TEAM_1 + " text,"
                + COL_TEAM_2 + " text,"
                + COL_URL + " text);");  // add or remove columns
    }
    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }
    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the new table:
        onCreate(db);
    }
}