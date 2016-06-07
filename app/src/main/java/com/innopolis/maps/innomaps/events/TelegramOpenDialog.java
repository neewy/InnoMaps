package com.innopolis.maps.innomaps.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


public class TelegramOpenDialog extends DialogFragment {
    String message;
    String link;
    String url;
    private static final String CONTACT_URL = "https://telegram.me/";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String nickURL = getArguments().getString("dialogText");

        if (nickURL.contains("@")) {
            link = nickURL.substring(nickURL.indexOf("@") + 1);
            message = "Get in touch with " + link + " via telegram";
            url = CONTACT_URL + link;
        } else {
            message = "Open link " + nickURL + " ?";
            link = getArguments().getString("dialogText");
            url = link;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        intentMessageTelegram();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TelegramOpenDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }


    protected void intentMessageTelegram() {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
    }
}
