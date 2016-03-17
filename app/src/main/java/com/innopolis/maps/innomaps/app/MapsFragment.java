package com.innopolis.maps.innomaps.app;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.bottomview.MapBottomView;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.pathfinding.JGraphTWrapper;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LOCATION;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY;

public class MapsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    MapView mapView; //an element of the layout
    private GoogleMap map;
    private GroundOverlay imageOverlay;
    private UiSettings mSettings;
    private LocationManager locationManager;
    DBHelper dbHelper;
    SQLiteDatabase database;
    MapBottomView mapBottomView;

    SearchView searchView;
    SearchView.SearchAutoComplete searchBox;

    RadioGroup floorPicker;
    List<Marker> markerList;
    JGraphTWrapper graphWrapper;
    LinearLayout mBottomWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getReadableDatabase();
        mBottomWrapper = (LinearLayout) v.findViewById(R.id.mapBottomWrapper);
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) v.findViewById(R.id.map);
                floorPicker = (RadioGroup) v.findViewById(R.id.floorPicker);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsInitializer.initialize(getActivity().getApplicationContext());
                    }
                });
                mapView.onCreate(savedInstanceState);
                if (mapView != null) {
                    map = mapView.getMap();
                    map.setMyLocationEnabled(true);
                    mSettings = map.getUiSettings();
                    mSettings.setMyLocationButtonEnabled(true);
                    mSettings.setZoomControlsEnabled(true);
                    final LatLng university = new LatLng(55.752321, 48.744674);
                    Cursor cursor = database.query(LOCATION, null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                            String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                            map.addMarker(marker);
                        } while (cursor.moveToNext());
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(university, 15));
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    markerList = new ArrayList<>();
                    map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                displayPromptForEnablingGPS(getActivity());
                            }
                            return false;
                        }
                    });
                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if (markerList != null && markerList.size() > 0)
                                markerList.get(0).remove();
                            markerList.clear();
                            Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
                            markerList.add(marker);
                        }
                    });
                    map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            graphWrapper = new JGraphTWrapper(getContext());
                            new RestRequest().execute();
                        }
                    });
                    map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            LatLng cameraTarget = cameraPosition.target;
                            if ((cameraTarget.latitude > 55.752116019 && cameraTarget.latitude < 55.754923377) &&
                                    (cameraTarget.longitude < 48.7448166297 && cameraTarget.longitude > 48.742106790) && cameraPosition.zoom > 17.50) {
                                floorPicker.setVisibility(View.VISIBLE);
                            } else {
                                floorPicker.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String latitude = null;
                String longitude = null;
                String eventName = null;
                CheckedTextView text = (CheckedTextView) view.findViewById(R.id.name);
                String sqlQuery = "SELECT * FROM location inner join events on events.eventID=location.eventID WHERE events.summary=?";
                Cursor cursor = database.rawQuery(sqlQuery, new String[]{String.valueOf(text.getText())});
                if (cursor.moveToFirst()) {
                    latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                    longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
                    eventName = cursor.getString(cursor.getColumnIndex(SUMMARY));
                    map.clear();
                    map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))));
                }
                if (mapBottomView == null && mBottomWrapper.getChildCount() == 0) {
                    mapBottomView = new MapBottomView(getContext());
                } else if (mapBottomView != null && mBottomWrapper.getChildCount() == 1) {
                    mapBottomView = (MapBottomView) mBottomWrapper.getChildAt(0);
                }
                if (mapBottomView.getVisibility() == View.INVISIBLE || mapBottomView.getVisibility() == View.GONE) {
                    mapBottomView.setVisibility(View.VISIBLE);
                }
                mapBottomView.setTitleText(eventName);
                mapBottomView.setDescText(latitude + " | " + longitude);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 17));
                if (mBottomWrapper.getChildCount() == 0) {
                    mBottomWrapper.addView(mapBottomView);
                }
                Utils.hideKeyboard(getActivity());
            }
        });
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

        MainActivity.getInstance().trackScreenView("Maps Fragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBottomWrapper.removeView(mapBottomView);
        mapBottomView = null;
    }

    private Marker addMarker(LatLng point) {
        return map.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.test_custom_marker)));
    }

    private class RestRequest extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return Utils.doGetRequest(Utils.restServerUrl + "/innomaps/graphml/loadmap?floor=1");
        }

        @Override
        protected void onPostExecute(String str) {
            Log.i("graph", str);
            if (str.equals("")) {
                return;
            }
            try {
                graphWrapper.importGraphML(IOUtils.toInputStream(str, "UTF-8"));
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            LatLng start = new LatLng(55.75351526583595, 48.74356482177973);
            LatLng finish = new LatLng(55.75421676452847, 48.74331135302782);

            ArrayList<LatLng> path = graphWrapper.shortestPath(start, finish);
            map.addPolyline(new PolylineOptions()
                    .addAll(path)
                    .width(4)
                    .color(Color.GREEN)
                    .geodesic(true));

        }
    }

    private void displayPromptForEnablingGPS(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location.  Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                d.dismiss();
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    private void initializeOverlay(){
        putOverlayToMap(new LatLng(55.752533,48.742492), new LatLng(55.754656,48.744589), BitmapDescriptorFactory.fromResource(R.raw.ai6_floor1));
        floorPicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LatLng southWest, northEast;
                switch (checkedId){
                    case R.id.button1: default:
                        southWest = new LatLng(55.752533,48.742492);
                        northEast = new LatLng(55.754656,48.744589);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor1));
                        break;
                    case R.id.button2:
                        southWest = new LatLng(55.752828,48.742661);
                        northEast = new LatLng(55.754597,48.744469);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor2));
                        break;
                    case R.id.button3:
                        southWest = new LatLng(55.752875,48.742739);
                        northEast = new LatLng(55.754572,48.744467);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor3));
                        break;
                    case R.id.button4:
                        southWest = new LatLng(55.752789,48.742711);
                        northEast = new LatLng(55.754578,48.744569);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor4));
                        break;
                    case R.id.button5:
                        southWest = new LatLng(55.752808,48.743497);
                        northEast = new LatLng(55.753383,48.744519);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor5));
                        break;
                }
            }
        });

    }
    private void putOverlayToMap(LatLng southWest, LatLng northEast, BitmapDescriptor bitmapDescriptor){
        if(imageOverlay!=null){
            imageOverlay.remove();
        }
        LatLngBounds latLngBounds;
        GroundOverlayOptions groundOverlayOptions;
        latLngBounds = new LatLngBounds(southWest, northEast);
        groundOverlayOptions = new GroundOverlayOptions();
        groundOverlayOptions.positionFromBounds(latLngBounds);
        groundOverlayOptions.image(bitmapDescriptor);
        imageOverlay = map.addGroundOverlay(groundOverlayOptions);
    }
}

