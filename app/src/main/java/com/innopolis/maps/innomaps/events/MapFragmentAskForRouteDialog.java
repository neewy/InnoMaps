package com.innopolis.maps.innomaps.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.app.SuggestionAdapter;
import com.innopolis.maps.innomaps.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragmentAskForRouteDialog extends DialogFragment {

    Context context;
    ListView listView;
    List<Event> list = new ArrayList<>(); //for storing entries
    EventsAdapter adapter; //to populate list above
    SwipeRefreshLayout swipeRefreshLayout;

    DBHelper dbHelper;
    SQLiteDatabase database;
    SharedPreferences sPref; //to store md5 hash of loaded file

    String hashPref;
    String updatedPref;

    ActionBar mActionBar;
    SearchView searchView;
    SearchView.SearchAutoComplete searchBox;
    Menu menu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final List<SearchableItem> searchItems = new ArrayList();
        final List<SearchableItem> adapterList = new ArrayList<>(searchItems);
        final EditText editText = new EditText(getContext());

        searchView = (SearchView) editText.findViewById(R.id.search_location);
        editText.setSingleLine(true);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                SearchableItem.addPois(searchItems, DBHelper.readPois(database));
                final SearchView.SearchAutoComplete searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
                searchBox.setAdapter(new SuggestionAdapter(context, R.layout.single_route, adapterList));
                searchBox.setThreshold(0);

                dismiss();
                return true;
            }
        });


        return new AlertDialog.Builder(getContext())
                .setTitle("Find route")
                .setMessage("Please specify your location")
                .setView(editText)
                .setPositiveButton("Route", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //// TODO
                        MapFragmentAskForRouteDialog.this.getDialog().cancel();
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
