package org.melayjaire.boimela.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import org.melayjaire.boimela.OnBookSearchListener;
import org.melayjaire.boimela.R;
import org.melayjaire.boimela.adapter.BookListAdapter;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.loader.BookListLoader;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.search.SearchCategory;
import org.melayjaire.boimela.search.SearchFilter;
import org.melayjaire.boimela.search.SearchSuggestionHelper;
import org.melayjaire.boimela.utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Book>>, OnBookSearchListener
        , BookListAdapter.FavoriteCheckedListener, BookListAdapter.OnItemClickListener {

    private View bookListLoadProgressView;
    private RecyclerView mRecyclerView;
    private BookDataSource dataSource;
    private BookListAdapter bookListAdapter;

    private static final String ARG_SEARCH_CATEGORY = "_arg_search_category";
    private static final String ARG_SEARCH_FILTER = "_arg_search_filter";

    public static BookListFragment newInstance(SearchCategory searchCategory, SearchFilter searchFilter) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_CATEGORY, searchCategory);
        args.putSerializable(ARG_SEARCH_FILTER, searchFilter);
        fragment.setArguments(args);
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
        Bundle args = getArguments();
        getActivity().getSupportLoaderManager().initLoader(0, args, this);
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
        SearchCategory searchCategory = null;
        SearchFilter searchFilter = null;
        if (data != null && data.containsKey(ARG_SEARCH_CATEGORY) && data.containsKey(ARG_SEARCH_FILTER)) {
            Serializable category = data.getSerializable(ARG_SEARCH_CATEGORY);
            if (category != null) {
                searchCategory = (SearchCategory) category;
            }
            Serializable filter = data.getSerializable(ARG_SEARCH_FILTER);
            if (filter != null) {
                searchFilter = (SearchFilter) filter;
            }
        }
        return new BookListLoader(getActivity(), dataSource, searchCategory, searchFilter);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        if (books.size() == 1) {
            Message message = new Message();
            message.obj = books.get(0);
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Book book = (Book) msg.obj;
                    BookDetailFragment bookDetailFragment = BookDetailFragment.newInstance(book);
                    bookDetailFragment.show(getFragmentManager(), "Detail");
                }
            };
            handler.sendMessage(message);
        } else {
            bookListAdapter.swapList(books);
            if (books.isEmpty()) {
                Toast.makeText(getActivity(), Utilities.getBanglaSpannableString(getString(R.string.no_record_found)
                        , getActivity()), Toast.LENGTH_SHORT).
                        show();
            }
        }
        Utilities.showListLoadProgress(getActivity(), mRecyclerView,
                bookListLoadProgressView, false);
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
    public void searchForBooks(SearchCategory searchCategory, SearchFilter searchFilter) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_CATEGORY, searchCategory);
        args.putSerializable(ARG_SEARCH_FILTER, searchFilter);
        getActivity().getSupportLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public Cursor getSearchSuggestions(SearchCategory searchCategory, SearchFilter searchFilter) {
        SearchSuggestionHelper searchSuggestionHelper = SearchSuggestionHelper.getInstance();
        return searchSuggestionHelper.getSuggestions(dataSource.getPlainSuggestions(searchCategory, searchFilter), searchFilter);
    }
}
