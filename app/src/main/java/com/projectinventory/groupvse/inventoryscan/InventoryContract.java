package com.projectinventory.groupvse.inventoryscan;

import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract() {}

    /* Inner class that defines the table contents */
    public static class InventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "Inventory";
        public static final String COLUMN_NAME_STATION = "station";
        public static final String COLUMN_NAME_BUILDING = "building";
        public static final String COLUMN_NAME_ROOM = "room";
        public static final String COLUMN_NAME_SERIALNR = "serialnr";

    }

}
