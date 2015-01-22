package org.melayjaire.boimela;

import android.database.Cursor;

import org.melayjaire.boimela.model.SearchType;

/**
 * Created by Iftekhar on 09/01/2015.
 */
public interface OnBookQueryListener {
    public void listBooksWithQuery(String queryText, SearchType searchType);

    public Cursor getSuggestionsForQuery(String queryText, SearchType searchType);
}
