package org.melayjaire.boimela;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.utils.JsonTaskCompleteListener;
import org.melayjaire.boimela.utils.LocationHelper;
import org.melayjaire.boimela.utils.NetworkHelper;
import org.melayjaire.boimela.utils.Utilities;
import org.melayjaire.boimela.utils.VolleyHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements
        JsonTaskCompleteListener<JSONArray>, OnClickListener {

    public static final String EXTRA_TAG_CATEGORY = "extra_tag_category";
    public static final int GPS_REQUEST_CODE = 1;

    private MenuItem menuItemRefresh;
    private Button favoriteBookLocatorButton;
    private View bookListRefreshProgressView;
    private ActionBar actionBar;
    private ScrollView relativeLayoutSplashScreen;
    private MenuItem menuItemSearch;

    private LocationHelper locationHelper;
    private BookDataSource dataSource;

    private boolean showSplashScreen;
    private SharedPreferences preference;

    private TextView spHeading, splashTextOne, splashTextTwo, splashTextThree,
            splashTextFour, splashTextFive, splashTextSix;
    private Button spCorrect;

    @Override
    protected void onStart() {
        super.onStart();
        showSplashScreen = preference.getBoolean(
                getString(R.string.pref_key_splash_screen), false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        favoriteBookLocatorButton = (Button) findViewById(R.id.btn_locate_favorite_book);
        relativeLayoutSplashScreen = (ScrollView) findViewById(R.id.splash_screen);

        if (!showSplashScreen) {
            spHeading = (TextView) findViewById(R.id.splash_headline);
            splashTextOne = (TextView) findViewById(R.id.splash_text_one);
            splashTextTwo = (TextView) findViewById(R.id.splash_text_two);
            splashTextThree = (TextView) findViewById(R.id.splash_text_three);
            splashTextFour = (TextView) findViewById(R.id.splash_text_four);
            splashTextFive = (TextView) findViewById(R.id.splash_text_five);
            splashTextSix = (TextView) findViewById(R.id.splash_text_six);
            spCorrect = (Button) findViewById(R.id.splash_correct);
        }

        actionBar = getSupportActionBar();
        actionBar.setTitle(Utilities.getBanglaSpannableString(getString(R.string.app_name), this));

        ((Button) findViewById(R.id.btn_author))
                .setText(Utilities.getBanglaSpannableString(getString(R.string.author), this));
        ((Button) findViewById(R.id.btn_publisher))
                .setText(Utilities.getBanglaSpannableString(getString(R.string.publisher), this));
        ((Button) findViewById(R.id.btn_catagory))
                .setText(Utilities.getBanglaSpannableString(getString(R.string.category), this));
        ((Button) findViewById(R.id.btn_new_book))
                .setText(Utilities.getBanglaSpannableString(getString(R.string.new_book), this));
        ((Button) findViewById(R.id.btn_favorite_books))
                .setText(Utilities.getBanglaSpannableString(getString(R.string.favorite_books), this));

        if (dataSource == null) {
            dataSource = new BookDataSource(this);
            dataSource.open();
        }
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_activity, menu);

        menuItemRefresh = menu.findItem(R.id.action_refresh);
        menuItemSearch = menu.findItem(R.id.action_search);
        if (!showSplashScreen) {
            menuItemRefresh.setEnabled(false);
            menuItemSearch.setEnabled(false);
        } else {
            menuItemRefresh.setEnabled(true);
            menuItemSearch.setEnabled(true);
        }
        return super.onCreateOptionsMenu(menu);
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
                    Toast.makeText(this, Utilities.getBanglaSpannableString(getString(R.string.no_internet_connection), this), Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_search:
                Intent i = new Intent(HomeActivity.this, BookListActivity.class);
                i.setAction("android.intent.action.SEARCH");
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (preference.getBoolean(Utilities.GPS_TRACKING, false)
                & Utilities.isGpsEnabled(this)) {
            favoriteBookLocatorButton.setText(Utilities.getBanglaSpannableString(getString(R.string.gps_stop), this));
        } else {
            favoriteBookLocatorButton.setText(Utilities.getBanglaSpannableString(getString(R.string.gps_search), this));
        }

        if (!showSplashScreen) {

            Utilities.setAlpha(findViewById(R.id.splash_screen), .9f);
            // Utilities.setAlpha(Utilities.getActionBarView(this, getWindow()),
            // .9f);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color
                    .parseColor("#BB000000")));

            preference
                    .edit()
                    .putBoolean(getString(R.string.pref_key_splash_screen),
                            true).commit();

            relativeLayoutSplashScreen.setVisibility(View.VISIBLE);

            spHeading.setText(Utilities.getBanglaSpannableString(getString(R.string.splash_headline), this));

            splashTextOne.setText(Utilities.getBanglaSpannableString(getString(R.string.splash_text_one), this));

            splashTextTwo.setText(Utilities.getBanglaSpannableString(getString(R.string.splash_text_two), this));
            splashTextThree.setText(Utilities.getBanglaSpannableString(getString(R.string.splash_text_three), this));
            splashTextFour.setText(Utilities.getBanglaSpannableString(getString(R.string.splash_text_four), this));
            splashTextFive.setText(Utilities.getBanglaSpannableString(getString(R.string.splash_text_five), this));
            splashTextSix.setText(Utilities.getBanglaSpannableString(getString(R.string.splash_text_six), this));
            spCorrect.setText(Utilities.getBanglaSpannableString(getString(R.string.splash_correct), this));

        } else {
            relativeLayoutSplashScreen.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        Intent i = new Intent(HomeActivity.this, CommonListActivity.class);

        switch (v.getId()) {
            case R.id.splash_correct:
                relativeLayoutSplashScreen.setVisibility(View.GONE);
                menuItemRefresh.setEnabled(true);
                menuItemSearch.setEnabled(true);
                break;
            case R.id.btn_author:
                i.putExtra(EXTRA_TAG_CATEGORY, getString(R.string.author));
                startActivity(i);
                break;
            case R.id.btn_publisher:
                i.putExtra(EXTRA_TAG_CATEGORY, getString(R.string.publisher));
                startActivity(i);
                break;
            case R.id.btn_catagory:
                i.putExtra(EXTRA_TAG_CATEGORY, getString(R.string.category));
                startActivity(i);
                break;
            case R.id.btn_new_book:
                i = new Intent(HomeActivity.this, BookListActivity.class);
                i.putExtra(EXTRA_TAG_CATEGORY, getString(R.string.new_book));
                startActivity(i);
                break;
            case R.id.btn_favorite_books:
                i = new Intent(HomeActivity.this, BookListActivity.class);
                i.putExtra(EXTRA_TAG_CATEGORY, getString(R.string.favorite_books));
                startActivity(i);
                break;

            case R.id.btn_locate_favorite_book:

                if (preference.getBoolean(Utilities.GPS_TRACKING, false)) {
                    favoriteBookLocatorButton.setText(Utilities.getBanglaSpannableString(getString(R.string.gps_search), this));
                    Utilities.saveGpsSetting(this, false);
                    if (locationHelper != null) {
                        locationHelper.stopGpsTracking();
                    }
                } else {
                    if (!Utilities.isGpsEnabled(getBaseContext())) {
                        new GpsSettingScreen(this);
                        return;
                    } else {
                        locationHelper = new LocationHelper(this);
                        favoriteBookLocatorButton.setText(Utilities.getBanglaSpannableString(getString(R.string.gps_stop), this));
                    }

                }

                break;
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
        List<Book> books = new ArrayList<Book>();
        List<Long> bookIndexes = new ArrayList<Long>();
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
            dataSource.insert(books);
            books.clear();
            showRefreshProgress(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRefreshProgress(boolean show) {
        if (show) {
            if (bookListRefreshProgressView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                bookListRefreshProgressView = inflater.inflate(
                        R.layout.refresh_progress, null);
            }
            MenuItemCompat.setActionView(menuItemRefresh,
                    bookListRefreshProgressView);
        } else {
            MenuItemCompat.setActionView(menuItemRefresh, null);
        }
    }
}
