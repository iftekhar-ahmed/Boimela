package org.melayjaire.boimela.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.melayjaire.boimela.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iftekhar on 22/02/2015.
 */
public class NearbyBookListFragment extends BookListFragment {

    private static final String ARG_NEARBY_BOOKS = "_arg_nearby_books";

    public static NearbyBookListFragment newInstance(ArrayList<Book> nearbyBooks) {
        NearbyBookListFragment nearbyBookListFragment = new NearbyBookListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_NEARBY_BOOKS, nearbyBooks);
        nearbyBookListFragment.setArguments(args);
        return nearbyBookListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            List<Book> nearbyBooks = args.getParcelableArrayList(ARG_NEARBY_BOOKS);
            getBookListAdapter().swapList(nearbyBooks);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = getmRecyclerView();
        recyclerView.setPadding(0, 0, 0, 0);
        recyclerView.setClipToPadding(true);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle data) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
    }
}
