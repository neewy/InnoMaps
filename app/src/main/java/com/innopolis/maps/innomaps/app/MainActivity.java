package com.innopolis.maps.innomaps.app;

import android.app.SearchManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String MAPS = "Maps";
    private final String FAV = "Favourite";
    private final String EVENTS = "Events";
    private final String DETAILED = "Detailed";

    private boolean doubleBackToExitPressedOnce = false;

    DBHelper dbHelper;
    SQLiteDatabase database;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new MapsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, fragment, MAPS).addToBackStack(MAPS);
        ft.commit();
        getSupportActionBar().setTitle(MAPS);
        dbHelper = new DBHelper(MainActivity.this);
        database = dbHelper.getReadableDatabase();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

                if (doubleBackToExitPressedOnce) {
                    finish();
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

            } else {
                int lastEntry = getSupportFragmentManager().getBackStackEntryCount()-1;
                android.support.v4.app.FragmentManager.BackStackEntry last = getSupportFragmentManager().getBackStackEntryAt(lastEntry);
                    if (last.getName().equals(DETAILED)) {
                        getSupportActionBar().setTitle(getSupportFragmentManager().getBackStackEntryAt(lastEntry-1).getName());
                        getSupportFragmentManager().popBackStackImmediate();
                    } else {
                        getSupportFragmentManager().popBackStackImmediate(MAPS, 0);
                        getSupportActionBar().setTitle(MAPS);
                    }
            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            invalidateOptionsMenu();
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        final List<String> eventNames = Utils.getEventNames(database);
        final SearchView.SearchAutoComplete searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchBox.setAdapter(new SuggestionAdapter<String>(this, R.layout.complete_row, eventNames));
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventNames.clear();
                for (String string : Utils.getEventNames(database)) {
                    if (string.toLowerCase().contains(s.toString().toLowerCase())) {
                        eventNames.add(string);
                    }
                }
                ((SuggestionAdapter) searchBox.getAdapter()).refresh(eventNames);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (eventNames.size() == 0) {
                    Toast.makeText(MainActivity.this, R.string.empty_search, Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });;
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        String title = "";
        Fragment fragment = null;

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (id == R.id.nav_maps) {
                fragment = new MapsFragment();
                title = MAPS;
            } else if (id == R.id.nav_favourite) {
                fragment = new FavouriteFragment();
                title = FAV;
            } else if (id == R.id.nav_event) {
                fragment = new EventsFragment();
                title = EVENTS;
            } else if (id == R.id.nav_share) {
               //nothing to do
            }
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
        } else {
            if (id == R.id.nav_maps) {
                title = MAPS;
                if (getSupportFragmentManager().findFragmentByTag(MAPS) !=null) {
                    getSupportFragmentManager().popBackStackImmediate(MAPS, 0);
                } else {
                    fragment = new MapsFragment();
                }
            } else if (id == R.id.nav_favourite) {
                title = FAV;
                if (getSupportFragmentManager().findFragmentByTag(FAV)!=null) {
                    getSupportFragmentManager().popBackStackImmediate(FAV, 0);
                } else {
                    fragment = new FavouriteFragment();
                }
            } else if (id == R.id.nav_event) {
                title = EVENTS;
                if (getSupportFragmentManager().findFragmentByTag(EVENTS)!=null) {
                    getSupportFragmentManager().popBackStackImmediate(EVENTS, 0);
                } else {
                    fragment = new EventsFragment();
                }
            } else if (id == R.id.nav_share) {
                //nothing to do
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.content_frame, fragment, title).addToBackStack(title);
            ft.commit();
        }
        getSupportActionBar().setTitle(title);
        return true;
    }

}
