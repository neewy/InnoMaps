package com.innopolis.maps.innomaps.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HandleXML {
    private XmlPullParser parser;

    FileInputStream inputStream = null;


    public HandleXML(Context context) {
        try {
            inputStream = context.openFileInput("9.xml");
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(false);
            parser = xmlFactoryObject.newPullParser();
        } catch (XmlPullParserException | FileNotFoundException e) {
            Toast.makeText(context, "Connect to the internet", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public List<HashMap<String, String>> parseXml() {
        List<HashMap<String, String>> res = new ArrayList<>();
        BufferedReader br = null;
        int type;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            parser.setInput(br);
            type = parser.getEventType();
            HashMap<String, String> poi = new HashMap<>();
            while (type != XmlPullParser.END_DOCUMENT) {
                if (type == XmlPullParser.START_DOCUMENT) {
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
                        poi.put("floor", poi.get("id").substring(0,1) + "floor");

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
        try {
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}