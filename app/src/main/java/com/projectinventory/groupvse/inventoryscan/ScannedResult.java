package com.projectinventory.groupvse.inventoryscan;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class ScannedResult extends AppCompatActivity {
    InventoryDbHelper mDbHelper;
    SQLiteDatabase db;
    Intent mIntent;
    ArrayList<String> Items = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_result);

        mDbHelper = new InventoryDbHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        mIntent = getIntent();
        //add all Strings of Bundle into one ArrayList
        //Items.addAll(mIntent.getBundleExtra());

        //show all items
    }

    public void addItem(View view) {
        //finish and give bundle back and info to scan 1 more item
        save();
        Intent data = new Intent();
        data.putExtra("clicked", "ADD");
       // data.putExtra(mIntent); give bundle back @todo
        setResult(RESULT_OK, data);
        finish();
    }

    public void nextStation(View view) {
        //save and finish, info to scan 3 new items
        save();
        Intent data = new Intent();
        data.putExtra("clicked", "NEXT");
        setResult(RESULT_OK, data);
        finish();
    }

    public void doneWithIt(View view) {
        //finish after saving to database
        save();
        Intent data = new Intent();
        data.putExtra("clicked", "DONE");
        setResult(RESULT_OK, data);
        finish();

    }

    private void save(){
        for(int i = 0; i < Items.size(); i++) { // save each entry in the bundle
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING, Items.get(i).substring(0,3));
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING, Items.get(i).substring(5,7));
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING, Items.get(i).substring(9,11));
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING, Items.get(i).substring(13));

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        }
    }
}
