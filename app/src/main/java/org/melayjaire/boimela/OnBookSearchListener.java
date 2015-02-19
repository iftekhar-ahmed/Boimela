package org.melayjaire.boimela;

import android.database.Cursor;

import org.melayjaire.boimela.search.SearchCategory;
import org.melayjaire.boimela.search.SearchFilter;

/**
 * Created by Iftekhar on 09/01/2015.
 */
public interface OnBookSearchListener {
    /**
     * Query for books based on book title, author or publisher as search type
     *
     * @param searchCategory category of books like new, favorite
     * @param searchFilter   field constraints like book title, author or publisher, including query text
     */
    public void searchForBooks(SearchCategory searchCategory, SearchFilter searchFilter);

    /**
     * Suggests books based on book title, author or publisher as search type
     *
     * @param searchCategory category of books like new, favorite
     * @param searchFilter   field constraints like book title, author or publisher, including query text
     * @return cursor result with single column SearchManager.SUGGEST_COLUMN_TEXT_1
     */
    public Cursor getSearchSuggestions(SearchCategory searchCategory, SearchFilter searchFilter);
}
