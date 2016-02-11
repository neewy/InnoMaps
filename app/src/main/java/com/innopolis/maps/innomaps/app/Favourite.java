package com.innopolis.maps.innomaps.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.innopolis.maps.innomaps.R;

import java.util.ArrayList;
import java.util.HashMap;


public class Favourite extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    static Context context;
    ListView listView;
    static ArrayList<HashMap<String, String>> list = new ArrayList<>();
    EventsAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.events, container, false);
        listView = (ListView) view.findViewById(R.id.eventList);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        this.adapter = new EventsAdapter(context, getActivity().getSupportFragmentManager(), list, getActivity());
        listView.setAdapter(this.adapter);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        dbHelper = new DBHelper(context);
                                        database = dbHelper.getWritableDatabase();
                                        onRefresh();
                                    }
                                }
        );
        return view;
    }

    public void onRefresh() {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        list.clear();
        Toast.makeText(context, "Showing favourite events", Toast.LENGTH_SHORT).show();
        DBHelper.readEvents(list, database, true);
        adapter.notifyDataSetChanged();
        database.close();
        swipeRefreshLayout.setRefreshing(false);
    }
}
