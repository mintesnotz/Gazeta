package com.andnet.gazeta.Databases;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.andnet.gazeta.R;

public class KeyContentProvider extends ContentProvider {


    private KeyOpenHelper keyOpenHelper;
    private static final UriMatcher uriMatcher =new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SINGLE_ITEM_KEY = 1;
    private static final int ITEMS_KEY= 2;
    private static final int SINGLE_ITEM_SOURCE=3;
    private static final int ITEM_SOURCE=4;

    static {

        uriMatcher.addURI(DatabaseDescription.AUTHORITY_KEY, DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME + "/#", SINGLE_ITEM_KEY);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY_KEY, DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME, ITEMS_KEY);

        uriMatcher.addURI(DatabaseDescription.AUTHORITY_KEY, DatabaseDescription.SOURCE_TABLE.TABLE_NAME + "/#", SINGLE_ITEM_SOURCE);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY_KEY, DatabaseDescription.SOURCE_TABLE.TABLE_NAME, ITEM_SOURCE);
    }



    public KeyContentProvider() {
    }



    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int numberOfRowsDeleted;
        switch (uriMatcher.match(uri)){
            case SINGLE_ITEM_KEY:
                numberOfRowsDeleted = keyOpenHelper.getWritableDatabase().delete(
                        DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME, DatabaseDescription.CACHED_KEY_TABLE._ID + "= ?", new String[]{uri.getLastPathSegment()});
                break;
            case ITEMS_KEY:
                numberOfRowsDeleted=keyOpenHelper.getWritableDatabase().delete(DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME,null,null);
                break;
            case SINGLE_ITEM_SOURCE:
                numberOfRowsDeleted = keyOpenHelper.getWritableDatabase().delete(
                        DatabaseDescription.SOURCE_TABLE.TABLE_NAME, DatabaseDescription.SOURCE_TABLE._ID + "= ?", new String[]{uri.getLastPathSegment()});
                break;
            case ITEM_SOURCE:
                numberOfRowsDeleted=keyOpenHelper.getWritableDatabase().delete(DatabaseDescription.SOURCE_TABLE.TABLE_NAME,null,null);
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


    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Uri newContactUri = null;
        long rowId;

        switch (uriMatcher.match(uri)){
            case ITEMS_KEY:
                rowId = keyOpenHelper.getWritableDatabase().insert(
                        DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME, null, values);
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.CACHED_KEY_TABLE.buildContactUriForeId(rowId);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);

                break;
            case ITEM_SOURCE:
                rowId = keyOpenHelper.getWritableDatabase().insert(
                        DatabaseDescription.SOURCE_TABLE.TABLE_NAME, null, values);
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.SOURCE_TABLE.buildContactUriForeId(rowId);
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
        keyOpenHelper =new KeyOpenHelper(getContext());
        return true;
    }


    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case SINGLE_ITEM_KEY:
                queryBuilder.setTables(DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME);
                queryBuilder.appendWhere(
                        DatabaseDescription.CACHED_KEY_TABLE._ID + "=" + uri.getLastPathSegment());
                break;
            case ITEMS_KEY:
                queryBuilder.setTables(DatabaseDescription.CACHED_KEY_TABLE.TABLE_NAME);
                break;
            case SINGLE_ITEM_SOURCE:
                queryBuilder.setTables(DatabaseDescription.SOURCE_TABLE.TABLE_NAME);
                queryBuilder.appendWhere(
                        DatabaseDescription.SOURCE_TABLE._ID + "=" + uri.getLastPathSegment());
                break;
            case ITEM_SOURCE:
                queryBuilder.setTables(DatabaseDescription.SOURCE_TABLE.TABLE_NAME);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        Cursor cursor = queryBuilder.query(keyOpenHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
