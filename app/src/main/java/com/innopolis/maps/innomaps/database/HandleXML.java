package com.innopolis.maps.innomaps.database;

import android.content.Context;
import android.widget.Toast;

import com.innopolis.maps.innomaps.R;

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

import static com.innopolis.maps.innomaps.database.TableFields.BUILDING;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.ID;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.NODE;
import static com.innopolis.maps.innomaps.database.TableFields.UNIVERSITY;

public class HandleXML {
    private XmlPullParser parser;

    FileInputStream inputStream = null;


    public HandleXML(Context context) {
        try {
            inputStream = context.openFileInput(context.getString(R.string.graph_filename));
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(false);
            parser = xmlFactoryObject.newPullParser();
        } catch (XmlPullParserException | FileNotFoundException e) {
            Toast.makeText(context, R.string.internet_connect, Toast.LENGTH_SHORT).show();
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
                    if (parser.getName().equals(NODE)) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            poi.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                        }
                    } else {
                        type = parser.next();
                        continue;
                    }
                } else if (type == XmlPullParser.END_TAG) {
                    if (parser.getName().equals(NODE)) {
                        //Consider deleting it!
                        poi.put(BUILDING, UNIVERSITY);
                        poi.put(FLOOR, poi.get(ID).substring(0, 1) + FLOOR);

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
                        poi.put(LATITUDE, latLng[0]);
                        poi.put(LONGITUDE, latLng[1]);
                    }
                }
                type = parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}