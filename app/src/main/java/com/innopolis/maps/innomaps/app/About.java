package com.innopolis.maps.innomaps.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;

public class About extends Fragment {
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
        about.setText("InnoMaps is created to help you to navigate through Innopolis University. With the help of this application you are able to find any room in UI building. Moreover, it helps to find events that are held in near future.");

        return view;
    }
}
