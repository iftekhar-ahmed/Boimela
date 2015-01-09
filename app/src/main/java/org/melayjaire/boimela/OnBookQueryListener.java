package org.melayjaire.boimela;

import android.database.Cursor;

/**
 * Created by Iftekhar on 09/01/2015.
 */
public interface OnBookQueryListener {
    public void listBooksWithQuery(String queryText);

    public Cursor getSuggestionsForQuery(String queryText);
}
