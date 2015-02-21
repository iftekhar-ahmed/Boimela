package org.melayjaire.boimela;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.search.SearchCriteria;
import org.melayjaire.boimela.search.SearchFilter;
import org.melayjaire.boimela.service.BookTrackerService;
import org.melayjaire.boimela.ui.ActionBarDrawerFragment;
import org.melayjaire.boimela.ui.BookListFragment;
import org.melayjaire.boimela.utils.Constants;
import org.melayjaire.boimela.utils.JsonTaskCompleteListener;
import org.melayjaire.boimela.utils.PreferenceHelper;
import org.melayjaire.boimela.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements
        JsonTaskCompleteListener<JSONArray>, View.OnClickListener, ActionBarDrawerFragment.OnClickListener {

    private View actionBarRefreshProgressView;
    private MenuItem menuItemRefresh, menuItemSearch;
    private Toolbar toolbar;
    private SearchView searchView;
    private SearchCriteria searchCriteria;
    private SearchFilter searchFilter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FloatingActionButton fab;
    private PreferenceHelper preferenceHelper;
    private OnBookSearchListener onBookSearchListener;
    private SimpleCursorAdapter searchSuggestionsAdapter;

    public static final int GPS_REQUEST_CODE = 1;

    private static final String ARG_TITLE = "_arg_title";

    private void switchFab(boolean on) {
        if (fab == null) {
            return;
        }
        if (on) {
            fab.setColorNormalResId(R.color.holo_red_dark);
            fab.setColorPressedResId(R.color.holo_red_light);
            fab.setIcon(R.drawable.ic_gps_fixed);
        } else {
            fab.setColorNormalResId(R.color.holo_blue_dark);
            fab.setColorPressedResId(R.color.holo_blue_light);
            fab.setIcon(R.drawable.ic_gps_not_fixed);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferenceHelper = PreferenceHelper.getInstance(this);

        String title = getString(R.string.all_books);

        if (savedInstanceState != null) {
            title = savedInstanceState.getCharSequence(ARG_TITLE).toString();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        toolbar.setTitle(Utilities.getBanglaSpannableString(title, this));
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab_locate_books);
        fab.setOnClickListener(this);
        switchFab(Utilities.isServiceRunning(this, BookTrackerService.class));
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name_bangla, R.string.app_name_bangla);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        searchCriteria = null;
        searchFilter = SearchFilter.Title.withQuery("", false, false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BookListFragment bookListFragment = BookListFragment.newInstance(searchCriteria, searchFilter);
        fragmentTransaction.add(R.id.fragment_container, bookListFragment);
        ActionBarDrawerFragment actionBarDrawerFragment = ActionBarDrawerFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_drawer_container, actionBarDrawerFragment);
        fragmentTransaction.commit();
        onBookSearchListener = bookListFragment;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(ARG_TITLE, toolbar.getTitle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }
        if (MenuItemCompat.isActionViewExpanded(menuItemSearch)) {
            MenuItemCompat.collapseActionView(menuItemSearch);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_activity, menu);
        menu.findItem(R.id.menu_filter_title).setTitle(Utilities.getBanglaSpannableString(getString(R.string.title), this));
        menu.findItem(R.id.menu_filter_author).setTitle(Utilities.getBanglaSpannableString(getString(R.string.author), this));
        menu.findItem(R.id.menu_filter_publisher).setTitle(Utilities.getBanglaSpannableString(getString(R.string.publisher), this));
        // menuItemRefresh = menu.findItem(R.id.action_refresh);
        menuItemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItemSearch);
        searchView.setQueryHint(Utilities.getBanglaSpannableString(getString(R.string.search), this));
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchSuggestionsAdapter = new SimpleCursorAdapter(this, R.layout.list_item_search_suggestion
                , null, new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1}, new int[]{R.id.textView_title}, 0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                onBookSearchListener.searchForBooks(searchCriteria, searchFilter.withQuery(s, false, true));
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchSuggestionsAdapter.changeCursor(onBookSearchListener.getSearchSuggestions(searchCriteria
                        , searchFilter.withQuery(s, false, false)));
                searchSuggestionsAdapter.notifyDataSetChanged();
                return true;
            }
        });
        searchView.setSuggestionsAdapter(searchSuggestionsAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return onSuggestionClick(i);
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Cursor cursor = (Cursor) searchSuggestionsAdapter.getItem(i);
                String suggestion_text = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA));
                onBookSearchListener.searchForBooks(searchCriteria, searchFilter.withQuery(suggestion_text, false, true));
                searchView.clearFocus();
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchSuggestionsAdapter.changeCursor(null);
                onBookSearchListener.searchForBooks(searchCriteria, searchFilter.withQuery("", false, false));
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_refresh:
                if (NetworkHelper.isNetworkAvailable(this)) {
                    VolleyHelper volleyHelper = VolleyHelper
                            .getInstance(this, this);
                    volleyHelper.setUpApi(this);
                    volleyHelper.getJsonArray();
                    showRefreshProgress(true);
                } else {
                    Toast.makeText(this, Utilities.getBanglaSpannableString(getString(R.string.no_internet_connection)
                            , this), Toast.LENGTH_SHORT).show();
                }
                break;*/
            case R.id.menu_filter_title:
                searchFilter = SearchFilter.Title;
                item.setChecked(!item.isChecked());
                break;
            case R.id.menu_filter_author:
                searchFilter = SearchFilter.Author;
                item.setChecked(!item.isChecked());
                break;
            case R.id.menu_filter_publisher:
                searchFilter = SearchFilter.Publisher;
                item.setChecked(!item.isChecked());
                break;
        }
        return true;
    }

    private void locateBooks() {
        if (Utilities.isGpsEnabled(getBaseContext())) {
            if (Utilities.isServiceRunning(this, BookTrackerService.class)) {
                stopService(new Intent(this, BookTrackerService.class));
                switchFab(false);
            } else {
                startService(new Intent(this, BookTrackerService.class));
                switchFab(true);
            }
        } else {
            new MaterialDialog.Builder(this)
                    .title(Utilities.getBanglaSpannableString(getString(R.string.notice), this))
                    .content(Utilities.getBanglaSpannableString(getString(R.string.gps_alarm_msg), this))
                    .positiveText(Utilities.getBanglaSpannableString(getString(R.string.yes), this))
                    .positiveColorRes(R.color.material_blue_grey_800)
                    .negativeText(Utilities.getBanglaSpannableString(getString(R.string.no), this))
                    .negativeColorRes(R.color.material_blue_grey_800)
                    .backgroundColor(getResources().getColor(android.R.color.white))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            final Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            gpsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivityForResult(gpsIntent, HomeActivity.GPS_REQUEST_CODE);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            if (Utilities.isGpsEnabled(getBaseContext())) {
                startService(new Intent(this, BookTrackerService.class));
                switchFab(true);
            }
        }
    }

    @Override
    public void onJsonArray(JSONArray result) {
        if (result.length() <= 0) {
            showRefreshProgress(false);
            Toast.makeText(this, Utilities.getBanglaSpannableString(getString(R.string.no_new_book), this), Toast.LENGTH_SHORT).show();
            return;
        }
        List<Book> books = new ArrayList<>();
        List<Long> bookIndexes = new ArrayList<>();
        try {
            for (int i = 0; i < result.length(); i++) {
                Book book = new Book();
                JSONObject jsonObject = (JSONObject) result.get(i);
                book.setTitle(jsonObject.getString("Title"));
                book.setTitleInEnglish(jsonObject.getString("TitleInEnglish"));
                book.setAuthor(jsonObject.getString("Author"));
                book.setAuthorInEnglish(jsonObject.getString("AuthorInEnglish"));
                book.setCategory(jsonObject.getString("Catagory"));
                book.setPublisher(jsonObject.getString("Publisher"));
                book.setPublisherInEnglish(jsonObject
                        .getString("PublisherInEnglish"));
                book.setPrice(jsonObject.getString("Price"));
                book.setDescription(jsonObject.getString("Description"));
                book.setStallLatitude(Double.parseDouble(jsonObject
                        .getString("StallLat")));
                book.setStallLongitude(Double.parseDouble(jsonObject
                        .getString("StallLong")));
                book.setNew(Boolean.parseBoolean(jsonObject.getString("IsNew")));
                bookIndexes.add(jsonObject.getLong("Index"));
                books.add(book);
            }
            preferenceHelper.setLong(Constants.PREF_KEY_MAX_BOOK_INDEX, Collections.max(bookIndexes));
            bookIndexes.clear();
            // dataSource.insert(books);
            books.clear();
            showRefreshProgress(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRefreshProgress(boolean show) {
        if (show) {
            if (actionBarRefreshProgressView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                actionBarRefreshProgressView = inflater.inflate(
                        R.layout.refresh_progress, null);
            }
            MenuItemCompat.setActionView(menuItemRefresh,
                    actionBarRefreshProgressView);
        } else {
            MenuItemCompat.setActionView(menuItemRefresh, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_locate_books:
                locateBooks();
        }
    }

    @Override
    public void onItemClick(View view) {
        drawerLayout.closeDrawers();
        switch (view.getId()) {
            case R.id.drawer_item_all_books:
                searchCriteria = null;
                toolbar.setTitle(Utilities.getBanglaSpannableString(getString(R.string.all_books), this));
                break;
            case R.id.drawer_item_new_books:
                searchCriteria = SearchCriteria.NewBooks;
                toolbar.setTitle(Utilities.getBanglaSpannableString(getString(R.string.new_book), this));
                break;
            case R.id.drawer_item_favorite_books:
                searchCriteria = SearchCriteria.Favorites;
                toolbar.setTitle(Utilities.getBanglaSpannableString(getString(R.string.favorite_books), this));
                break;
        }
        onBookSearchListener.searchForBooks(searchCriteria, null);
    }
}
