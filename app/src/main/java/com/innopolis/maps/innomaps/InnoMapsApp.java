package com.innopolis.maps.innomaps;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Nikolay on 12.03.2016.
 */

@ReportsCrashes(
        formUri = "http://collector.tracepot.com/95d77fd5",
        reportType = org.acra.sender.HttpSender.Type.FORM,
        httpMethod = org.acra.sender.HttpSender.Method.POST,
        formUriBasicAuthLogin="reporter",
        formUriBasicAuthPassword="InnoMaps",

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