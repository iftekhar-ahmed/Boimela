package org.melayjaire.boimela;

import android.database.Cursor;

import org.melayjaire.boimela.search.SearchCriteria;
import org.melayjaire.boimela.search.SearchFilter;

/**
 * Created by Iftekhar on 09/01/2015.
 */
public interface OnBookSearchListener {

    public static final String ARG_SEARCH_CATEGORY = "_arg_search_category";
    public static final String ARG_SEARCH_FILTER = "_arg_search_filter";

    /**
     * Query for books based on book title, author or publisher as search type
     *
     * @param searchCriteria category of books like new, favorite
     * @param searchFilter   field constraints like book title, author or publisher, including query text
     */
    public void searchForBooks(SearchCriteria searchCriteria, SearchFilter searchFilter);

    /**
     * Suggests books based on book title, author or publisher as search type
     *
     * @param searchCriteria category of books like new, favorite
     * @param searchFilter   field constraints like book title, author or publisher, including query text
     * @return cursor result with single column SearchManager.SUGGEST_COLUMN_TEXT_1
     */
    public Cursor getSearchSuggestions(SearchCriteria searchCriteria, SearchFilter searchFilter);
}
