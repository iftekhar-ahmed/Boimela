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

import org.melayjaire.boimela.OnBookSearchListener;
import org.melayjaire.boimela.R;
import org.melayjaire.boimela.adapter.AuthorListAdapter;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.loader.AuthorListLoader;
import org.melayjaire.boimela.model.Author;
import org.melayjaire.boimela.search.SearchCriteria;
import org.melayjaire.boimela.search.SearchFilter;
import org.melayjaire.boimela.utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iftekhar on 2/24/2015.
 */
public class AuthorListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Author>>, OnBookSearchListener, AuthorListAdapter.OnItemClickListener {

    private View authorListLoadProgressView;
    private RecyclerView mRecyclerView;
    private BookDataSource dataSource;
    private AuthorListAdapter authorListAdapter;

    public AuthorListFragment() {
    }

    public static AuthorListFragment newInstance() {
        return new AuthorListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authorListAdapter = new AuthorListAdapter(getActivity(), new ArrayList<Author>(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        authorListLoadProgressView = view.findViewById(R.id.load_status);
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
        mRecyclerView.setAdapter(authorListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataSource = new BookDataSource(activity);
        dataSource.open();
    }

    @Override
    public void onDetach() {
        dataSource.close();
        super.onDetach();
    }

    @Override
    public Loader<List<Author>> onCreateLoader(int id, Bundle data) {
        Utilities.showListLoadProgress(getActivity(), mRecyclerView,
                authorListLoadProgressView, true);
        SearchCriteria searchCriteria = null;
        SearchFilter searchFilter = null;
        if (data != null && data.containsKey(ARG_SEARCH_CATEGORY) && data.containsKey(ARG_SEARCH_FILTER)) {
            Serializable category = data.getSerializable(ARG_SEARCH_CATEGORY);
            if (category != null) {
                searchCriteria = (SearchCriteria) category;
            }
            Serializable filter = data.getSerializable(ARG_SEARCH_FILTER);
            if (filter != null) {
                searchFilter = (SearchFilter) filter;
            }
        }
        return new AuthorListLoader(getActivity(), dataSource, searchCriteria, searchFilter);
    }

    @Override
    public void onLoadFinished(Loader<List<Author>> listLoader, List<Author> authors) {
        authorListAdapter.swapList(authors);
        Utilities.showListLoadProgress(getActivity(), mRecyclerView,
                authorListLoadProgressView, false);
    }

    @Override
    public void onLoaderReset(Loader<List<Author>> listLoader) {
        authorListAdapter.swapList(null);
    }

    @Override
    public void searchForBooks(SearchCriteria searchCriteria, SearchFilter searchFilter) {

    }

    @Override
    public Cursor getSearchSuggestions(SearchCriteria searchCriteria, SearchFilter searchFilter) {
        return null;
    }

    @Override
    public void onItemClick(Author author) {

    }
}
