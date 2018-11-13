package com.projectinventory.groupvse.inventoryscan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

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
        //Start service
    }
}

