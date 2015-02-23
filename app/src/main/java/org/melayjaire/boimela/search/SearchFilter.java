package org.melayjaire.boimela.search;

import static org.melayjaire.boimela.data.BookDatabaseHelper.AUTHOR;
import static org.melayjaire.boimela.data.BookDatabaseHelper.AUTHOR_ENGLISH;
import static org.melayjaire.boimela.data.BookDatabaseHelper.CATEGORY;
import static org.melayjaire.boimela.data.BookDatabaseHelper.ID;
import static org.melayjaire.boimela.data.BookDatabaseHelper.PUBLISHER;
import static org.melayjaire.boimela.data.BookDatabaseHelper.PUBLISHER_ENGLISH;
import static org.melayjaire.boimela.data.BookDatabaseHelper.RANK;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE_ENGLISH;

/**
 * Created by Iftekhar on 2/19/2015.
 */
public enum SearchFilter {

    Title(TITLE_ENGLISH, TITLE, new String[]{ID, TITLE_ENGLISH, TITLE}),

    Author(AUTHOR_ENGLISH, AUTHOR, new String[]{ID, AUTHOR_ENGLISH, AUTHOR}),

    Publisher(PUBLISHER_ENGLISH, PUBLISHER, new String[]{ID, PUBLISHER_ENGLISH, PUBLISHER}),

    BookCategory(CATEGORY, TITLE, new String[]{ID, CATEGORY, TITLE});

    SearchFilter(String primarySearchColumn, String secondarySearchColumn, String[] searchSuggestionColumns) {
        this.primarySearchColumn = primarySearchColumn;
        this.secondarySearchColumn = secondarySearchColumn;
        this.searchSuggestionColumns = searchSuggestionColumns;
    }

    private String queryText = "";

    private boolean fullyQualify = false;

    private boolean order = true;

    private final String primarySearchColumn;

    private final String secondarySearchColumn;

    private final String[] searchSuggestionColumns;

    public String getQueryText() {
        return queryText;
    }

    public boolean isFullyQualified() {
        return fullyQualify;
    }

    public String getPrimarySearchColumn() {
        return primarySearchColumn;
    }

    public String getSecondarySearchColumn() {
        return secondarySearchColumn;
    }

    public String[] getSearchSuggestionColumns() {
        return searchSuggestionColumns;
    }

    public boolean order() {
        return order;
    }

    /**
     * provide additional query requirements and result ordering
     *
     * @param queryText    the text literal to be used with primarySearchColumn of this filter
     * @param fullyQualify if true, requires result items to fully match queryText for this filter.
     *                     default is false.
     * @param order        whether or not to order result items
     * @return the configured SearchFilter
     */
    public SearchFilter withQuery(String queryText, boolean fullyQualify, boolean order) {
        this.queryText = queryText;
        this.fullyQualify = fullyQualify;
        this.order = order;
        return this;
    }
}
