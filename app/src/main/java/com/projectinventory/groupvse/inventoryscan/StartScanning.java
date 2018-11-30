package com.projectinventory.groupvse.inventoryscan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

public class StartScanning extends AppCompatActivity {
    ArrayList<String> itemList = new ArrayList<String>();
    String station;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    SurfaceView surfaceView;
    TextView barcodeVal;
    String intentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_scanning);

        //start scanning
        //mockup data
        station = "AACC,204,001";
        itemList.add("2UA5181MT8");
        itemList.add("96KYYK2");

        surfaceView = findViewById(R.id.surfaceView);
        barcodeVal = findViewById(R.id.textView2);
        initBarcodeScanning();

    }

    private void initBarcodeScanning() {
        Toast.makeText(getApplicationContext(), "Barcode Scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(StartScanning.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(StartScanning.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {


                    barcodeVal.post(new Runnable() {

                        @Override
                        public void run() {
                                intentData = barcodes.valueAt(0).displayValue;
                                barcodeVal.setText(intentData);
                        }
                    });

                }

            }
        });
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
