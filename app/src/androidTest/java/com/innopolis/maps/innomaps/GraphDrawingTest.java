package com.innopolis.maps.innomaps;

import android.graphics.Color;
import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.pathfinding.JGraphTWrapper;
import com.innopolis.maps.innomaps.maps.LatLngGraphVertex;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.io.IOUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Testing how path drawing works.
 * Created by luckychess on 3/21/16.
 */
public class GraphDrawingTest extends ActivityInstrumentationTestCase2<MainActivity> {
    JGraphTWrapper graph;
    MapView mapView;
    GoogleMap map;
    final LatLng center = new LatLng(55.75339808045493, 48.743342868983746);
    ArrayList<Polyline> routes;

    public GraphDrawingTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        graph = new JGraphTWrapper();
        assertNotNull(graph);
        mapView = (MapView) getActivity().findViewById(R.id.map);
        assertNotNull(mapView);
        map = mapView.getMap();
        assertNotNull(map);
        routes = new ArrayList<>();
    }

    public void testAllFloors() throws Exception {
        ZoomTask zoomTask = new ZoomTask();
        zoomTask.execute();
        Thread.sleep(2000);
        int floor;
        for (floor = 1; floor <= 5; ++floor) {
            String result = Utils.doGetRequest(Utils.restServerUrl + "/innomaps/graphml/loadmap?floor=" + floor);
            assertNotNull(result);
            graph.importGraphML(IOUtils.toInputStream(result, "UTF-8"));

            int testVertexCount = 3;
            LatLngGraphVertex[] vTo = graph.getVertices();
            LatLngGraphVertex[] vFrom = new LatLngGraphVertex[testVertexCount];
            Random rnd = new Random();

            for (int i = 0; i < testVertexCount; ++i) {
                vFrom[i] = vTo[rnd.nextInt(vTo.length)];
            }

            DrawTask task = new DrawTask();
            task.execute(vFrom, vTo);
            switch (floor) {
                case 1:
                    onView(withId(R.id.button1)).perform(click());
                    break;
                case 2:
                    onView(withId(R.id.button2)).perform(click());
                    break;
                case 3:
                    onView(withId(R.id.button3)).perform(click());
                    break;
                case 4:
                    onView(withId(R.id.button4)).perform(click());
                    break;
                case 5:
                    onView(withId(R.id.button5)).perform(click());
                    break;
            }
            Thread.sleep(10000);
        }
    }

    class DrawTask extends AsyncTask<LatLngGraphVertex[], Void, LatLngGraphVertex[][]> {
        @Override
        protected LatLngGraphVertex[][] doInBackground(LatLngGraphVertex[]... params) {
            return params;
        }

        @Override
        protected void onPostExecute(LatLngGraphVertex[][] data) {
            for (Polyline route: routes) {
                route.remove();
            }
            routes.clear();
            LatLngGraphVertex[] vFrom = data[0];
            LatLngGraphVertex[] vTo = data[1];
            for (LatLngGraphVertex start: vFrom) {
                for (LatLngGraphVertex finish: vTo) {
                    if (start.equals(finish)) {
                        continue;
                    }
                    ArrayList<LatLngGraphVertex> path;
                    try {
                        NetworkController networkController = new NetworkController();
                        path = (ArrayList<LatLngGraphVertex>) networkController.findShortestPath(String.valueOf(start.getVertex().latitude), String.valueOf(start.getVertex().longitude),
                                String.valueOf(finish.getVertex().latitude), String.valueOf(finish.getVertex().longitude));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return;
                    }
                    if (path == null) {
                        Log.d("graph", "Path not found between " + start.getVertexId() + " and " + finish.getVertexId());
                        assertTrue(false);
                    }

                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.width(4);
                    polylineOptions.color(Color.GREEN);
                    polylineOptions.geodesic(true);
                    for (LatLngGraphVertex v: path) {
                        polylineOptions.add(v.getVertex());
                    }
                    routes.add(map.addPolyline(polylineOptions));
                }
            }
        }
    }

    class ZoomTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 18.0f));
        }
    }
}
