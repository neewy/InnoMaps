package com.innopolis.maps.innomaps.qr;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.SQLQueries;
import com.innopolis.maps.innomaps.maps.MapsFragment;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields._ID;


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
            int floor = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FLOOR)).substring(0, 1));
            maps.showRoute(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), floor, new LatLng(destinationLatitude, destinationLongitude));
            maps.currentDialog.cancel();
            this.finish();
        } else {
            Toast.makeText(this, R.string.wrond_qr, Toast.LENGTH_SHORT).show();
            mScannerView.resumeCameraPreview(this);
        }
        cursor.close();
    }
}
