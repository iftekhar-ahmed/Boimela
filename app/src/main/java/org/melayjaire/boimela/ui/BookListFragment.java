package org.melayjaire.boimela.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.melayjaire.boimela.OnBookQueryListener;
import org.melayjaire.boimela.R;
import org.melayjaire.boimela.adapter.BookListAdapter;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.loader.BookListLoader;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Book>>, OnBookQueryListener
        , BookListAdapter.FavoriteCheckedListener, BookListAdapter.OnItemClickListener {

    private String queryFilter;
    private View bookListLoadProgressView;
    private RecyclerView mRecyclerView;
    private BookDataSource dataSource;
    private BookListAdapter bookListAdapter;

    public static BookListFragment newInstance() {
        BookListFragment fragment = new BookListFragment();
        return fragment;
    }

    public BookListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookListAdapter = new BookListAdapter(getActivity(), new ArrayList<Book>(), this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        bookListLoadProgressView = view.findViewById(R.id.load_status);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_books);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(bookListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (dataSource == null) {
            dataSource = new BookDataSource(getActivity());
            dataSource.open();
        }
    }

    @Override
    public void onDetach() {
        dataSource.close();
        super.onDetach();
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle data) {
        Utilities.showListLoadProgress(getActivity(), mRecyclerView,
                bookListLoadProgressView, true);
        return new BookListLoader(getActivity(), dataSource, null, queryFilter);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        if (data.isEmpty()) {
            Toast.makeText(getActivity(), Utilities.getBanglaSpannableString(getString(R.string.no_record_found)
                    , getActivity()), Toast.LENGTH_SHORT).
                    show();
        } else {
            bookListAdapter.swapList(data);
        }
        Utilities.showListLoadProgress(getActivity(), mRecyclerView,
                bookListLoadProgressView, false);
        queryFilter = null;
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        bookListAdapter.swapList(null);
    }

    @Override
    public void onFavoriteCheckedChange(Book book, boolean isFavorite) {
        if (book.isFavorite() == isFavorite) {
            return;
        }
        book.setFavorite(isFavorite);
        dataSource.update(book);
    }

    @Override
    public void onItemClick(Book book) {
        BookDetailFragment bookDetailFragment = BookDetailFragment.newInstance(book);
        bookDetailFragment.show(getFragmentManager(), "Detail");
    }

    @Override
    public void listBooksWithQuery(String queryText) {
        queryFilter = queryText;
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Cursor getSuggestionsForQuery(String queryText) {
        return dataSource.getSearchSuggestions(queryText);
    }
}
