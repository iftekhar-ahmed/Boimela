package org.melayjaire.boimela;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import org.melayjaire.boimela.ui.BookListFragment;
import org.melayjaire.boimela.utils.JsonTaskCompleteListener;
import org.melayjaire.boimela.utils.LocationHelper;
import org.melayjaire.boimela.utils.NetworkHelper;
import org.melayjaire.boimela.utils.Utilities;
import org.melayjaire.boimela.utils.VolleyHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements
        JsonTaskCompleteListener<JSONArray>, View.OnClickListener {

    private View actionBarRefreshProgressView;
    private MenuItem menuItemRefresh;
    private Toolbar toolbar;
    private SearchView searchView;
    private FloatingActionButton fab;
    private LocationHelper locationHelper;
    private SharedPreferences preference;
    private OnSearchIntentCallback onSearchIntentCallback;

    public static final String EXTRA_TAG_CATEGORY = "extra_tag_category";
    public static final int GPS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preference = PreferenceManager.getDefaultSharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        toolbar.setTitle(Utilities.getBanglaSpannableString(getString(R.string.app_name), this));
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab_locate_books);
        fab.setOnClickListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BookListFragment bookListFragment = BookListFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_container, bookListFragment).commit();
        onSearchIntentCallback = bookListFragment;
        onSearchIntentCallback.onNewIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_activity, menu);
        menuItemRefresh = menu.findItem(R.id.action_refresh);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItemSearch);
        searchView.setQueryHint(Utilities.getBanglaSpannableString(getString(R.string.search), this));
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
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
                break;
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        onSearchIntentCallback.onNewIntent(intent);
    }

    private void locateBooks() {
        if (preference.getBoolean(Utilities.GPS_TRACKING, false)) {
            Utilities.saveGpsSetting(this, false);
            if (locationHelper != null) {
                locationHelper.stopGpsTracking();
            }
        } else {
            if (!Utilities.isGpsEnabled(getBaseContext())) {
                new MaterialDialog.Builder(this)
                        .title(Utilities.getBanglaSpannableString(getString(R.string.notice), this))
                        .content(Utilities.getBanglaSpannableString(getString(R.string.gps_alarm_msg), this))
                        .positiveText(Utilities.getBanglaSpannableString(getString(R.string.yes), this))
                        .positiveColorRes(R.color.material_blue_grey_800)
                        .negativeText(Utilities.getBanglaSpannableString(getString(R.string.no), this))
                        .negativeColorRes(R.color.material_blue_grey_800)
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
                return;
            } else {
                locationHelper = new LocationHelper(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            if (!Utilities.isGpsEnabled(getBaseContext())) {
                Toast.makeText(this, Utilities.getBanglaSpannableString(getString(R.string.no_gps_set), this), Toast.LENGTH_SHORT).show();
                Utilities.saveGpsSetting(this, false);
            } else {
                locationHelper = new LocationHelper(this);
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
            Utilities.storeMaxBookIndex(this, bookIndexes);
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

    public interface OnSearchIntentCallback {
        void onNewIntent(Intent intent);
    }
}
