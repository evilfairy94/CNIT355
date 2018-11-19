package com.projectinventory.groupvse.inventoryscan;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ScannedResult extends AppCompatActivity {
    InventoryDbHelper mDbHelper;
    SQLiteDatabase db;
    Intent mIntent;
    ArrayList<String> Items = new ArrayList<String>();
    String input, station, building, room;
    TextView i1, i2, i3, i4, i5, i6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_result);

        mDbHelper = new InventoryDbHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        mIntent = getIntent();
        //add all Strings of Intent into one ArrayList
        input = mIntent.getStringExtra("Station");
        Items.addAll(mIntent.getStringArrayListExtra("Items"));
        //show all items
        TextView stationV = (TextView) findViewById(R.id.textViewStationInfo);
        stationV.setText(input);

        i1 = (TextView) findViewById(R.id.textViewFirstScanned);
        i2 = (TextView) findViewById(R.id.textViewSecondScanned);
        i3 = (TextView) findViewById(R.id.textViewThirdScanned);

        if(Items.size() == 3) {
            i1.setText(Items.get(0));
            i2.setText(Items.get(1));
            i3.setText(Items.get(2));
        } else if (Items.size() == 2) {
            i1.setText(Items.get(0));
            i2.setText(Items.get(1));
        } else if (Items.size() == 1) {
            i1.setText(Items.get(0));
        }

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
        cut();
        for(int i = 0; i < Items.size(); i++) { // save each entry in the bundle
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING, building);
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_ROOM, room);
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_STATION, station);
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR, Items.get(i));

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        }
    }

    public int getPart (String text) {
        int part = 0;

        for (int i = 0; i < text.length(); i++) {
            if(text.charAt(i) == ',') {
                return i;
            }
        }
        return part;
    }

    public void cut() {
       building = input.substring(0, getPart(input));
       input = input.substring(getPart(input) + 1);
       room = input.substring(0,getPart(input));
       input = input.substring(getPart(input) + 1);
       station = input;
    }

}
