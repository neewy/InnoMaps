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
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.SQLQueries;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.maps.MapsFragment;
import com.innopolis.maps.innomaps.network.Constants;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields._ID;
import static com.innopolis.maps.innomaps.network.Constants.LOG;


public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

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
        List<BarcodeFormat> formatList = new ArrayList<>();
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
        Cursor cursor = database.rawQuery(SQLQueries.selectAllLike(POI, _ID, rawResult.getText()), null);
        if (cursor.moveToFirst()) {
            FragmentManager fm = MainActivity.getInstance().getSupportFragmentManager();
            MapsFragment maps = (MapsFragment) fm.findFragmentByTag(getString(R.string.maps));
            String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
            int floorSource = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FLOOR)).substring(0, 1));
            int floorDestination = 1;
            LatLng destinationLatLng = new LatLng(destinationLatitude, destinationLongitude);
            cursor = database.rawQuery(SQLQueries.selectFloorForCoordinate(destinationLatLng), null);
            if (cursor.moveToFirst())
                floorDestination = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FLOOR)).substring(0, 1));
            else {
                // TODO: Remove commented code after and only after the app will work with 3D coordinates and the DB will support them
                // To be honest, the error message should be shown. If there was no floor detection on server shortest path will work incorrectly.
                // Since, as I hope, we will rewrite app and DB to support 3D coordinates and such floor detection won't be needed
                // I will leave it as it is. But honestly, I understand that everything here holds on a hair.
                Log.e(LOG, String.format("%1$s %2$s: %3$s, %4$s: %5$s", Constants.FLOOR_CALCULATION_ERROR, Constants.LATITUDE,
                        destinationLatLng.latitude, Constants.LONGITUDE, destinationLatLng.longitude));
            }
            cursor.close();

            LatLngFlr source = new LatLngFlr(Double.parseDouble(latitude), Double.parseDouble(longitude), floorSource);
            LatLngFlr destination = new LatLngFlr(destinationLatLng.latitude, destinationLatLng.longitude, floorDestination);

            maps.showRoute(source, destination);
            maps.currentDialog.cancel();
            this.finish();
        } else {
            Toast.makeText(this, R.string.wrond_qr, Toast.LENGTH_SHORT).show();
            mScannerView.resumeCameraPreview(this);
        }
        cursor.close();
    }
}
