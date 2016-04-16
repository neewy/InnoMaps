package com.innopolis.maps.innomaps;

import android.app.Application;

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
    public final void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}