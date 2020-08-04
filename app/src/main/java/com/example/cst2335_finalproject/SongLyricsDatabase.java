package com.example.cst2335_finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongLyricsDatabase extends SQLiteOpenHelper{



        protected final static String DATABASE_NAME = "Message";
        protected final static int VERSION_NUM = 2;
        public final static String TABLE_NAME = "SONG";
        public final static String COL_ARTIST = "Artist";
        public final static String COL_TITLE = "Title";
        public final static String COL_ID = "_id";

        public SongLyricsDatabase(Context ctx)
        {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }


        //This function gets called if no database file exists.
        //Look on your device in the /data/data/package-name/database directory.
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_TITLE+" TEXT, "+COL_ARTIST+" TEXT);");
        }


        //this function gets called if the database version on your device is lower than VERSION_NUM
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {   //Drop the old table:
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

            //Create the new table:
            onCreate(db);
        }

        //this function gets called if the database version on your device is higher than VERSION_NUM
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {   //Drop the old table:
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

            //Create the new table:
            onCreate(db);
        }


    }


