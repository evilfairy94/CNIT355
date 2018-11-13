package com.projectinventory.groupvse.inventoryscan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " (" +
                    InventoryContract.InventoryEntry._ID + "INTEGER PRIMARY KEY," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_STATION + " TEXT," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING + " TEXT," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_ROOM + " TEXT," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR + " TEXT," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_STATIONFLAG + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryContract.InventoryEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Inventory.db";

        public InventoryDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
}
