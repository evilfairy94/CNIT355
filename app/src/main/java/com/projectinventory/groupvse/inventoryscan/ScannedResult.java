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
    ArrayList<String> Items = new ArrayList<String>();
    ArrayList<String> lostItems = new ArrayList<String>();
    String input, station, building, room;
    ListView listViewScan;


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


        //put items in a list with a radio button so an item can be selected during edit
        listViewScan = (ListView) findViewById(R.id.listViewScanned);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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

    private void save() {
        cut();
        ArrayList<String> newItems = new ArrayList<>();
        for(int i = 0; i < Items.size(); i++) {
            if (!isInDB(Items.get(i))) {
                newItems.add(Items.get(i));
            }
        }
        insert(newItems);
        if(lostItems.size() > 0) {
            delete(lostItems);
        }
    }

    private void delete(ArrayList<String> items) {
        for (int i = 0; i < items.size(); i++) {
            db.delete(InventoryContract.InventoryEntry.TABLE_NAME, InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR + "=?", new String[]{items.get(i)});
        }
    }

    private void insert(ArrayList<String> items) {
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

        String query = "select * from " + InventoryContract.InventoryEntry.TABLE_NAME + " where "
                + InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING + " = '" + building + "' AND "
                +  InventoryContract.InventoryEntry.COLUMN_NAME_ROOM + " = '" + room + "' AND "
                + InventoryContract.InventoryEntry.COLUMN_NAME_STATION + " = '" + station + "' AND "
                + InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR + " = '" + item + "'";

        Cursor cursor = db.rawQuery(query, new String[] {});

        boolean found = false;

        if (cursor.moveToNext()) {
            found = true;
        }

        cursor.close();

        return found;
    }


    public int getPart(String text) {
        int part = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ',') {
                return i;
            }
        }
        return part;
    }

    public void cut() {
        building = input.substring(0, getPart(input));
        input = input.substring(getPart(input) + 1);
        room = input.substring(0, getPart(input));
        input = input.substring(getPart(input) + 1);
        station = input;
    }
//method to delete selected item from listview and repopulate list with remaining items
    public void deleteItem(View view) {
        if(!Items.isEmpty()) {
            lostItems.add(selectedItem);
            Items.remove(Items.indexOf(selectedItem));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, Items);
        listViewScan.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewScan.setAdapter(adapter);
        listViewScan.setItemChecked(0, true);
        if(!Items.isEmpty()) {
            selectedItem = Items.get(0);
        }}

    }

}
