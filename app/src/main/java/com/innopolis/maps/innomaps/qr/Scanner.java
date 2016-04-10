package com.innopolis.maps.innomaps.qr;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.maps.MapsFragment;
import com.innopolis.maps.innomaps.database.DBHelper;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.innopolis.maps.innomaps.database.TableFields.*;

/**
 * Created by neewy on 09.04.16.
 */
public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler  {

    private ZXingScannerView mScannerView;

    DBHelper dbHelper;
    SQLiteDatabase database;

    Double destinationLatitude;
    Double destinationLongitude;

    @Override
    public void onCreate(Bundle state) {
        Bundle extras = getIntent().getExtras();
        destinationLatitude = extras.getDouble(LATITUDE);
        destinationLongitude = extras.getDouble(LONGITUDE);

        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        List<BarcodeFormat> formatList = new ArrayList<BarcodeFormat>();
        formatList.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(formatList);
        setContentView(mScannerView);                // Set the scanner view as the content view
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.d("Tag", rawResult.getText()); // Prints scan results
        Cursor cursor = database.rawQuery("SELECT * FROM poi where _id like '"+ rawResult.getText() +"'", null);
        if (cursor.moveToFirst()) {
            FragmentManager fm = MainActivity.getInstance().getSupportFragmentManager();
            MapsFragment maps = (MapsFragment) fm.findFragmentByTag("Maps");
            String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
            maps.showRoute(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), new LatLng(destinationLatitude, destinationLongitude));
            maps.currentDialog.cancel();
            this.finish();
        } else {
            Toast.makeText(this, "This QR code cannot be used", Toast.LENGTH_SHORT).show();
            mScannerView.resumeCameraPreview(this);
        }
    }
}
