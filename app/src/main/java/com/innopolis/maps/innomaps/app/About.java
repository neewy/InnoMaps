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

import java.util.LinkedList;
import java.util.List;

public class About extends Fragment {
    TextView creators;
    TextView about;

    List<String> authorsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, container, false);
        authorsList = new LinkedList<>();

        creators = (TextView) view.findViewById(R.id.t3_1);
        about = (TextView) view.findViewById(R.id.t4);

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
        String authors = "";
        appendText();

        for (String string : authorsList) {
            if (authorsList.indexOf(string) < (authorsList.size() - 1)) {
                authors += string + " • ";
            } else {
                authors += string;
            }
        }
        return authors;
    }

    private void appendText() {
        authorsList.add("Yushkevich N");
        authorsList.add("Munichev K");
        authorsList.add("Yusupov Z");
        authorsList.add("Grishina E");
        authorsList.add("Shalavin M");
    }
}
