package com.projectinventory.groupvse.inventoryscan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

        Intent Service = new Intent(this, exporting.class);
        try {
            startService(Service);

        } catch (Exception e) {
            Toast.makeText(this, "File could not be saved", Toast.LENGTH_SHORT).show();
        }

    }


}

