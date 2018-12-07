package com.projectinventory.groupvse.inventoryscan;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ScannedResult extends AppCompatActivity {
    InventoryDbHelper mDbHelper;
    SQLiteDatabase db;
    Intent mIntent;
    String selectedItem;
    ArrayList<String> Items = new ArrayList<>();
    ArrayList<String> lostItems = new ArrayList<>();
    String input, station, building, room;
    ListView listViewScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_result);

        //instantiate a Database to write to
        mDbHelper = new InventoryDbHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        //get Information of previous scan
        mIntent = getIntent();
        //save Station and items in cache
        input = mIntent.getStringExtra("Station");
        Items.addAll(mIntent.getStringArrayListExtra("Items"));
        //show Station as headline
        TextView stationV = (TextView) findViewById(R.id.textViewStationInfo);
        stationV.setText(input);


        //put items in a list with a radio button so an item can be selected during edit
        //show all items in a list - select first
        listViewScan = (ListView) findViewById(R.id.listViewScanned);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, Items);
        listViewScan.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewScan.setAdapter(adapter);
        listViewScan.setItemChecked(0, true);

        listViewScan
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        selectedItem = Items.get(arg2);

                    }
                });
        selectedItem = Items.get(0);

    }





    public void addItem(View view) {
        //give all the information back to scan an additional item
        //finish this activity
        Intent data = new Intent();
        data.putExtra("clicked", "ADD");
        data.putExtra("Station", input);
        data.putExtra("Items", Items);
        setResult(RESULT_OK, data);
        finish();
    }

    public void nextStation(View view) {
        //save all the information to database
        //then finish activity to scan next station
        save();
        Intent data = new Intent();
        data.putExtra("clicked", "NEXT");
        setResult(RESULT_OK, data);
        finish();
    }

    public void doneWithIt(View view) {
        //save all the information to database
        //then finish activity to return to home screen
        save();
        Intent data = new Intent();
        data.putExtra("clicked", "DONE");
        setResult(RESULT_OK, data);
        finish();

    }

    private void save() {
        //extract pieces of station
        cut();
        ArrayList<String> newItems = new ArrayList<>();
        //insert each item that is not in the DB
        for(int i = 0; i < Items.size(); i++) {
            if (!isInDB(Items.get(i))) {
                newItems.add(Items.get(i));
            }
        }
        insert(newItems);
        //if there are any items that got deleted from GUI, delete them from DB
        if(lostItems.size() > 0) {
            delete(lostItems);
        }
    }

    private void delete(ArrayList<String> items) {
        //Delete given items from Table
        for (int i = 0; i < items.size(); i++) {
            db.delete(InventoryContract.InventoryEntry.TABLE_NAME, InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR + "=?", new String[]{items.get(i)});
        }
    }

    private void insert(ArrayList<String> items) {
        //insert given items into table, format is Building, Room, Station, Serialnr
        for (int i = 0; i < items.size(); i++) { // save each entry in the bundle
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING, building);
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_ROOM, room);
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_STATION, station);
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR, items.get(i));

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        }
    }

    private boolean isInDB(String item){
        //query to find item to specific station
        String query = "select * from " + InventoryContract.InventoryEntry.TABLE_NAME + " where "
                + InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING + " = '" + building + "' AND "
                +  InventoryContract.InventoryEntry.COLUMN_NAME_ROOM + " = '" + room + "' AND "
                + InventoryContract.InventoryEntry.COLUMN_NAME_STATION + " = '" + station + "' AND "
                + InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR + " = '" + item + "'";

        Cursor cursor = db.rawQuery(query, new String[] {});

        boolean found = false;
        //if the query has a result, an item was found and method returns true
        if (cursor.moveToNext()) {
            found = true;
        }

        cursor.close();

        return found;
    }

    //returns the index of the next "," Character in a String
    public int getPart(String text) {

        int part = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ',') {
                return i;
            }
        }
        return part;
    }

    //extract building, room, station from input String
    public void cut() {
        building = input.substring(0, getPart(input));
        input = input.substring(getPart(input) + 1);
        room = input.substring(0, getPart(input));
        input = input.substring(getPart(input) + 1);
        station = input;
    }
//method to delete selected item from listview and repopulate list with remaining items

    //on button click
    public void deleteItem(View view) {
        //mark item as deleted and remove from itemlist
        if(!Items.isEmpty()) {
            lostItems.add(selectedItem);
            Items.remove(Items.indexOf(selectedItem));


        //update gui to remove item from List
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, Items);
        listViewScan.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewScan.setAdapter(adapter);
        listViewScan.setItemChecked(0, true);
        if(!Items.isEmpty()) {
            selectedItem = Items.get(0);
        }}

    }

}
