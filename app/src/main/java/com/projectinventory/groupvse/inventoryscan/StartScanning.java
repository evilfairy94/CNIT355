package com.projectinventory.groupvse.inventoryscan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartScanning extends AppCompatActivity {
    Bundle scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_scanning);

        //start scanning
    }

    public void checkResults(View view) { //called by click or after 3 scans
        // add scan info into bundle
        scan.putString("Item0", "value1");
        scan.putString("Item1", "value2");
        scan.putString("Item2", "value3");
        Intent intent = new Intent(this,ScannedResult.class);
        intent.putExtra("scanContent", scan);
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
