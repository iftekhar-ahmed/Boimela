package org.melayjaire.boimela.loader;

import android.content.Context;

import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.data.BookShelf;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.search.SearchCategory;
import org.melayjaire.boimela.search.SearchFilter;

import java.io.IOException;
import java.util.List;

public class BookListLoader extends SimpleListLoader {

    private Context context;
    private BookDataSource dataSource;
    private SearchCategory searchCategory;
    private SearchFilter searchFilter;

    public BookListLoader(Context context, BookDataSource dataSource,
                          SearchCategory searchCategory, SearchFilter searchFilter) {
        super(context);

        this.context = context;
        this.dataSource = dataSource;
        this.searchCategory = searchCategory;
        this.searchFilter = searchFilter;
    }

    @Override
    public List<Book> loadInBackground() {
        if (dataSource.isEmpty()) {
            dataSource.insert(loadBookShelf().getBooks());
        }
        return dataSource.getAllBooks(searchCategory, searchFilter);
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
