package com.projectinventory.groupvse.inventoryscan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class LookupStation extends AppCompatActivity {
    String station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_station);
        //mockup data
        station = "AACC,204,001";
    }
    public void checkResults(View view) { //called by click or after 3 scans
        // add scan info into bundle

        Intent intent = new Intent(this,LookupResult.class);
        intent.putExtra("Station", station);
        startActivityForResult(intent,2);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if((requestCode == 2) && (resultCode == RESULT_OK)) {
            if(data.getStringExtra("clicked").equals("DONE")) {
                finish();
            } else if (data.getStringExtra("clicked").equals("EDIT")) {
                //if edit new intent?
            }
        }
    }
}
