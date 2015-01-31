package org.melayjaire.boimela.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.melayjaire.boimela.R;
import org.w3c.dom.Text;

public class ActionBarDrawerFragment extends Fragment implements View.OnClickListener {

    public ActionBarDrawerFragment() {
    }

    public static ActionBarDrawerFragment newInstance() {
        ActionBarDrawerFragment actionBarDrawerFragment = new ActionBarDrawerFragment();
        return actionBarDrawerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_action_bar_drawer, container, false);
        TextView tvBookType = (TextView) rootView.findViewById(R.id.textView_book_type);
        tvBookType.setOnClickListener(this);
        TextView tvNewBooks = (TextView) rootView.findViewById(R.id.textView_new_books);
        tvNewBooks.setOnClickListener(this);
        TextView tvFavBooks = (TextView) rootView.findViewById(R.id.textView_favorite_books);
        tvFavBooks.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_book_type:
                Toast.makeText(getActivity(), "book type", Toast.LENGTH_SHORT).show();
                break;
            case R.id.textView_new_books:
                Toast.makeText(getActivity(), "new books", Toast.LENGTH_SHORT).show();
                break;
            case R.id.textView_favorite_books:
                Toast.makeText(getActivity(), "favorite books", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
