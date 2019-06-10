package com.andnet.gazeta.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class KeyOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER = " INTEGER";

    private static final String COMMA = ", ";

    //create key catch table
    private static final String CREATE_KEY_CATCH_TABLE = "CREATE TABLE "
            + DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME + " ("
            + DatabaseDescription.CACHED_KEY_TABLE.KEY+ TEXT_TYPE + COMMA
            + DatabaseDescription.CACHED_KEY_TABLE.CAT + TEXT_TYPE + " )";
    //create source list table
    private static final String CREATE_SOURCE_BANNED_TABLE = "CREATE TABLE "
            + DatabaseDescription.SOURCE_TABLE.TABLE_NAME + " ("
            + DatabaseDescription.SOURCE_TABLE.NAME+ TEXT_TYPE + COMMA
            + DatabaseDescription.SOURCE_TABLE.BANNED + INTEGER + " )";

    public static final String DROP_CATCH_KEY_TABLE = "DROP TABLE IF EXISTS "+ DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME;
    public static final String DROP_BANNED_SOURCE_TABLE = "DROP TABLE IF EXISTS "+ DatabaseDescription.SOURCE_TABLE.TABLE_NAME;



    public KeyOpenHelper(Context context) {
        super(context, DatabaseDescription.KEY_DATABASE, null, DATABASE_VERSION);    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_KEY_CATCH_TABLE);
        db.execSQL(CREATE_SOURCE_BANNED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_CATCH_KEY_TABLE);
        db.execSQL(DROP_BANNED_SOURCE_TABLE);

        onCreate(db);

    }
}
