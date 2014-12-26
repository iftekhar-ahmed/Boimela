package org.melayjaire.boimela.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.model.SearchSuggestion;
import org.melayjaire.boimela.model.SearchSuggestionComparator;
import org.melayjaire.boimela.utils.EditDistance;

public class SearchHelper {

	private MatrixCursor suggestionsCursor;
	private Cursor dbResultCursor;
	private List<Book> books;
	private List<Double> similarityValues;
	private List<SearchSuggestion> searchSuggestions;

	private boolean returnAll;
	private int searchColumnIndex;
	private String filter;

	static final String[] suggestionColumns = { BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA };

	static final double SIMILARITY_THRESHOLD = 0.5;

	public SearchHelper() {
		books = new ArrayList<>();
		similarityValues = new ArrayList<>();
		searchSuggestions = new ArrayList<>();
	}

	/**
	 * Must be invoked before any helper method
	 * 
	 * @param searchColumn
	 *            column name for which database cursor will be matched (must
	 *            not be null). pass an empty string to specify no column.
	 * @param cursor
	 *            database result for the specified search category (must not be
	 *            null)
	 * @param filter
	 *            query filter for narrowing down search result
	 */
	public void prepare(String searchColumn, Cursor cursor, String filter) {
		dbResultCursor = cursor;
		if (filter == null || TextUtils.isEmpty(filter)) {
			returnAll = true;
		} else {
			this.filter = filter.toLowerCase();
			returnAll = false;
		}
		searchColumnIndex = dbResultCursor.getColumnIndex(searchColumn);
		if (!books.isEmpty())
			books.clear();
	}

	public List<Book> getFavorites() {
		while (dbResultCursor.moveToNext()) {
			books.add(new Book().fromCursorToFavorite(dbResultCursor));
		}
		dbResultCursor.close();
		return books;
	}

	public List<Book> cursorToBookList() {
		while (dbResultCursor.moveToNext()) {
			books.add(new Book().fromCursor(dbResultCursor));
		}
		dbResultCursor.close();
		return books;
	}

	public List<Book> findRelatedBooks() {
		if (returnAll) {
			return cursorToBookList();
		}

		while (dbResultCursor.moveToNext()) {
			if (dbResultCursor.getString(searchColumnIndex).toLowerCase()
					.contains(filter)) {
				books.add(new Book().fromCursor(dbResultCursor));
			}
		}
		dbResultCursor.close();
		return books;
	}

	public Cursor getSuggestions() {

		suggestionsCursor = new MatrixCursor(suggestionColumns);

		String value;
		String substring;
		String token;
		similarityValues.clear();
		searchSuggestions.clear();

		while (dbResultCursor.moveToNext()) {
			value = dbResultCursor.getString(searchColumnIndex);
			if (value.length() < filter.length())
				continue;
			StringTokenizer st = new StringTokenizer(value);
			while (st.hasMoreTokens()) {
				token = st.nextToken();
				if (token.length() < filter.length())
					continue;
				substring = token.substring(0, filter.length());
				double similarity = EditDistance.similarity(substring, filter);
				if (similarity > SIMILARITY_THRESHOLD) {
					SearchSuggestion searchSuggestion = new SearchSuggestion() {};
					searchSuggestion.info = new String[] {
							dbResultCursor.getString(0),
							dbResultCursor.getString(2),
							dbResultCursor.getString(1) };
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
		dbResultCursor.close();
		return suggestionsCursor;
	}
}
