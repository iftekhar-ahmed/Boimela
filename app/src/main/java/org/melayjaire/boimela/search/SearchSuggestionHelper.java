package org.melayjaire.boimela.search;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import org.melayjaire.boimela.utils.EditDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class SearchSuggestionHelper {

    private List<SearchSuggestion> searchSuggestions;

    private static SearchSuggestionHelper searchSuggestionHelper;

    static final String[] suggestionColumns = {BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA};

    static final double SIMILARITY_THRESHOLD = 0.5;

    private SearchSuggestionHelper() {
        searchSuggestions = new ArrayList<>();
    }

    public static SearchSuggestionHelper getInstance() {
        if (searchSuggestionHelper == null) {
            searchSuggestionHelper = new SearchSuggestionHelper();
        }
        return searchSuggestionHelper;
    }

    public Cursor getSuggestions(Cursor allSuggestionsCursor, SearchFilter searchFilter) {
        int searchColumnIndex = allSuggestionsCursor.getColumnIndex(searchFilter.getPrimarySearchColumn());
        String value;
        String substring;
        String token;
        String queryText = searchFilter.getQueryText().toLowerCase();
        MatrixCursor suggestionsCursor = new MatrixCursor(suggestionColumns);
        searchSuggestions.clear();

        while (allSuggestionsCursor.moveToNext()) {
            value = allSuggestionsCursor.getString(searchColumnIndex);
            if (value.length() < queryText.length())
                continue;
            StringTokenizer st = new StringTokenizer(value);
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (token.length() < queryText.length())
                    continue;
                substring = token.substring(0, queryText.length());
                double similarity = EditDistance.similarity(substring, queryText);
                if (similarity > SIMILARITY_THRESHOLD) {
                    SearchSuggestion searchSuggestion = new SearchSuggestion();
                    searchSuggestion.info = new String[]{
                            allSuggestionsCursor.getString(0),
                            allSuggestionsCursor.getString(2),
                            allSuggestionsCursor.getString(1)};
                    searchSuggestion.similarity = similarity;
                    searchSuggestions.add(searchSuggestion);
                    break;
                }
            }
        }
        Collections.sort(searchSuggestions, new SearchSuggestionComparator());
        for (int i = 0; i < searchSuggestions.size(); i++) {
            suggestionsCursor.addRow(searchSuggestions.get(i).info);
        }
        allSuggestionsCursor.close();
        return suggestionsCursor;
    }
}
