package com.innopolis.maps.innomaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void openWiFi(View view) {
        Intent intent = new Intent(this, WiFiActivity.class);
        startActivity(intent);
    }

    public void openEvents(View view){
        Intent intent = new Intent(this, Events.class);
        startActivity(intent);
    }
}
