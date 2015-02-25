package org.melayjaire.boimela.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.search.SearchCriteria;
import org.melayjaire.boimela.search.SearchFilter;
import org.melayjaire.boimela.utils.Constants;
import org.melayjaire.boimela.utils.Utilities;
import org.melayjaire.boimela.view.BanglaTextView;

public class ActionBarDrawerFragment extends Fragment implements View.OnClickListener {

    private int selectedItemId;
    private View selectedItem;
    private BanglaTextView allBooksCounter;
    private BanglaTextView favBooksCounter;
    private BanglaTextView rankedBooksCounter;
    private BanglaTextView authorCounter;
    private OnClickListener onClickListener;
    private BookDataSource bookDataSource;
    private BroadcastReceiver bookUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_INSERT_BOOK) | action.equals(Constants.ACTION_UPDATE_BOOK)) {
                updateAllCounters();
            }
        }
    };

    private static final String ARG_SELECTED_ITEM_ID = "_arg_selected_item_id";

    public interface OnClickListener {
        void onItemClick(View view);
    }

    public ActionBarDrawerFragment() {
    }

    public static ActionBarDrawerFragment newInstance() {
        return new ActionBarDrawerFragment();
    }

    private void updateAllCounters() {
        if (!bookDataSource.isOpen()) {
            bookDataSource.open();
        }
        allBooksCounter.setBanglaText(Utilities.translateCount(bookDataSource.count()));
        favBooksCounter.setBanglaText(Utilities.translateCount(bookDataSource.count(SearchCriteria.Favorites)));
        rankedBooksCounter.setBanglaText(Utilities.translateCount(bookDataSource.count(SearchCriteria.Rank)));
        authorCounter.setBanglaText(Utilities.translateCount(bookDataSource.count(SearchFilter.Author, true)));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bookDataSource = new BookDataSource(activity);
        bookDataSource.open();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_INSERT_BOOK);
        intentFilter.addAction(Constants.ACTION_UPDATE_BOOK);
        LocalBroadcastManager.getInstance(activity).registerReceiver(bookUpdateBroadcastReceiver, intentFilter);
        try {
            onClickListener = (OnClickListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(getClass().getSimpleName(), "activity must implement OnClickListener");
        }
    }

    @Override
    public void onDetach() {
        bookDataSource.close();
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_SELECTED_ITEM_ID, selectedItemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            selectedItemId = savedInstanceState.getInt(ARG_SELECTED_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_action_bar_drawer, container, false);

        View allBooks = rootView.findViewById(R.id.drawer_item_all_books);
        allBooks.setOnClickListener(this);
        View rankedBooks = rootView.findViewById(R.id.drawer_item_ranked_books);
        rankedBooks.setOnClickListener(this);
        View favoriteBooks = rootView.findViewById(R.id.drawer_item_favorite_books);
        favoriteBooks.setOnClickListener(this);
        View authors = rootView.findViewById(R.id.drawer_item_authors);
        authors.setOnClickListener(this);

        allBooksCounter = (BanglaTextView) allBooks.findViewById(R.id.textView_counter_all_books);
        favBooksCounter = (BanglaTextView) favoriteBooks.findViewById(R.id.textView_counter_favorite_books);
        rankedBooksCounter = (BanglaTextView) rankedBooks.findViewById(R.id.textView_counter_ranked_books);
        authorCounter = (BanglaTextView) authors.findViewById(R.id.textView_counter_authors);
        updateAllCounters();

        switch (selectedItemId) {
            case R.id.drawer_item_all_books:
                selectedItem = allBooks;
                break;
            case R.id.drawer_item_ranked_books:
                selectedItem = rankedBooks;
                break;
            case R.id.drawer_item_favorite_books:
                selectedItem = favoriteBooks;
                break;
            case R.id.drawer_item_authors:
                selectedItem = authors;
                break;
            default:
                selectedItem = allBooks;
        }
        selectedItem.setSelected(true);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        assert onClickListener != null;
        selectedItem.setSelected(false);
        selectedItem = v;
        selectedItemId = selectedItem.getId();
        selectedItem.setSelected(true);
        onClickListener.onItemClick(selectedItem);
    }
}
