package org.melayjaire.boimela.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.melayjaire.boimela.R;

public class ActionBarDrawerFragment extends Fragment implements View.OnClickListener {

    private int selectedItemId;
    private View selectedItem;
    private OnClickListener onClickListener;

    private static final String ARG_SELECTED_ITEM_ID = "_arg_selected_item_id";

    public interface OnClickListener {
        void onItemClick(View view);
    }

    public ActionBarDrawerFragment() {
    }

    public static ActionBarDrawerFragment newInstance() {
        return new ActionBarDrawerFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onClickListener = (OnClickListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(getClass().getSimpleName(), "activity must implement OnClickListener");
        }
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
        View newBooks = rootView.findViewById(R.id.drawer_item_new_books);
        newBooks.setOnClickListener(this);
        View favoriteBooks = rootView.findViewById(R.id.drawer_item_favorite_books);
        favoriteBooks.setOnClickListener(this);
        switch (selectedItemId) {
            case R.id.drawer_item_all_books:
                selectedItem = allBooks;
                break;
            case R.id.drawer_item_new_books:
                selectedItem = newBooks;
                break;
            case R.id.drawer_item_favorite_books:
                selectedItem = favoriteBooks;
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
