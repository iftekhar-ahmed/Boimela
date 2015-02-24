package org.melayjaire.boimela.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.content.LocalBroadcastManager;

import org.melayjaire.boimela.model.Author;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.model.Publisher;
import org.melayjaire.boimela.search.SearchCriteria;
import org.melayjaire.boimela.search.SearchFilter;
import org.melayjaire.boimela.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import static org.melayjaire.boimela.data.BookDatabaseHelper.RANK;
import static org.melayjaire.boimela.data.BookDatabaseHelper.STALL_LAT;
import static org.melayjaire.boimela.data.BookDatabaseHelper.STALL_LONG;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TABLE_BOOK;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE_ENGLISH;

/**
 * Source class to access all sorts of book data through convenience
 * methods. Must call {@link #open()} before accessing any data. Call {@link #close()}
 * responsibly after usage
 */
public class BookDataSource {

    private Context context;
    private SQLiteDatabase database;
    private BookDatabaseHelper dbHelper;

    private final String[] allColumnsBook = {ID, TITLE, TITLE_ENGLISH, AUTHOR,
            AUTHOR_ENGLISH, CATEGORY, PUBLISHER, PUBLISHER_ENGLISH, PRICE,
            DESCRIPTION, STALL_LAT, STALL_LONG, FAVORITE, IS_NEW, RANK};
    private final String[] allColumnsAuthor = {ID, AUTHOR, AUTHOR_ENGLISH};
    private final String[] allColumnsPublisher = {PUBLISHER, PUBLISHER_ENGLISH,
            STALL_LAT, STALL_LONG};

    public BookDataSource(Context context) {
        this.context = context;
        dbHelper = new BookDatabaseHelper(context);
    }

    private void sendDataChangedBroadcast(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public List<Book> cursorToBookList(Cursor dbResultCursor) {
        List<Book> books = new ArrayList<>();
        while (dbResultCursor.moveToNext()) {
            books.add(new Book().fromCursor(dbResultCursor));
        }
        dbResultCursor.close();
        return books;
    }

    public List<Publisher> cursorToPublisherList(Cursor dbResultCursor) {
        List<Publisher> books = new ArrayList<>();
        while (dbResultCursor.moveToNext()) {
            books.add(new Publisher().fromCursor(dbResultCursor));
        }
        dbResultCursor.close();
        return books;
    }

    public List<Author> cursorToAuthorList(Cursor dbResultCursor) {
        List<Author> authors = new ArrayList<>();
        while (dbResultCursor.moveToNext()) {
            authors.add(new Author().fromCursor(dbResultCursor));
        }
        dbResultCursor.close();
        return authors;
    }

    public void open() throws SQLiteException {
        database = dbHelper.getWritableDatabase();
    }

    public boolean isOpen() {
        return database.isOpen();
    }

    public boolean isEmpty() {
        long dataCount = count();
        return dataCount <= 0;
    }

    public long count() {
        SQLiteStatement s = database.compileStatement("SELECT count(*) FROM "
                + TABLE_BOOK);
        long dataCount = s.simpleQueryForLong();
        s.close();
        return dataCount;
    }

    public long count(SearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            return count();
        }
        SQLiteStatement s = database.compileStatement("SELECT count(*) FROM "
                + TABLE_BOOK + " WHERE " + searchCriteria.getKeySearchColumn() + "=" + searchCriteria.getDefaultSearchArgument());
        long dataCount = s.simpleQueryForLong();
        s.close();
        return dataCount;
    }

