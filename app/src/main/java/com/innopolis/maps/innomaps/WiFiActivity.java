package com.innopolis.maps.innomaps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WiFiActivity extends AppCompatActivity implements View.OnClickListener {

    WifiManager wifi;
    ListView listView;
    List<ScanResult> results;
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    FloatingActionButton buttonScan;
    int size = 0;
    String ITEM_KEY = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi);
        Context context = getApplicationContext();
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        listView = (ListView) findViewById(R.id.listView);
        buttonScan = (FloatingActionButton) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);
        //List<ScanResult> wifiList = wifi.getScanResults();
        if (!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        this.adapter = new SimpleAdapter(WiFiActivity.this, list, android.R.layout.simple_list_item_1, new String[]{ITEM_KEY}, new int[]{ android.R.id.text1});
        //TODO: write custom adapter
        listView.setAdapter(this.adapter);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        /*for (ScanResult scanResult : wifiList) {
            int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
            TextView view = (TextView) findViewById(R.id.textView2);
            view.setText("Level is " + level + " out of 5");
        }

        int rssi = wifi.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(rssi, 5);
        TextView view1 = (TextView) findViewById(R.id.textView3);
        view1.setText("Level is " + level + " out of 5");*/
    }

    @Override
    public void onClick(View view) {
        list.clear();
        wifi.startScan();

        try {
            size = size - 1;
            while (size >= 0) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).capabilities + results.get(size).level);
                list.add(item);
                size--;
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }
}
