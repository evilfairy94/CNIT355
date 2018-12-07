package com.projectinventory.groupvse.inventoryscan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

    InventoryDbHelper mDbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    //starts scan process
    public void startScanning(View view) {
        Intent mIntent = new Intent(this, StartScanning.class);
        startActivityForResult(mIntent,1);
    }

    //starts lookup process
    public void lookup(View view) {
        Intent mIntent = new Intent(this, LookupStation.class);
        startActivityForResult(mIntent,1);
    }

    //starts exporting process
    public void exportData(View view) {

        //check and grant permission for writing on external storage space
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

            //instantiates a database to read from
            mDbHelper = new InventoryDbHelper(getApplicationContext());
            db = mDbHelper.getReadableDatabase();
            ArrayList<String> Items = new ArrayList<>();

            //query to select the whole database table
            String query = "select * from " + InventoryContract.InventoryEntry.TABLE_NAME;

            Cursor cursor = db.rawQuery(query, new String[]{});

            //go throw line by line and build the Strings of building, room, station, serialnr all comma separated
            //each line of database corresponds to new line in file
            while (cursor.moveToNext()) {
                StringBuffer item = new StringBuffer();
                item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING)) + ", ");
                item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_ROOM)) + ", ");
                item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_STATION)) + ", ");
                item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR)));
                item.append("\n");
                Items.add(item.toString());

            }
            cursor.close();

            //get current timestamp for filename
            Long ts = System.currentTimeMillis() / 1000;
            String filename = ts + ".txt";


            try {
                //create a File in Download directory with timestamp as name
                FileOutputStream outputStream = new FileOutputStream
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename);
                //write each item line into file
                for (int i = 0; i < Items.size(); i++) {
                    outputStream.write(Items.get(i).getBytes());
                }
                outputStream.close();
                //inform user that file was saved
                Toast.makeText(this, "File saved in Downloads", Toast.LENGTH_SHORT).show();
                //delete all information from table
                wipeDB();
            } catch (Exception e) {
                //inform user that file could not be saved
                Toast.makeText(this, "File could not be saved", Toast.LENGTH_SHORT).show();

            }

    }
    private void wipeDB(){
        //instantiate a DB to write to
        mDbHelper = new InventoryDbHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        //query to delete all data from table
        String sql = "Delete FROM " + InventoryContract.InventoryEntry.TABLE_NAME;
        //execute query
        db.execSQL(sql);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if((requestCode == 1) && (resultCode == RESULT_OK)) {

        }
    }
}

