package com.projectinventory.groupvse.inventoryscan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LookupResult extends AppCompatActivity {

    InventoryDbHelper mDbHelper;
    SQLiteDatabase db;
    Intent mIntent;
    ArrayList<String> Items = new ArrayList<String>();
    String input, station, building, room;
    EditText allItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_result);

        //instantiate a Database to read from
        mDbHelper = new InventoryDbHelper(getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        //get Information of previous scan
        mIntent = getIntent();
        input = mIntent.getStringExtra("Station");


        TextView stationV = (TextView) findViewById(R.id.textView);
        Button edit = (Button) findViewById(R.id.button9);
        stationV.setText(input);
        allItems = (EditText) findViewById(R.id.editText);

        //extract information from scan
        cut();

        //query to find all items to a station
        String query = "select " + InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR
                +" from " + InventoryContract.InventoryEntry.TABLE_NAME + " where "
                + InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING + " = '" + building + "' AND "
                +  InventoryContract.InventoryEntry.COLUMN_NAME_ROOM + " = '" + room + "' AND "
                + InventoryContract.InventoryEntry.COLUMN_NAME_STATION + " = '" + station + "'";

        Cursor cursor = db.rawQuery(query, new String[] {});

        StringBuffer item = new StringBuffer();

        while (cursor.moveToNext()) {
            //get all items into cache and show them on GUI
            item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR)));
            Items.add(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR)));
            item.append("\n");
        }

        //when nothing was found in DB show
        if(item.length() == 0) {
            allItems.append("Station not found");
            edit.setClickable(false);
            edit.setAlpha(.5f);
        } else {
            allItems.append(item);
        }

        cursor.close();

    }

    //extract building, room, station from input String
    public void cut() {
        building = input.substring(0, getPart(input));
        input = input.substring(getPart(input) + 1);
        room = input.substring(0,getPart(input));
        input = input.substring(getPart(input) + 1);
        station = input;
    }

    //returns the index of the next "," Character in a String
    public int getPart (String text) {
        int part = 0;

        for (int i = 0; i < text.length(); i++) {
            if(text.charAt(i) == ',') {
                return i;
            }
        }
        return part;
    }

    public void doneWithIt(View view) {

        // finish activity to return to home screen
        Intent data = new Intent();
        data.putExtra("clicked", "DONE");
        setResult(RESULT_OK, data);
        finish();

    }

    public void editItem(View view) {
        //start ScannedResult Activity to edit Station
        Intent intent = new Intent(this,StartScanning.class);
        intent.putExtra("Station", station);
        intent.putExtra("Items", Items);
        startActivityForResult(intent,1);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // finish activity to return to home screen after editing Station
        if((requestCode == 1) && (resultCode == RESULT_OK)) {
            if(data.getStringExtra("clicked").equals("DONE")) {
                Intent data2 = new Intent();
                data2.putExtra("clicked", "DONE");
                setResult(RESULT_OK, data2);
                finish();
            }
        }
    }
}
