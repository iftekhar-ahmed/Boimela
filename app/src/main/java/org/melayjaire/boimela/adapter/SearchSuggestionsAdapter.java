package org.melayjaire.boimela.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;

import org.melayjaire.boimela.view.BanglaTextView;

/**
 * Created by Iftekhar on 02/01/2015.
 */
public abstract class SearchSuggestionsAdapter extends CursorAdapter {

    public SearchSuggestionsAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder {
        public BanglaTextView textView;
    }
}
