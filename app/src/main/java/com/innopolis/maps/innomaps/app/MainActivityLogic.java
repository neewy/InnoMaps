package com.innopolis.maps.innomaps.app;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.view.View;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.events.EventsFragment;
import com.innopolis.maps.innomaps.events.FavouriteFragment;
import com.innopolis.maps.innomaps.events.MapFragmentAskForRouteDialog;
import com.innopolis.maps.innomaps.maps.MapsFragment;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.LinkedList;
import java.util.List;


public class MainActivityLogic extends AppCompatActivity {
    private static TextView locationText;
    private static TextView idPoi;
    public final List<SearchableItem> searchItems = new LinkedList<>();
    protected final String maps = "Maps";
    private final MainAnalytics mainAnalytics = new MainAnalytics();
    private final String fav = "Favourite";
    private final String events = "Events";
    private final String about = "About";
    private final String log_in = "Log in";
    private final String detailed = "Detailed";
    protected Toolbar toolbar;
    protected SQLiteDatabase database;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean doubleBackToFinishRoute = false;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private CustomScrollView scrollView;
    private FloatingActionButton routeButton;

    protected void initializeView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        scrollView = (CustomScrollView) findViewById(R.id.bottom_sheet);
        routeButton = (FloatingActionButton) findViewById(R.id.goto_fab);
        locationText = (TextView) scrollView.findViewById(R.id.locationText);
        idPoi = (TextView) scrollView.findViewById(R.id.idPoi);
    }

    protected void initializeFragment() {
        Fragment fragment = new MapsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, fragment, maps).addToBackStack(maps);
        ft.commit();
    }

    protected void initializeNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.getInstance());
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void trackScreenView(String mainActivity) {
        mainAnalytics.trackScreenView(mainActivity);
    }

    public void trackException(Exception e) {
        mainAnalytics.trackException(e);
    }

    public void trackEvent(String category, String action, String label) {
        mainAnalytics.trackEvent(category, action, label);
    }

    @Override
    public void onBackPressed() {
        int delayMillis = 2000;

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (scrollView.getVisibility() == View.VISIBLE) {
            scrollView.setVisibility(View.GONE);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                final MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(maps);
                if (mapsFragment.mapRoute != null && mapsFragment.mapRoute.hasCurrentPath) {
                    if (doubleBackToFinishRoute) {
                        mapsFragment.mapRoute.finishRoute(false);
                    }

                    this.doubleBackToFinishRoute = true;
                    Snackbar.make(findViewById(android.R.id.content), R.string.finish_route, Snackbar.LENGTH_LONG)
                            .setAction(R.string.finish, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mapsFragment.mapRoute.finishRoute(false);
                                }
                            }).setActionTextColor(Color.WHITE).show();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToFinishRoute = false;
                        }
                        // Extract 2000 to a local constant +
                    }, delayMillis);

                } else {
                    if (doubleBackToExitPressedOnce) {
                        getSupportFragmentManager().popBackStack(null,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        super.onBackPressed();
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.exit_double_back,
                            Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, delayMillis);
                }

            } else {
                int lastEntry = getSupportFragmentManager().getBackStackEntryCount() - 1;
                FragmentManager.BackStackEntry last = getSupportFragmentManager().getBackStackEntryAt(lastEntry);
                if (last.getName().equals(detailed)) {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(getSupportFragmentManager().getBackStackEntryAt(lastEntry - 1).getName());
                        getSupportFragmentManager().popBackStackImmediate();
                    }

                }else{
                    //TODO: handle back press to update events in EventsFragment
                    getSupportFragmentManager().popBackStackImmediate(maps, 0);
                    getSupportActionBar().setTitle(maps);
                }
            }
            setToggle(toolbar);
            if (getSupportActionBar().getTitle() != null)
                highlightItemDrawer(getSupportActionBar().getTitle().toString());
        }
    }

    protected void routeButtonClickListener() {
        final TextView locationText = MainActivityLogic.locationText;
        final TextView finalIdPoi = MainActivityLogic.idPoi;

        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MapFragmentAskForRouteDialog();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.dialogSource), getString(R.string.MapsFragment));
                if (finalIdPoi.getText().toString().equals(getString(R.string.event))) {
                    bundle.putString(getString(R.string.type), getString(R.string.event));
                } else {
                    bundle.putString(getString(R.string.type), getString(R.string.poi));
                    bundle.putString(getString(R.string.id), finalIdPoi.getText().toString());
                }

                bundle.putString(getString(R.string.destination), locationText.getText().toString());
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), getString(R.string.FindRoute));
            }
        });
    }

    public void setActivityDrawerToggle() {
        setToggle(toolbar);
    }

    protected void setToggle(Toolbar toolbar) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        invalidateOptionsMenu();
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Methods used for accessing database by two different parameters.
     * Depending on them DBhelper use "room" in table POI or other.
     *
     * @return Integer
     */


    private Integer getRoomPoi() {
        return 0;
    }

    private Integer getOtherPoi() {
        return 1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchItems.clear();

        // TODO: Unclear what do they mean, and how they are used +

        SearchableItem.addEvents(searchItems, DBHelper.readUniqueEvents(this, false));
        SearchableItem.addPois(searchItems, DBHelper.readPois(database, getRoomPoi()));
        SearchableItem.addPois(searchItems, DBHelper.readPois(database, getOtherPoi()));
        final List<SearchableItem> adapterList = new LinkedList<>(searchItems);
        final SearchView.SearchAutoComplete searchBox = (SearchView.SearchAutoComplete)
                searchView.findViewById(R.id.search_src_text);
        searchBox.setAdapter(new SuggestionAdapter(this, R.layout.complete_row, adapterList));
        searchBox.setThreshold(0);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterList.clear();
                for (SearchableItem item : searchItems) {
                    if (item.getType().equals(getString(R.string.room)) &&
                            (item.getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                                    item.getBuilding().toLowerCase().contains(s.toString().toLowerCase()) ||
                                    item.getFloor().toLowerCase().contains(s.toString().toLowerCase()) ||
                                    item.getRoom().toLowerCase().contains(s.toString().toLowerCase()))) {
                        adapterList.add(item);
                    } else if (item.getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                            item.getBuilding().toLowerCase().contains(s.toString().toLowerCase()) ||
                            item.getFloor().toLowerCase().contains(s.toString().toLowerCase())) {
                        adapterList.add(item);
                    }
                }
                ((SuggestionAdapter) searchBox.getAdapter()).refresh(adapterList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapterList.size() == 0) {
                    Utils.hideKeyboard(MainActivityLogic.this);
                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.empty_search, Snackbar.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        String title = getString(R.string.empty);
        Fragment fragment = null;

        final MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(maps);

        if (mapsFragment != null && mapsFragment.mapRoute != null && mapsFragment.mapRoute.hasCurrentPath) {
            mapsFragment.mapRoute.finishRoute(false);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (id == R.id.nav_maps) {
                fragment = new MapsFragment();
                title = maps;
            } else if (id == R.id.nav_favourite) {
                fragment = new FavouriteFragment();
                title = fav;
            } else if (id == R.id.nav_event) {
                fragment = new EventsFragment();
                title = events;
            } else if (id == R.id.nav_about) {
                fragment = new About();
                title = about;
            }
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
        } else {
            if (id == R.id.nav_maps) {
                title = maps;
                if (getSupportFragmentManager().findFragmentByTag(maps) != null) {
                    getSupportFragmentManager().popBackStackImmediate(maps, 0);
                } else {
                    fragment = new MapsFragment();
                }
            } else if (id == R.id.nav_favourite) {
                title = fav;
                if (getSupportFragmentManager().findFragmentByTag(fav) != null) {
                    getSupportFragmentManager().popBackStackImmediate(fav, 0);
                } else {
                    fragment = new FavouriteFragment();
                }
            } else if (id == R.id.nav_event) {
                title = events;
                if (getSupportFragmentManager().findFragmentByTag(events) != null) {
                    getSupportFragmentManager().popBackStackImmediate(events, 0);
                } else {
                    fragment = new EventsFragment();
                }

            } else if (id == R.id.nav_about) {
                title = about;
                if (getSupportFragmentManager().findFragmentByTag(about) != null) {
                    getSupportFragmentManager().popBackStackImmediate(about, 0);
                } else {
                    fragment = new About();
                }
            } else if (id == R.id.nav_auth) {
                title = log_in;
                if (getSupportFragmentManager().findFragmentByTag(log_in) != null) {
                    getSupportFragmentManager().popBackStackImmediate(log_in, 0);
                } else {
                    fragment = new Login();
                }
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.content_frame, fragment, title).addToBackStack(title);
            ft.commit();
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getTitle());
        return true;
    }

    public void highlightItemDrawer(String title) {
        // TODO: HashMap is not needed here, could be removed +
        String[] titles = {maps, fav, events, about};
        for (int i = 0; i < titles.length; i++)
            if (titles[i].equals(title)) navigationView.getMenu().getItem(i).setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MainActivity.getInstance().trackScreenView(getString(R.string.main_fragment));
    }
}
