package com.andnet.gazeta.Databases;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class GazetaOpenHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER = " INTEGER";

    private static final String COMMA = ", ";

    //create key catch table
    private static final String CREATE_KEY_CATCH_TABLE = "CREATE TABLE "
            + DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME + " ("
            + DatabaseDescription.CACHED_KEY_TABLE.KEY+ TEXT_TYPE + COMMA
            + DatabaseDescription.CACHED_KEY_TABLE.CAT + TEXT_TYPE + " )";


    //create body table
    private static final String CREATE_BODY_TABLE = "CREATE TABLE "
            + DatabaseDescription.BODY_TABLE.TABLE_NAME + " ("
            + DatabaseDescription.BODY_TABLE.key + TEXT_TYPE + COMMA
            + DatabaseDescription.BODY_TABLE.body + TEXT_TYPE + " )";


    //history table
    private static final String CREATE_SEARCH_HISTORY_TABLE = "CREATE TABLE "
            + DatabaseDescription.SEARCH_HISTORY.TABLE_NAME + " ("
            + DatabaseDescription.SEARCH_HISTORY.SEARCH_TERM + TEXT_TYPE + COMMA
            + DatabaseDescription.SEARCH_HISTORY.TIME_STAMP + INTEGER + " )";
    //saved news table
    private static final String CREATE_NEWS_DATABASE = "CREATE TABLE "
            + DatabaseDescription.SAVED_NEWS.TABLE_NAME + " ("
            + DatabaseDescription.SAVED_NEWS.COVER_VIDEO + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.TITLE + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.KEY + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.IMAGE + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.COVER_AUDIO + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.O_COVER_PREVIEW + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.DATE + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.SYNOP + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.AUTHOR + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.IS_ALLOWED + INTEGER + COMMA
            + DatabaseDescription.SAVED_NEWS.COVER_IMAGE + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.TIME_STAMP + INTEGER + COMMA
            + DatabaseDescription.SAVED_NEWS.SOURCE_LINK + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.NEWS_LINK + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.SOURCE_LOGO + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.ORIGINAL_IMAGE + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.COVER_Y_EMBED + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.O_COVER_A_PREVIEW + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.O_COVER_V_PREVIEW + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.COVER_CAPTION + TEXT_TYPE + COMMA
            + DatabaseDescription.SAVED_NEWS.SOURCE_NAME + TEXT_TYPE + " )";

    public static final String DROP_NEWS_TABLE = "DROP TABLE IF EXISTS "+ DatabaseDescription.SAVED_NEWS.TABLE_NAME;
    public static final String DROP_SEARCH_HISTORY_TABLE = "DROP TABLE IF EXISTS "+ DatabaseDescription.SEARCH_HISTORY.TABLE_NAME;
    public static final String DROP_SEARCH_BODY_TABLE = "DROP TABLE IF EXISTS "+ DatabaseDescription.BODY_TABLE.TABLE_NAME;
    public static final String DROP_CATCH_KEY_TABLE = "DROP TABLE IF EXISTS "+ DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME;



    public GazetaOpenHelper(Context context) {
        super(context, DatabaseDescription.LIBRARY_DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_NEWS_DATABASE);
        db.execSQL(CREATE_SEARCH_HISTORY_TABLE);
        db.execSQL(CREATE_BODY_TABLE);
        db.execSQL(CREATE_KEY_CATCH_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_NEWS_TABLE);
        db.execSQL(DROP_SEARCH_HISTORY_TABLE);
        db.execSQL(DROP_SEARCH_BODY_TABLE);
        db.execSQL(DROP_CATCH_KEY_TABLE);
        onCreate(db);
    }
}
