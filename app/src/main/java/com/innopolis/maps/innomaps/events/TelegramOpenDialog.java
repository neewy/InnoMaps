package com.innopolis.maps.innomaps.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.innopolis.maps.innomaps.R;


public class TelegramOpenDialog extends DialogFragment {
    String message;
    String link;
    String url;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String nickURL = getArguments().getString(getContext().getString(R.string.dialog_text));

        if (nickURL.contains("@")) {
            link = nickURL.substring(nickURL.indexOf("@") + 1);
            message = String.format(getContext().getString(R.string.telegram_in_touch), link);
            url = getContext().getString(R.string.contact_url) + link;
        } else {
            message = String.format(getContext().getString(R.string.open_link), nickURL);
            link = getArguments().getString(getContext().getString(R.string.dialog_text));
            url = link;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        intentMessageTelegram();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
