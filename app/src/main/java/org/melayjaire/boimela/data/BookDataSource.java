package org.melayjaire.boimela.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.search.SearchCategory;
import org.melayjaire.boimela.search.SearchFilter;

import java.util.ArrayList;
import java.util.List;

import static org.melayjaire.boimela.data.BookDatabaseHelper.AUTHOR;
import static org.melayjaire.boimela.data.BookDatabaseHelper.AUTHOR_ENGLISH;
import static org.melayjaire.boimela.data.BookDatabaseHelper.CATEGORY;
import static org.melayjaire.boimela.data.BookDatabaseHelper.DESCRIPTION;
import static org.melayjaire.boimela.data.BookDatabaseHelper.FAVORITE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.ID;
import static org.melayjaire.boimela.data.BookDatabaseHelper.IS_NEW;
import static org.melayjaire.boimela.data.BookDatabaseHelper.PRICE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.PUBLISHER;
import static org.melayjaire.boimela.data.BookDatabaseHelper.PUBLISHER_ENGLISH;
import static org.melayjaire.boimela.data.BookDatabaseHelper.STALL_LAT;
import static org.melayjaire.boimela.data.BookDatabaseHelper.STALL_LONG;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TABLE_BOOK;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE_ENGLISH;

public class BookDataSource {

    private SQLiteDatabase database;
    private BookDatabaseHelper dbHelper;

    private final String[] allColumnsBook = {ID, TITLE, TITLE_ENGLISH, AUTHOR,
            AUTHOR_ENGLISH, CATEGORY, PUBLISHER, PUBLISHER_ENGLISH, PRICE,
            DESCRIPTION, STALL_LAT, STALL_LONG, FAVORITE, IS_NEW};

    /**
     * Source class for accessing all sorts of book data through convenience
     * methods. Must call {@link #open()} before accessing any data. Call {@link #close()}
     * responsibly after usage
     */
    public BookDataSource(Context context) {
        dbHelper = new BookDatabaseHelper(context);
    }

    public List<Book> cursorToBookList(Cursor dbResultCursor) {
        List<Book> books = new ArrayList<>();
        while (dbResultCursor.moveToNext()) {
            books.add(new Book().fromCursor(dbResultCursor));
        }
        dbResultCursor.close();
        return books;
    }

    public void open() throws SQLiteException {
        database = dbHelper.getWritableDatabase();
    }

    public boolean isEmpty() {
        SQLiteStatement s = database.compileStatement("SELECT count(*) FROM "
                + TABLE_BOOK);
        int dataCount = (int) s.simpleQueryForLong();
        s.close();
        return dataCount <= 0;
    }

    public void close() {
        dbHelper.close();
    }

    public void update(Book book) {
        ContentValues values = new ContentValues();
        values.put(FAVORITE, book.isFavorite() ? "1" : "0");
        database.update(TABLE_BOOK, values,
                ID + "=?",
                new String[]{String.valueOf(book.getId())});
    }

    public void insert(Book book) {
        book.setId(database.insert(TABLE_BOOK, null,
                book.populate()));
    }

    public void insert(List<Book> books) {
        for (Book book : books) {
            insert(book);
        }
    }

    public Cursor getInCursorByPriceRange(String[] range) {
        return database.query(TABLE_BOOK, allColumnsBook, PRICE + " >= ? AND "
                + PRICE + " <= ? ", range, null, null, null);
    }

    public Cursor getPlainSuggestions(SearchCategory searchCategory, SearchFilter searchFilter) {
        if (searchFilter == null) {
            return null;
        }
        return database.query(true, TABLE_BOOK, searchFilter.getSearchSuggestionColumns()
                , searchCategory == null ? null : (searchCategory.getKeySearchColumn() + "=?")
                , searchCategory == null ? null : new String[]{"1"}
                , searchFilter.getKeySearchColumn(), null, null, null);
    }

    public List<Book> getAllBooks() {
        Cursor result = database.query(TABLE_BOOK, allColumnsBook, null, null, null, null, null);
        return cursorToBookList(result);
    }

    /**
     * List of books for a specific category e.g- new, favorite etc.
     *
     * @param searchCategory category to group books
     * @return all books found for this category
     */
    public List<Book> getAllBooks(SearchCategory searchCategory) {
        if (searchCategory == null) {
            return getAllBooks();
        }
        Cursor result = database.query(TABLE_BOOK, allColumnsBook, searchCategory.getKeySearchColumn()
                + "=?", new String[]{"1"}, null, null, null);
        return cursorToBookList(result);
    }

    /**
     * List of books against a specific attribute e.g- title, author, publisher etc.
     *
     * @param searchFilter attribute to search for in books
     * @return all books found for this category
     */
    public List<Book> getAllBooks(SearchFilter searchFilter) {
        if (searchFilter == null) {
            return getAllBooks();
        }
        Cursor result = database.query(TABLE_BOOK, allColumnsBook, searchFilter.getKeySearchColumn()
                + (searchFilter.isFullyQualified() ? "=?" : " LIKE ?")
                , new String[]{searchFilter.isFullyQualified()
                ? searchFilter.getQueryText() : "%" + searchFilter.getQueryText() + "%"}
                , null, null, null);
        return cursorToBookList(result);
    }

    /**
     * List of books for a specific category e.g- new, favorite etc.
     * and against a specific attribute e.g- title, author, publisher etc.
     *
     * @param searchCategory category to group books
     * @param searchFilter attribute to search for in books
     * @return all books found for specified category and attribute
     */
    public List<Book> getAllBooks(SearchCategory searchCategory, SearchFilter searchFilter) {
        if (searchCategory == null && searchFilter == null) {
            return getAllBooks();
        } else if (searchFilter == null) {
            return getAllBooks(searchCategory);
        } else if (searchCategory == null) {
            return getAllBooks(searchFilter);
        }
        Cursor result = database.query(TABLE_BOOK, allColumnsBook, searchCategory.getKeySearchColumn()
                + "=? AND " + searchFilter.getKeySearchColumn() + (searchFilter.isFullyQualified() ? "=?" : " LIKE ?")
                , new String[]{"1"
                , searchFilter.isFullyQualified() ? searchFilter.getQueryText() : "%" + searchFilter.getQueryText() + "%"}
                , null, null, null);
        return cursorToBookList(result);
    }
}
