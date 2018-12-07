package com.projectinventory.groupvse.inventoryscan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

public class LookupStation extends AppCompatActivity {
    String station = new String();
    ArrayList<String> itemList = new ArrayList<>();
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    SurfaceView surfaceView;
    TextView barcodeVal;
    String intentData;
    int index;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_station);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        surfaceView = findViewById(R.id.surfaceView2);
        barcodeVal = findViewById(R.id.textView3);
        index = 0;
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
                    if (ActivityCompat.checkSelfPermission(LookupStation.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(LookupStation.this, new
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

                    intentData = barcodes.valueAt(0).displayValue;
                    handler.sendEmptyMessage(0);

                    if(!station.equals(intentData)) {
                    station = intentData;

                    v.vibrate(100);}

                }

            }
        });
    }

    public void checkResults(View view) {
        //on buttonclick Save all scanned Information to Intent
        // call LookupResult
        if(!station.isEmpty()) {
            Intent intent = new Intent(this, LookupResult.class);
            intent.putExtra("Station", station);
            startActivityForResult(intent, 2);
        }
    }



    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        //finish and return to homescreen when Resuld of LookupResult is DONE
        if((requestCode == 2) && (resultCode == RESULT_OK)) {
            if(data.getStringExtra("clicked").equals("DONE")) {
                finish();
            }
        }
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            barcodeVal.setText(intentData);
        }
    };
}
