package com.innopolis.maps.innomaps.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.innopolis.maps.innomaps.R;

public class About extends Fragment {
    // TODO: decide on access level +
    private TextView creators;
    private TextView about;

    // TODO: Replace author list with XML array resource +
    String[] authorsArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, container, false);

        authorsArray = getResources().getStringArray(R.array.authors_array);

        creators = (TextView) view.findViewById(R.id.creators_about);
        about = (TextView) view.findViewById(R.id.text_about);

        about.setText(R.string.about_text);
        creators.setText(makeAuthors());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private String makeAuthors() {
        String authors = Joiner.on(" • ").join(authorsArray);
        return authors;
    }

}
