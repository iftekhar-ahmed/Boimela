package org.melayjaire.boimela;

import android.database.Cursor;

import org.melayjaire.boimela.model.SearchType;

/**
 * Created by Iftekhar on 09/01/2015.
 */
public interface OnBookQueryListener {
    /**
     * Query for books based on book title, author or publisher as search type
     * @param queryText string filter to search for in books
     * @param searchType type of search requested.
     */
    public void listBooksWithQuery(String queryText, SearchType searchType);

    /**
     * Suggests books based on book title, author or publisher as search type
     * @param queryText string filter to search for in books
     * @param searchType type of search requested.
     * @return cursor result with single column SearchManager.SUGGEST_COLUMN_TEXT_1
     */
    public Cursor getSuggestionsForQuery(String queryText, SearchType searchType);
}
