package com.innopolis.maps.innomaps.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

public class SplashScreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove the Title Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Get the view from splash_screen.xml
        setContentView(R.layout.splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();

        long delay = 500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Executed after timer is finished (Opens MainActivity)
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, delay);
    }
}
