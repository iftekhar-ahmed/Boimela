package org.melayjaire.boimela.search;

import static org.melayjaire.boimela.data.BookDatabaseHelper.FAVORITE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.IS_NEW;

/**
 * Created by Iftekhar on 2/19/2015.
 */
public enum SearchCriteria {

    NewBooks(IS_NEW, "1"),

    Favorites(FAVORITE, "1");

    public String getDefaultSearchArgument() {
        return defaultSearchArgument;
    }

    public String getKeySearchColumn() {
        return keySearchColumn;
    }

    private final String keySearchColumn;
    private final String defaultSearchArgument;

    SearchCriteria(String keySearchColumn, String defaultSearchArgument) {
        this.keySearchColumn = keySearchColumn;
        this.defaultSearchArgument = defaultSearchArgument;
    }
}
