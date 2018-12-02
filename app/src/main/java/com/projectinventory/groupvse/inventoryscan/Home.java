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

    public void startScanning(View view) {
        Intent mIntent = new Intent(this, StartScanning.class);
        startActivity(mIntent);
    }

    public void lookup(View view) {
        Intent mIntent = new Intent(this, LookupStation.class);
        startActivity(mIntent);
    }

    public void exportData(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

            mDbHelper = new InventoryDbHelper(getApplicationContext());
            db = mDbHelper.getReadableDatabase();
            ArrayList<String> Items = new ArrayList<>();

            String query = "select * from " + InventoryContract.InventoryEntry.TABLE_NAME;

            Cursor cursor = db.rawQuery(query, new String[]{});
            StringBuffer item = new StringBuffer();

            while (cursor.moveToNext()) {

                item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_BUILDING)) + ", ");
                item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_ROOM)) + ", ");
                item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_STATION)) + ", ");
                item.append(cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_SERIALNR)));
                item.append("\n");
                Items.add(item.toString());
            }
            cursor.close();

            Long ts = System.currentTimeMillis() / 1000;

            try {
                FileOutputStream outputStream = new FileOutputStream
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + ts.toString());
                for (int i = 0; i < Items.size(); i++) {
                    outputStream.write(Items.get(i).getBytes());
                }
                outputStream.close();
                Toast.makeText(this, "File saved in Downloads", Toast.LENGTH_SHORT).show();
                wipeDB();
            } catch (Exception e) {
                Toast.makeText(this, "File could not be saved", Toast.LENGTH_SHORT).show();

            }

    }
    private void wipeDB(){
        mDbHelper = new InventoryDbHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        String sql = "Delete FROM " + InventoryContract.InventoryEntry.TABLE_NAME;
        db.execSQL(sql);
    }
}

