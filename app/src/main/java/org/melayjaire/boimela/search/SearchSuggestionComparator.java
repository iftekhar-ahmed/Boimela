package org.melayjaire.boimela.search;

import java.util.Comparator;

public class SearchSuggestionComparator implements Comparator<SearchSuggestion> {

    @Override
    public int compare(SearchSuggestion lhs, SearchSuggestion rhs) {
        if (lhs.similarity > rhs.similarity) {
            return -1;
        } else if (lhs.similarity < rhs.similarity) {
            return 1;
        }
        return 0;
    }

}
