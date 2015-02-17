package org.melayjaire.boimela.loader;

import android.content.Context;

import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.data.BookShelf;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.model.SearchType;

import java.io.IOException;
import java.util.List;

public class BookListLoader extends SimpleListLoader {

    private Context context;
    private BookDataSource dataSource;
    private String filter;
    private SearchType searchType;

    public BookListLoader(Context context, BookDataSource bookDataSource,
                          SearchType searchType, String queryConstraint) {
        super(context);

        this.context = context;
        dataSource = bookDataSource;
        this.searchType = searchType == null ? SearchType.Title : searchType;
        filter = queryConstraint;
    }

    @Override
    public List<Book> loadInBackground() {
        List<Book> books;
        if (dataSource.isEmpty()) {
            dataSource.insert(loadBookShelf().getBooks());
        }

        switch (searchType) {
            case NewBooks:
                books = dataSource.getNewBooks();
                break;
            case Favorites:
                books = dataSource.getFavoritesForView();
                break;
            default:
                books = dataSource.getInList(searchType, filter);
        }
        return books;
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
