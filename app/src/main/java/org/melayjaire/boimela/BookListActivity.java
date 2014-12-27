package org.melayjaire.boimela;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.melayjaire.boimela.adapter.BookListAdapter;
import org.melayjaire.boimela.adapter.FavoriteCheckListener;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.loader.BookListLoader;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.search.SearchType;
import org.melayjaire.boimela.search.SearchCategoryMap;
import org.melayjaire.boimela.utils.Utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.melayjaire.boimela.data.BookDatabaseHelper.CATEGORY;

public class BookListActivity extends ActionBarActivity implements
        LoaderCallbacks<List<Book>>, FavoriteCheckListener {

    private Spinner spinnerBookCategory;
    private MenuItem menuItemSearch, menuItemBookCategory,
            menuItemCancelSpinnerView;
    private View bookListLoadProgressView;
    private ListView bookListView;
    private SearchView searchView;
    private ActionBar actionBar;

    private BookDataSource dataSource;
    private SimpleCursorAdapter bookCategoryCursorAdapter;
    private BookListAdapter bookListAdapter;
    private SearchType searchType;
    private SharedPreferences preferences;

    private boolean iconifySearchView, showActionBarMenuItems;
    private String queryFilter;

    OnCloseListener searchCloseListener = new OnCloseListener() {

        @Override
        public boolean onClose() {
            searchType = SearchType.Title;
            preferences
                    .edit()
                    .putString(getString(R.string.pref_key_search_category),
                            getString(R.string.title)).commit();
            searchView.setQueryHint(getString(R.string.enter_book_name));
            searchView.onActionViewCollapsed();
            getSupportLoaderManager().restartLoader(0, null,
                    BookListActivity.this);
            return true;
        }
    };

    OnItemSelectedListener bookCategorySelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view,
                                   int position, long id) {
            Cursor cur = (Cursor) spinnerBookCategory
                    .getItemAtPosition(position);
            queryFilter = cur.getString(cur.getColumnIndex(CATEGORY));
            getSupportLoaderManager().restartLoader(0, null,
                    BookListActivity.this);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            searchType = new SearchCategoryMap(this).obtain(extras
                    .getString(HomeActivity.EXTRA_TAG_CATEGORY));
            iconifySearchView = true;
            showActionBarMenuItems = false;
        } else {
            searchType = SearchType.Title;
            iconifySearchView = false;
            showActionBarMenuItems = true;
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bookListLoadProgressView = (View) findViewById(R.id.load_status);
        bookListView = (ListView) findViewById(R.id.listView);

        if (dataSource == null) {
            dataSource = new BookDataSource(this);
            dataSource.open();
        }

        bookListAdapter = new BookListAdapter(this, R.layout.list_item_book,
                new ArrayList<Book>(), this);
        bookListView.setAdapter(bookListAdapter);

        bookCategoryCursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                dataSource.getInCursor(SearchType.Category),
                new String[]{CATEGORY}, new int[]{android.R.id.text1}, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(getResources().getColor(
                        R.color.White));
                ((TextView) v).setTypeface(Utilities
                        .getBanglaFont(BookListActivity.this));
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(getResources().getColor(
                        R.color.Black));
                ((TextView) v).setTypeface(Utilities
                        .getBanglaFont(BookListActivity.this));
                return v;
            }
        };
        bookCategoryCursorAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        actionBar = getSupportActionBar();
        actionBar.setTitle(Utilities.getBanglaSpannableString(getString(R.string.title), this));
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (showActionBarMenuItems)
            getOverflowMenu();
        getSupportLoaderManager().initLoader(0, null, this);
        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return showActionBarMenuItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_book_actions, menu);

        menuItemSearch = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItemSearch);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setIconified(iconifySearchView);
        searchView.setOnCloseListener(searchCloseListener);
        searchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showMenuItems(false, true, false);
                searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                searchView.requestFocus();
            }
        });
        Utilities.overrideFont(searchView,
                Utilities.getBanglaFont(BookListActivity.this));

        menuItemBookCategory = menu.findItem(R.id.action_view_category);
        spinnerBookCategory = (Spinner) MenuItemCompat
                .getActionView(menuItemBookCategory);
        spinnerBookCategory.setAdapter(bookCategoryCursorAdapter);
        spinnerBookCategory
                .setOnItemSelectedListener(bookCategorySelectedListener);

        menuItemCancelSpinnerView = menu
                .findItem(R.id.action_cancel_spinner_menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;

            case R.id.action_search_by_author:
                searchType = SearchType.Author;
                preferences
                        .edit()
                        .putString(getString(R.string.pref_key_search_category),
                                getString(R.string.author)).commit();
                searchView.setQueryHint(getString(R.string.enter_author_name));
                searchView.setIconified(false);
                return true;

            case R.id.action_search_by_publisher:
                searchType = SearchType.Publisher;
                preferences
                        .edit()
                        .putString(getString(R.string.pref_key_search_category),
                                getString(R.string.publisher)).commit();
                searchView.setQueryHint(getString(R.string.enter_publisher_name));
                searchView.setIconified(false);
                return true;

            case R.id.action_search_by_category:
                searchType = SearchType.Category;
                showMenuItems(true, false, true);
                spinnerBookCategory.setAdapter(bookCategoryCursorAdapter);
                return true;

            case R.id.action_cancel_spinner_menu:
                showMenuItems(false, true, false);
                searchView.setIconified(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (searchView != null) {
            if (searchView.isIconified()) {
                super.onBackPressed();
            } else {
                searchView.setIconified(true);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        String query = null;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            query = intent.getData().toString();
        }

        if (!TextUtils.isEmpty(query) || query != null) {
            queryFilter = query;
            Log.d("query", queryFilter);
            getSupportLoaderManager().restartLoader(0, null, this);
        }
    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * show/hide or switch between menu items NOT located in the overflow menu
     */
    private void showMenuItems(boolean bookCategory, boolean searchWidget,
                               boolean cancelSpinnerView) {

        menuItemBookCategory.setVisible(bookCategory);
        searchView.setVisibility(searchWidget ? View.VISIBLE : View.GONE);
        menuItemCancelSpinnerView.setVisible(cancelSpinnerView);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle data) {
        Utilities.showListLoadProgress(this, bookListView,
                bookListLoadProgressView, true);
        return new BookListLoader(this, dataSource, searchType, queryFilter);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        if (data.isEmpty()) {
            Toast.makeText(this, Utilities.getBanglaSpannableString(getString(R.string.no_record_found), this), Toast.LENGTH_SHORT).
                    show();
        } else {
            bookListAdapter.swapList(data);
        }
        Utilities.showListLoadProgress(this, bookListView,
                bookListLoadProgressView, false);
        queryFilter = null;
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        bookListAdapter.swapList(null);
    }

    @Override
    public void onFavoriteCheckedChange(Book book, boolean isFavorite) {

        if (book.isFavorite() == isFavorite)
            return;

        if (isFavorite) {
            Toast.makeText(this, Utilities.getBanglaSpannableString(getString(R.string.favorite_book_added), this), Toast.LENGTH_SHORT).show();
        }
        book.setFavorite(isFavorite);
        dataSource.update(book);
    }
}
