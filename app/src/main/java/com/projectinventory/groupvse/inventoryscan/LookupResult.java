package com.projectinventory.groupvse.inventoryscan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LookupResult extends AppCompatActivity {

    InventoryDbHelper mDbHelper;
    SQLiteDatabase db;
    Intent mIntent;
    ArrayList<String> Items = new ArrayList<String>();
    String input, station, building, room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_result);

        mDbHelper = new InventoryDbHelper(getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        mIntent = getIntent();
        //add all Strings of Intent into one ArrayList
        input = mIntent.getStringExtra("Station");
        //show all items
        TextView stationV = (TextView) findViewById(R.id.textView);
        stationV.setText(input);
        EditText allItems = (EditText) findViewById(R.id.editText);

        cut();

        String query = "select " + InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR
                +" from " + InventoryContract.InventoryEntry.TABLE_NAME + " where "
                + InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING + " = '" + building + "' AND "
                +  InventoryContract.InventoryEntry.COLUMN_NAME_ROOM + " = '" + room + "' AND "
                + InventoryContract.InventoryEntry.COLUMN_NAME_STATION + " = '" + station + "'";

        Cursor cursor = db.rawQuery(query, new String[] {});


        while(cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR));
            Items.add(item);
        }
        cursor.close();

        for(int i = 0; i < Items.size(); i++) {

            allItems.append(Items.get(i)+"\n");
        }


    }

    public void cut() {
        building = input.substring(0, getPart(input));
        input = input.substring(getPart(input) + 1);
        room = input.substring(0,getPart(input));
        input = input.substring(getPart(input) + 1);
        station = input;
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

    public void doneWithIt(View view) {
        //finish after saving to database
        Intent data = new Intent();
        data.putExtra("clicked", "DONE");
        setResult(RESULT_OK, data);
        finish();

    }
}
