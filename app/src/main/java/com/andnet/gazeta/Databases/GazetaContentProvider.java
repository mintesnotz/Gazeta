package com.andnet.gazeta.Databases;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.andnet.gazeta.R;

public class GazetaContentProvider extends ContentProvider {

    private GazetaOpenHelper gazetaOpenHelper;
    private static final UriMatcher uriMatcher =new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SINGLE_ITEM_SAVED_NEWS =1;
    private static final int ITEMS_SAVED_NEWS= 2;
    private static final int SINGLE_ITEM__HISTORY = 3;
    private static final int ITEMS_HISTORY= 4;
    private static final int ITEMS_BODY=5;
    private static final int SINGLE_ITEM_BODY=6;
    private static final int ITEM_CATCH_KEY=7;
    private static final int SINGLE_ITEM_CATCH_KEY=8;


    static {


        //for saved news table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.SAVED_NEWS.TABLE_NAME + "/#", SINGLE_ITEM_SAVED_NEWS);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.SAVED_NEWS.TABLE_NAME, ITEMS_SAVED_NEWS);

        //for history table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.SEARCH_HISTORY.TABLE_NAME + "/#", SINGLE_ITEM__HISTORY);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.SEARCH_HISTORY.TABLE_NAME, ITEMS_HISTORY);

        //for body table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.BODY_TABLE.TABLE_NAME + "/#", SINGLE_ITEM_BODY);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.BODY_TABLE.TABLE_NAME, ITEMS_BODY);

        //for body table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME + "/#", SINGLE_ITEM_CATCH_KEY);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME, ITEM_CATCH_KEY);
    }


