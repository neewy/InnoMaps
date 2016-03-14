package com.innopolis.maps.innomaps.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nikolay on 13.03.2016.
 */
public class HandleXML {
    private Context context;
    private XmlPullParserFactory xmlFactoryObject;
    private XmlPullParser parser;

    public HandleXML(Context context) {
        this.context = context;
        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(false);
            parser = xmlFactoryObject.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public List<HashMap<String, String>> parseXml(String filename) {
        List<HashMap<String, String>> res = new ArrayList<>();
        AssetManager manager = context.getResources().getAssets();
        int type = 0;
        try {
            InputStream input = manager.open(filename);
            parser.setInput(input, null);
            type = parser.getEventType();
            HashMap<String, String> poi = new HashMap<>();
            while (type != XmlPullParser.END_DOCUMENT) {
                if (type == XmlPullParser.START_DOCUMENT) {
                    Log.d("", "In start document");
                } else if (type == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("node")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            poi.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                        }
                    } else {
                        type = parser.next();
                        continue;
                    }
                } else if (type == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("node")) {
                        //Consider deleting it!
                        poi.put("building", "university");
                        poi.put("floor", "1floor");

                        res.add(poi);
                        poi = new HashMap<>();
                    } else {
                        type = parser.next();
                        continue;
                    }
                } else if (type == XmlPullParser.TEXT) {
                    if (parser.isWhitespace()) {
                        type = parser.next();
                        continue;
                    } else {
                        String latLng[] = parser.getText().split(" ");
                        poi.put("latitude", latLng[0]);
                        poi.put("longitude", latLng[1]);
                    }
                }
                type = parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return res;
    }
}
