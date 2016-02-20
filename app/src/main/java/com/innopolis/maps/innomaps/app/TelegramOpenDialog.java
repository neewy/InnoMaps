package com.innopolis.maps.innomaps.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.innopolis.maps.innomaps.utils.Utils;


/**
 * Created by neewy on 2/13/16.
 */
public class TelegramOpenDialog extends DialogFragment {
    String message;
    String link;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        message = "Get in touch with " + getArguments().getString("dialogText") + " via telegram";
        String[] dialogUrl = getArguments().getString("dialogUrl").split("/");
        link = dialogUrl[dialogUrl.length - 1];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Open in Telegram", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        intentMessageTelegram(link);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TelegramOpenDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    protected void intentMessageTelegram(String link) {
        final String appName = "org.telegram.messenger";
        final boolean isAppInstalled = Utils.isAppAvailable(getContext(), appName);
        if (isAppInstalled) {
            String url = "tg://join?invite=" + link;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            Toast.makeText(getActivity(), "Telegram not Installed", Toast.LENGTH_SHORT).show();
        }
    }
}
