package org.melayjaire.boimela.search;

import static org.melayjaire.boimela.data.BookDatabaseHelper.AUTHOR;
import static org.melayjaire.boimela.data.BookDatabaseHelper.AUTHOR_ENGLISH;
import static org.melayjaire.boimela.data.BookDatabaseHelper.CATEGORY;
import static org.melayjaire.boimela.data.BookDatabaseHelper.ID;
import static org.melayjaire.boimela.data.BookDatabaseHelper.PUBLISHER;
import static org.melayjaire.boimela.data.BookDatabaseHelper.PUBLISHER_ENGLISH;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE_ENGLISH;

/**
 * Created by Iftekhar on 2/19/2015.
 */
public enum SearchFilter implements SearchConfig {

    Title() {
        @Override
        public String getKeySearchColumn() {
            return TITLE_ENGLISH;
        }

        @Override
        public String[] getSearchSuggestionColumns() {
            return new String[]{ID, TITLE_ENGLISH, TITLE};
        }
    },
    Author() {
        @Override
        public String getKeySearchColumn() {
            return AUTHOR_ENGLISH;
        }

        @Override
        public String[] getSearchSuggestionColumns() {
            return new String[]{ID, AUTHOR_ENGLISH, AUTHOR};
        }
    },
    Publisher() {
        @Override
        public String getKeySearchColumn() {
            return PUBLISHER_ENGLISH;
        }

        @Override
        public String[] getSearchSuggestionColumns() {
            return new String[]{ID, PUBLISHER_ENGLISH, PUBLISHER};
        }
    },
    BookCategory() {
        @Override
        public String getKeySearchColumn() {
            return CATEGORY;
        }

        @Override
        public String[] getSearchSuggestionColumns() {
            return new String[]{ID, CATEGORY};
        }
    };

    public String getQueryText() {
        return queryText;
    }

    public boolean isFullyQualified() {
        return fullyQualify;
    }

    public SearchFilter withQuery(String queryText, boolean fullyQualify) {
        this.queryText = queryText;
        this.fullyQualify = fullyQualify;
        return this;
    }

    private String queryText = "";

    private boolean fullyQualify = false;
}
