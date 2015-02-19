package org.melayjaire.boimela.search;

import static org.melayjaire.boimela.data.BookDatabaseHelper.FAVORITE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.ID;
import static org.melayjaire.boimela.data.BookDatabaseHelper.IS_NEW;

/**
 * Created by Iftekhar on 2/19/2015.
 */
public enum SearchCategory implements SearchConfig {

    NewBooks() {
        @Override
        public String getKeySearchColumn() {
            return FAVORITE;
        }

        @Override
        public String[] getSearchSuggestionColumns() {
            return new String[]{ID, FAVORITE};
        }
    },
    Favorites() {
        @Override
        public String getKeySearchColumn() {
            return IS_NEW;
        }

        @Override
        public String[] getSearchSuggestionColumns() {
            return new String[]{ID, IS_NEW};
        }
    };
}
