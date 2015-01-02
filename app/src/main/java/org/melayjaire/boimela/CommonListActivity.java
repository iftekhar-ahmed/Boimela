package org.melayjaire.boimela;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.melayjaire.boimela.adapter.SingleItemRVCursorAdapter;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.loader.CommonCursorLoader;
import org.melayjaire.boimela.model.SearchType;
import org.melayjaire.boimela.utils.SearchCategoryMap;
import org.melayjaire.boimela.utils.Utilities;
import org.melayjaire.boimela.view.RecyclerItemClickListener;

public class CommonListActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    private View listLoadProgressView;
    private RecyclerView mRecyclerView;
    private ActionBar actionBar;

    private SingleItemRVCursorAdapter singleItemRVCursorAdapter;
    private BookDataSource dataSource;
    private SearchType searchType;
    private Cursor result;

    private String sCategory;
    private String[] from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_books);
        listLoadProgressView = findViewById(R.id.load_status);

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

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        singleItemRVCursorAdapter = new SingleItemRVCursorAdapter(this, null) {
            @Override
            public void onBind(ViewHolder viewHolder, Cursor cursor) {
                String itemText = cursor.getString(cursor.getColumnIndex(from[columnsArraySize - 1]));
                viewHolder.textView.setBanglaText(itemText);
            }
        };
        mRecyclerView.setAdapter(singleItemRVCursorAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(CommonListActivity.this, BookListActivity.class);
                i.putExtra(HomeActivity.EXTRA_TAG_CATEGORY, sCategory);
                Uri name = Uri.parse(result.getString(result.getColumnIndex(from[1])));
                i.setData(name);
                i.setAction("android.intent.action.VIEW");
                startActivity(i);
            }
        }));

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        Utilities.showListLoadProgress(this, mRecyclerView, listLoadProgressView, true);
        return new CommonCursorLoader(this, dataSource, searchType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        result = data;
        singleItemRVCursorAdapter.swapCursor(data);
        Utilities.showListLoadProgress(this, mRecyclerView, listLoadProgressView, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        singleItemRVCursorAdapter.swapCursor(null);
    }
}
