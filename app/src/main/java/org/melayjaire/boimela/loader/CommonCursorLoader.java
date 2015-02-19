package org.melayjaire.boimela.loader;

import android.content.Context;
import android.database.Cursor;

import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.data.BookShelf;
import org.melayjaire.boimela.search.SearchType;

import java.io.IOException;

public class CommonCursorLoader extends SimpleCursorLoader {

    Context context;
    BookDataSource dataSource;
    SearchType category;

    public CommonCursorLoader(Context context, BookDataSource bds,
                              SearchType searchType) {
        super(context);
        this.context = context;
        dataSource = bds;
        category = searchType;
        if (category == null)
            category = SearchType.Title;
    }

    @Override
    public Cursor loadInBackground() {

        if (dataSource.isEmpty())
            dataSource.insert(loadBookShelf().getBooks());

        return dataSource.getPlainSuggestions(category);
    }

    private BookShelf loadBookShelf() {
        BookShelf bookShelf = BookShelf.getInstance();
        try {
            bookShelf.loadBooks(context.getResources());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookShelf;
    }
}