    public GazetaContentProvider() {
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;
        switch (uriMatcher.match(uri)){
            case SINGLE_ITEM_SAVED_NEWS:
                numberOfRowsDeleted = gazetaOpenHelper.getWritableDatabase().delete(
                        DatabaseDescription.SAVED_NEWS.TABLE_NAME, DatabaseDescription.SAVED_NEWS._ID + "= ?", new String[]{uri.getLastPathSegment()});
                break;
            case ITEMS_SAVED_NEWS:
                numberOfRowsDeleted=gazetaOpenHelper.getWritableDatabase().delete(DatabaseDescription.SAVED_NEWS.TABLE_NAME,null,null);
                break;
            case SINGLE_ITEM__HISTORY:
                numberOfRowsDeleted = gazetaOpenHelper.getWritableDatabase().delete(
                        DatabaseDescription.SEARCH_HISTORY.TABLE_NAME, DatabaseDescription.SEARCH_HISTORY._ID + "= ?", new String[]{uri.getLastPathSegment()});
                break;
            case ITEMS_HISTORY:
                numberOfRowsDeleted=gazetaOpenHelper.getWritableDatabase().delete(DatabaseDescription.SEARCH_HISTORY.TABLE_NAME,null,null);
                break;
            case SINGLE_ITEM_BODY:
                numberOfRowsDeleted = gazetaOpenHelper.getWritableDatabase().delete(
                        DatabaseDescription.BODY_TABLE.TABLE_NAME, DatabaseDescription.BODY_TABLE._ID + "= ?", new String[]{uri.getLastPathSegment()});
                break;
            case ITEMS_BODY:
                numberOfRowsDeleted=gazetaOpenHelper.getWritableDatabase().delete(DatabaseDescription.BODY_TABLE.TABLE_NAME,null,null);
                break;
            case SINGLE_ITEM_CATCH_KEY:
                numberOfRowsDeleted = gazetaOpenHelper.getWritableDatabase().delete(
                        DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME, DatabaseDescription.CACHED_KEY_TABLE._ID + "= ?", new String[]{uri.getLastPathSegment()});
                break;
            case ITEM_CATCH_KEY:
                numberOfRowsDeleted=gazetaOpenHelper.getWritableDatabase().delete(DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME,null,null);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        if (numberOfRowsDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri newContactUri = null;
        long rowId;

        switch (uriMatcher.match(uri)){
            case ITEMS_SAVED_NEWS:

                rowId = gazetaOpenHelper.getWritableDatabase().insert(
                        DatabaseDescription.SAVED_NEWS.TABLE_NAME, null, values);
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.SAVED_NEWS.buildContactUriForeId(rowId);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);

                break;
            case ITEMS_HISTORY:
                rowId = gazetaOpenHelper.getWritableDatabase().insert(
                        DatabaseDescription.SEARCH_HISTORY.TABLE_NAME, null, values);
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.SEARCH_HISTORY.buildContactUriForeId(rowId);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);

                break;
            case ITEMS_BODY:
                rowId = gazetaOpenHelper.getWritableDatabase().insert(
                        DatabaseDescription.BODY_TABLE.TABLE_NAME, null, values);
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.BODY_TABLE.buildContactUriForeId(rowId);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);

                break;
            case ITEM_CATCH_KEY:
                rowId = gazetaOpenHelper.getWritableDatabase().insert(
                        DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME, null, values);
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.CACHED_KEY_TABLE.buildContactUriForeId(rowId);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);

                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }


        return newContactUri;
    }


    @Override
    public boolean onCreate() {
        gazetaOpenHelper =new GazetaOpenHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){

        case SINGLE_ITEM_SAVED_NEWS:
            queryBuilder.setTables(DatabaseDescription.SAVED_NEWS.TABLE_NAME);
            queryBuilder.appendWhere(
                    DatabaseDescription.SAVED_NEWS._ID + "=" + uri.getLastPathSegment());
            break;
        case ITEMS_SAVED_NEWS:
            queryBuilder.setTables(DatabaseDescription.SAVED_NEWS.TABLE_NAME);
            break;
        case SINGLE_ITEM__HISTORY:
            queryBuilder.setTables(DatabaseDescription.SEARCH_HISTORY.TABLE_NAME);
            queryBuilder.appendWhere(
                    DatabaseDescription.SEARCH_HISTORY._ID + "=" + uri.getLastPathSegment());
            break;
        case ITEMS_HISTORY:
            queryBuilder.setTables(DatabaseDescription.SEARCH_HISTORY.TABLE_NAME);
            break;
            case SINGLE_ITEM_BODY:
                queryBuilder.setTables(DatabaseDescription.BODY_TABLE.TABLE_NAME);
                queryBuilder.appendWhere(
                        DatabaseDescription.BODY_TABLE._ID + "=" + uri.getLastPathSegment());
                break;
            case ITEMS_BODY:
                queryBuilder.setTables(DatabaseDescription.BODY_TABLE.TABLE_NAME);
                break;
            case SINGLE_ITEM_CATCH_KEY:
                queryBuilder.setTables(DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME);
                queryBuilder.appendWhere(
                        DatabaseDescription.CACHED_KEY_TABLE._ID + "=" + uri.getLastPathSegment());
                break;
            case ITEM_CATCH_KEY:
                queryBuilder.setTables(DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME);
                break;
        default:
            throw new UnsupportedOperationException(
                    getContext().getString(R.string.invalid_delete_uri) + uri);
    }

        Cursor cursor = queryBuilder.query(gazetaOpenHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int numberOfRowsUpdated;//1 if update successful; 0 otherwise

       switch (uriMatcher.match(uri)){

        case SINGLE_ITEM_SAVED_NEWS:
            numberOfRowsUpdated = gazetaOpenHelper.getWritableDatabase().update(
                    DatabaseDescription.SAVED_NEWS.TABLE_NAME, values, DatabaseDescription.SAVED_NEWS.TABLE_NAME + "=" + uri.getLastPathSegment(),
                    selectionArgs);
            break;

        case SINGLE_ITEM__HISTORY:
            numberOfRowsUpdated = gazetaOpenHelper.getWritableDatabase().update(
                    DatabaseDescription.SEARCH_HISTORY.TABLE_NAME, values, DatabaseDescription.SEARCH_HISTORY.TABLE_NAME + "=" + uri.getLastPathSegment(),
                    selectionArgs);
            break;
           case SINGLE_ITEM_BODY:
               numberOfRowsUpdated = gazetaOpenHelper.getWritableDatabase().update(
                       DatabaseDescription.BODY_TABLE.TABLE_NAME, values, DatabaseDescription.BODY_TABLE.TABLE_NAME + "=" + uri.getLastPathSegment(),
                       selectionArgs);
               break;
           case SINGLE_ITEM_CATCH_KEY:
               numberOfRowsUpdated = gazetaOpenHelper.getWritableDatabase().update(
                       DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME, values, DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME + "=" + uri.getLastPathSegment(),
                       selectionArgs);
               break;
        default:
            throw new UnsupportedOperationException(
                    getContext().getString(R.string.invalid_delete_uri) + uri);
    }


        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

      return   numberOfRowsUpdated;
    }


}
