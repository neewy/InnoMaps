package com.innopolis.maps.innomaps.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;

public class About extends Fragment {
    SearchView searchView;

    TextView head;
    TextView ver;
    TextView creators;
    TextView about;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, container, false);

        head = (TextView) view.findViewById(R.id.t1);
        ver = (TextView) view.findViewById(R.id.t2);
        creators = (TextView) view.findViewById(R.id.t3_1);
        about = (TextView) view.findViewById(R.id.t4);

        head.setText("InnoMaps");
        ver.setText("ver 1.0");
        creators.setText("Maksim Shalavin\n" +
                "Nikolay Yushkevich\n" +
                "Konstantin Munichev\n" +
                "Ekaterina Grishina\n" +
                "Ziyoiddin Yusupov");
        about.setText("InnoMaps is designed to help you with navigation through Innopolis University. With this application you will easily find any room in the building as well as find out about all events that take place in the University.");
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
}
