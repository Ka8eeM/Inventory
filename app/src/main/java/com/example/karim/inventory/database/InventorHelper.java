package com.example.karim.inventory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.karim.inventory.database.InventoryContract.InventoryEntry;

public class InventorHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventorDB.db";

    public InventorHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTOR_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME +
                "(" + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_INVENTORY_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_INVENTORY_PRODUCT_PICTURE + " BLOB, "
                + InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_INVENTORY_SUPPLIERS + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_INVENTOR_TABLE);
        Log.e("InventorHelper", "Inventor Helper created database successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME + ";";
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}
