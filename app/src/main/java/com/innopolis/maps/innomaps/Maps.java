package com.innopolis.maps.innomaps;

/**
 * Created by Nikolay on 02.02.2016.
 */

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class Maps extends SupportMapFragment implements GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    static Context context;
    private GoogleMap mMap;
    private UiSettings mSettings;
    SupportMapFragment mSupportMapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.activity_maps, null, false);
        initilizeMap();
        return root;
    }

    private void initilizeMap()
    {
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapWrapper, mSupportMapFragment).commit();
        }
        if (mSupportMapFragment != null)
        {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    mSettings = mMap.getUiSettings();
                    mSettings.setZoomControlsEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    LatLng university = new LatLng(55.752321, 48.744674);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(university, 15));
                }
            });
            /*if (mMap != null)
                mMap.setOnMapClickListener(new mMap()
                {
                    @Override
                    public void onMapClick(LatLng point)
                    {
                        //TODO: your onclick stuffs
                    }
                });*/
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        Location myLocation = mMap.getMyLocation();
        LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        CameraPosition myPosition = new CameraPosition.Builder().target(myLatLng).zoom(17).bearing(90).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
        return false;
    }

}

