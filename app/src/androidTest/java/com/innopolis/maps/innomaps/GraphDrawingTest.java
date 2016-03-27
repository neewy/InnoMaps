package com.innopolis.maps.innomaps;

import android.graphics.Color;
import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.pathfinding.JGraphTWrapper;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.io.IOUtils;

import java.util.ArrayList;
import java.util.Collection;
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
        graph = new JGraphTWrapper(null);
        assertNotNull(graph);
        mapView = (MapView) getActivity().findViewById(R.id.map);
        assertNotNull(mapView);
        map = mapView.getMap();
        assertNotNull(map);
        routes = new ArrayList<>();
    }

    public void testAllFloors() throws Exception {
        ZoomTask zoomTask = new ZoomTask();
        zoomTask.execute(5);
        Thread.sleep(2000);
        int floor;
        for (floor = 1; floor <= 5; ++floor) {
            String result = Utils.doGetRequest(Utils.restServerUrl + "/innomaps/graphml/loadmap?floor=" + floor);
            assertNotNull(result);

            graph.importGraphML(IOUtils.toInputStream(result, "UTF-8"));
            Collection<LatLng> c = graph.getVerticesMap().values();
            LatLng[] vTo = new LatLng[c.size()];
            vTo = c.toArray(vTo);
            int testVertexCount = 5;
            Random rnd = new Random();
            LatLng[] vFrom = new LatLng[testVertexCount];
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

    class DrawTask extends AsyncTask<LatLng[], Void, LatLng[][]> {
        @Override
        protected LatLng[][] doInBackground(LatLng[]... params) {
            return params;
        }

        @Override
        protected void onPostExecute(LatLng[][] data) {
            for (Polyline route: routes) {
                route.remove();
            }
            routes.clear();
            LatLng[] vFrom = data[0];
            LatLng[] vTo = data[1];
            for (LatLng start: vFrom) {
                for (LatLng finish: vTo) {
                    ArrayList<LatLng> path = graph.shortestPath(start, finish);
                    if (path != null) {
                        routes.add(map.addPolyline(new PolylineOptions()
                                .addAll(path)
                                .width(4)
                                .color(Color.GREEN)
                                .geodesic(true)));
                    }
                }
            }
        }
    }

    class ZoomTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(Integer floor) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 18.0f));
        }
    }
}
