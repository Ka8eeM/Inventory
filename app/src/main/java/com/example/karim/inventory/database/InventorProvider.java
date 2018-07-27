package com.example.karim.inventory.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import static com.example.karim.inventory.database.InventoryContract.CONTENT_AUTHORITY;
import static com.example.karim.inventory.database.InventoryContract.InventoryEntry;
import static com.example.karim.inventory.database.InventoryContract.PATH_INVENTORY;

public class InventorProvider extends ContentProvider {
    InventorHelper helper;
    public static final String LOG_TAG = InventorProvider.class.getSimpleName();
    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int INVENTOR = 10;
    public static final int INVENTOR_ID = 11;

    static {
        URI_MATCHER.addURI(CONTENT_AUTHORITY, PATH_INVENTORY, INVENTOR);
        URI_MATCHER.addURI(CONTENT_AUTHORITY, PATH_INVENTORY + "/#", INVENTOR_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new InventorHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case INVENTOR:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTOR_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                Log.e(LOG_TAG, "Unkown URI " + uri + " with match " + match);
                throw new IllegalArgumentException("Unkown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case INVENTOR:
                cursor = database.query(InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case INVENTOR_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Can not query unknown URI " + uri);
        }
        // notify for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(InventoryEntry.COLUMN_INVENTORY_NAME);
        Integer price = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_PRICE);
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY);
        String supplier = values.getAsString(InventoryEntry.COLUMN_INVENTORY_SUPPLIERS);
        byte[] pictureArray = values.getAsByteArray(InventoryEntry.COLUMN_INVENTORY_PRODUCT_PICTURE);
        //Bitmap productPicture = BitmapFactory.decodeByteArray(pictureArray, 0, pictureArray.length);

        boolean ok = false; // denote if save product works well
        long id = -1;
        // check for null values
        if (name != null && price != null && quantity != null && supplier != null) {
            SQLiteDatabase database = helper.getWritableDatabase();
            id = database.insert(InventoryEntry.TABLE_NAME, null, values);
            if (id != -1) {
                ok = true;
            }
        } else {
            Toast.makeText(getContext(), "Please fill all data, data can not be null", Toast.LENGTH_SHORT).show();
        }
        if (ok) {
            getContext().getContentResolver().notifyChange(uri, null);
            Log.e(LOG_TAG, "Complete insert data: " + uri);
            return ContentUris.withAppendedId(uri, id);
        }
        Log.e(LOG_TAG, "Failed to insert row for " + uri);
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case INVENTOR:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = URI_MATCHER.match(uri);
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (match) {
            case INVENTOR:
                int rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted > 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case INVENTOR_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if (rowDeleted > 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selections, String selectionsArgs[]) {
        if (values.size() == 0)
            return 0;
        Integer quantity;
        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY)) {
            quantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY);
            if (quantity == null) {
                Toast.makeText(getContext(), "Quantity can not be null", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selections, selectionsArgs);
        Log.e(InventorProvider.class.getSimpleName(), "rows updated : " + rowsUpdated);
        if (rowsUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case INVENTOR:
                return updateProduct(uri, values, selection, selectionArgs);
            case INVENTOR_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
}
