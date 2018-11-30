package com.projectinventory.groupvse.inventoryscan;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class exporting extends IntentService {
    InventoryDbHelper mDbHelper;
    SQLiteDatabase db;

    public exporting() {
        super("exporting");
    }

    @Override
    protected void onHandleIntent(Intent intent) {




        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

            mDbHelper = new InventoryDbHelper(getApplicationContext());
            db = mDbHelper.getReadableDatabase();
            ArrayList<String> Items = new ArrayList<>();

            String query = "select * from " +  InventoryContract.InventoryEntry.TABLE_NAME;

            Cursor cursor = db.rawQuery(query, new String[] {});

        while(cursor.moveToNext()){
                String item = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING)) + ", "
                        + cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_ROOM)) + ", "
                        + cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_STATION)) + ", "
                        + cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR));
                Items.add(item);
            }
        cursor.close();

            Long ts = System.currentTimeMillis()/1000;

            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), ts.toString());

            String filename = ts.toString();
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                for(int i = 0; i < Items.size(); i++) {
                    outputStream.write(Items.get(i).getBytes());
                }
                outputStream.close();
                wipeDB();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                Toast.makeText(this, "Execution ended", Toast.LENGTH_SHORT).show();
                stopSelf();
            }

        }
    private void wipeDB(){
        mDbHelper = new InventoryDbHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        String sql = "Delete FROM " + InventoryContract.InventoryEntry.TABLE_NAME;
        db.execSQL(sql);
    }
    }
