package org.melayjaire.boimela;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.loader.CommonCursorLoader;
import org.melayjaire.boimela.model.SearchType;
import org.melayjaire.boimela.utils.SearchCategoryMap;
import org.melayjaire.boimela.utils.Utilities;

public class CommonListActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    private View listLoadProgressView;
    private ListView mListView;
    private ActionBar actionBar;

    private SimpleCursorAdapter cursorAdapter;
    private BookDataSource dataSource;
    private SearchType searchType;
    private Cursor result;

    private String sCategory;
    private String[] from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        listLoadProgressView = (View) findViewById(R.id.load_status);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (dataSource == null) {
            dataSource = new BookDataSource(this);
            dataSource.open();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sCategory = extras.getString(HomeActivity.EXTRA_TAG_CATEGORY);
            searchType = new SearchCategoryMap(this).obtain(sCategory);
            from = dataSource.getCursorColumns(searchType);
        }

        actionBar.setTitle(Utilities.getBanglaSpannableString(sCategory, this));

        final int columnsArraySize = from.length;

        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.list_item_common,
                null, new String[]{from[columnsArraySize - 1]},
                new int[]{R.id.tv_list_item_common}, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Cursor cursor = getCursor();
                cursor.moveToPosition(position);
                String itemText = cursor.getString(cursor.getColumnIndex(from[columnsArraySize - 1]));
                View v = super.getView(position, convertView, parent);
                ((TextView) v.findViewById(R.id.tv_list_item_common)).setText(Utilities.getBanglaSpannableString(itemText, CommonListActivity.this));
                return v;
            }
        };
        getListView().setAdapter(cursorAdapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parentView, View v, int position, long id) {
                Intent i = new Intent(CommonListActivity.this, BookListActivity.class);
                i.putExtra(HomeActivity.EXTRA_TAG_CATEGORY, sCategory);
                Uri name = Uri.parse(result.getString(result.getColumnIndex(from[1])));
                i.setData(name);
                i.setAction("android.intent.action.VIEW");
                startActivity(i);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    private ListView getListView() {
        if (mListView == null)
            mListView = (ListView) findViewById(R.id.listView);
        return mListView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        Utilities.showListLoadProgress(this, getListView(), listLoadProgressView, true);
        return new CommonCursorLoader(this, dataSource, searchType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        result = data;
        cursorAdapter.swapCursor(data);
        Utilities.showListLoadProgress(this, getListView(), listLoadProgressView, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
