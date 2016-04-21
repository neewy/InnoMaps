package com.innopolis.maps.innomaps;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formUri = "https://collector.tracepot.com/95d77fd5",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class InnoMapsApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}