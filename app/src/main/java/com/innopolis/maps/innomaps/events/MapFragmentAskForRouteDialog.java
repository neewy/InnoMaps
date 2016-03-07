package com.innopolis.maps.innomaps.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Ziyoiddin on 26-Feb-16.
 */
public class MapFragmentAskForRouteDialog extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new AlertDialog.Builder(getActivity())
                .setMessage("Take to " + getArguments().getString("dialogText"))
                .setPositiveButton("Route", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO need to make a route in main screen
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MapFragmentAskForRouteDialog.this.getDialog().cancel();
                    }
                })
                .create();
    }

}