    public long count(SearchFilter searchFilter, boolean distinct) {
        if (searchFilter == null) {
            return count();
        }
        SQLiteStatement s = database.compileStatement("SELECT count(" + (distinct ? "DISTINCT " : "")
                + searchFilter.getSecondarySearchColumn() + ") FROM "
                + TABLE_BOOK + " WHERE " + searchFilter.getPrimarySearchColumn()
                + (searchFilter.isFullyQualified()
                ? ("='" + searchFilter.getQueryText() + "'")
                : (" LIKE '%" + searchFilter.getQueryText() + "%'")));
        long dataCount = s.simpleQueryForLong();
        s.close();
        return dataCount;
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
        sendDataChangedBroadcast(Constants.ACTION_UPDATE_BOOK);
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

    public void insertBulk(int resourceId) {
        InputStream mInsertStream;
        BufferedReader mInsertReader;
        try {
            mInsertStream = context.getResources().openRawResource(resourceId);
            mInsertReader = new BufferedReader(new InputStreamReader(mInsertStream));
            while (mInsertReader.ready()) {
                String insertStmt = mInsertReader.readLine();
                database.execSQL(insertStmt);
            }
        } catch (SQLiteConstraintException | IOException e) {
            e.printStackTrace();
        }
        sendDataChangedBroadcast(Constants.ACTION_INSERT_BOOK);
    }

    public Cursor getInCursorByPriceRange(String[] range) {
        return database.query(TABLE_BOOK, allColumnsBook, PRICE + " >= ? AND "
                + PRICE + " <= ? ", range, null, null, null);
    }

    public Cursor getPlainSuggestions(SearchCriteria searchCriteria, SearchFilter searchFilter) {
        if (searchFilter == null) {
            return null;
        }
        return database.query(true, TABLE_BOOK, searchFilter.getSearchSuggestionColumns()
                , searchCriteria == null ? null : (searchCriteria.getKeySearchColumn() + "=?")
                , searchCriteria == null ? null : new String[]{"1"}
                , searchFilter.getPrimarySearchColumn(), null, null, null);
    }

    public List<Book> getAllBooks() {
        Cursor result = database.query(TABLE_BOOK, allColumnsBook, null, null, null, null, null);
        return cursorToBookList(result);
    }

    /**
     * List of books for a specific category e.g- new, favorite etc.
     *
     * @param searchCriteria category to group books
     * @return all books found for this category
     */
    public List<Book> getAllBooks(SearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            return getAllBooks();
        }
        Cursor result = database.query(TABLE_BOOK, allColumnsBook, searchCriteria.getKeySearchColumn()
                + "=?", new String[]{searchCriteria.getDefaultSearchArgument()}, null, null
                , TITLE);
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
        Cursor result = database.query(TABLE_BOOK, allColumnsBook, searchFilter.getPrimarySearchColumn()
                + (searchFilter.isFullyQualified() ? "=?" : " LIKE ?")
                , new String[]{searchFilter.isFullyQualified()
                ? searchFilter.getQueryText() : "%" + searchFilter.getQueryText() + "%"}
                , null, null
                , searchFilter.order() ? TITLE : null);
        return cursorToBookList(result);
    }

    /**
     * List of books for a specific category e.g- new, favorite etc.
     * and against a specific attribute e.g- title, author, publisher etc.
     *
     * @param searchCriteria category to group books
     * @param searchFilter   attribute to search for in books
     * @return all books found for specified category and attribute
     */
    public List<Book> getAllBooks(SearchCriteria searchCriteria, SearchFilter searchFilter) {
        if (searchCriteria == null && searchFilter == null) {
            return getAllBooks();
        } else if (searchFilter == null) {
            return getAllBooks(searchCriteria);
        } else if (searchCriteria == null) {
            return getAllBooks(searchFilter);
        }
        Cursor result = database.query(TABLE_BOOK, allColumnsBook, searchCriteria.getKeySearchColumn()
                + "=? AND " + searchFilter.getPrimarySearchColumn() + (searchFilter.isFullyQualified() ? "=?" : " LIKE ?")
                , new String[]{searchCriteria.getDefaultSearchArgument()
                , searchFilter.isFullyQualified() ? searchFilter.getQueryText() : "%" + searchFilter.getQueryText() + "%"}
                , null, null
                , searchFilter.order() ? TITLE : null);
        return cursorToBookList(result);
    }

    public List<Publisher> getAllPublishers() {
        Cursor result = database.query(true, TABLE_BOOK, allColumnsPublisher, null, null, null, null, PUBLISHER, null);
        return cursorToPublisherList(result);
    }

    public List<Publisher> getAllPublishers(SearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            return getAllPublishers();
        }
        Cursor result = database.query(true, TABLE_BOOK, allColumnsPublisher, searchCriteria.getKeySearchColumn()
                + "=?", new String[]{searchCriteria.getDefaultSearchArgument()}, null, null
                , PUBLISHER, null);
        return cursorToPublisherList(result);
    }

    public List<Author> getAllAuthors() {
        Cursor result = database.query(true, TABLE_BOOK, allColumnsAuthor, null, null, null, null, AUTHOR, null);
        return cursorToAuthorList(result);
    }
}
