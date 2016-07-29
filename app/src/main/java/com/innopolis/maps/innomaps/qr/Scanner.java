package com.innopolis.maps.innomaps.qr;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.maps.MapsFragment;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    Double destinationLatitude;
    Double destinationLongitude;
    int destinationFloor;

    @Override
    public void onCreate(Bundle state) {
        Bundle extras = getIntent().getExtras();
        destinationLatitude = extras.getDouble(com.innopolis.maps.innomaps.db.Constants.LATITUDE);
        destinationLongitude = extras.getDouble(com.innopolis.maps.innomaps.db.Constants.LONGITUDE);
        destinationFloor = extras.getInt(com.innopolis.maps.innomaps.db.Constants.FLOOR);

        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        List<BarcodeFormat> formatList = new ArrayList<>();
        formatList.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(formatList);
        setContentView(mScannerView);                // Set the scanner view as the content view
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
        RoomDAO roomDAO = new RoomDAO(MainActivity.getMainActivityContext());
        Room room = (Room) roomDAO.findById(Integer.parseInt(rawResult.getText()));
        if (null != room) {
            FragmentManager fm = MainActivity.getInstance().getSupportFragmentManager();
            MapsFragment maps = (MapsFragment) fm.findFragmentByTag(getString(R.string.maps));

            CoordinateDAO coordinateDAO = new CoordinateDAO(MainActivity.getMainActivityContext());
            Coordinate roomsCoordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());

            LatLngFlr source = new LatLngFlr(roomsCoordinate.getLatitude(), roomsCoordinate.getLongitude(), roomsCoordinate.getFloor());
            LatLngFlr destination = new LatLngFlr(destinationLatitude, destinationLongitude, destinationFloor);

            maps.showRoute(source, destination);
            maps.currentDialog.cancel();
            this.finish();
        } else {
            Toast.makeText(this, R.string.wrond_qr, Toast.LENGTH_SHORT).show();
            mScannerView.resumeCameraPreview(this);
        }
    }
}
