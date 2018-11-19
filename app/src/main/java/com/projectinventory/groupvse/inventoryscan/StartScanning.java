package com.projectinventory.groupvse.inventoryscan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class StartScanning extends AppCompatActivity {
    ArrayList<String> itemList = new ArrayList<String>();
    String station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_scanning);

        //start scanning
        //mockup data
        station = "AACC,204,001";
        itemList.add("2UA5181MT8");
        itemList.add("96KYYK2");
    }

    public void checkResults(View view) { //called by click or after 3 scans
        // add scan info into bundle

        Intent intent = new Intent(this,ScannedResult.class);
        intent.putExtra("Station", station);
        intent.putExtra("Items", itemList);

        startActivityForResult(intent,1);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if((requestCode == 1) && (resultCode == RESULT_OK)) {
            if(data.getStringExtra("clicked").equals("DONE")) {
                finish();
            } else if (data.getStringExtra("clicked").equals("NEXT")) {
                //if next station scan 3
            } else if (data.getStringExtra("clicked").equals("ADD")) {
                //if add item scan one
            }
        }
    }

}
