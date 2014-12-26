package org.melayjaire.boimela.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.melayjaire.boimela.data.BookDataSource;

public class SearchSuggestionsProvider extends ContentProvider {

	BookDataSource dataSource;

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		return null;
	}

	@Override
	public boolean onCreate() {
		dataSource = new BookDataSource(getContext());
		dataSource.open();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		if (selectionArgs != null && selectionArgs.length > 0
				&& selectionArgs[0].length() > 0) {
			return dataSource.getSearchSuggestions(selectionArgs[0]);
		} else {
			return null;
		}
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		return 0;
	}

}
