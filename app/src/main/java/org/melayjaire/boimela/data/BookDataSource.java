package org.melayjaire.boimela.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.model.SearchType;
import org.melayjaire.boimela.utils.SearchCategoryMap;
import org.melayjaire.boimela.utils.SearchHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import static org.melayjaire.boimela.data.BookDatabaseHelper.TABLE_FAVORITES;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE_ENGLISH;

public class BookDataSource {

    private String[] allColumnsBook = {ID, TITLE, TITLE_ENGLISH, AUTHOR,
            AUTHOR_ENGLISH, CATEGORY, PUBLISHER, PUBLISHER_ENGLISH, PRICE,
            DESCRIPTION, STALL_LAT, STALL_LONG, FAVORITE, IS_NEW};
    private String[] allColumnsFavorites = {ID, TITLE, PUBLISHER,
            PUBLISHER_ENGLISH, STALL_LAT, STALL_LONG};

    private SQLiteDatabase database;
    private BookDatabaseHelper dbHelper;
    private SearchHelper searchHelper;
    private Map<SearchType, String[]> categoryColumnMap;
    private SharedPreferences preferences;
    private SearchCategoryMap searchCategoryMap;

    private Context context;

    /**
     * Source class for accessing all sorts of book data through convenience
     * methods. Must call {@link #open()} before accessing any data. Call {@link #close()}
     * responsibly after usage
     */
    public BookDataSource(Context context) {
        this.context = context;
        dbHelper = new BookDatabaseHelper(context);
        categoryColumnMap = new HashMap<>();
        createMap();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        searchCategoryMap = new SearchCategoryMap(context);
    }

    private void createMap() {
        categoryColumnMap.put(SearchType.Title, new String[]{ID,
                TITLE_ENGLISH, TITLE});
        categoryColumnMap.put(SearchType.Author, new String[]{ID,
                AUTHOR_ENGLISH, AUTHOR});
        categoryColumnMap.put(SearchType.Publisher, new String[]{ID,
                PUBLISHER_ENGLISH, PUBLISHER});
        categoryColumnMap.put(SearchType.Category, new String[]{ID,
                CATEGORY});
        categoryColumnMap.put(SearchType.NewBook, new String[]{ID,
                IS_NEW});
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
        if (book.isFavorite()) {
            if (addToFavorites(book))
                database.update(TABLE_BOOK, book.update(new ContentValues()),
                        ID + "=?",
                        new String[]{String.valueOf(book.getId())});
        } else {
            if (removeFromFavorites(book))
                database.update(TABLE_BOOK, book.update(new ContentValues()),
                        ID + "=?",
                        new String[]{String.valueOf(book.getId())});
        }
    }

    public boolean addToFavorites(Book book) {
        return database.insert(TABLE_FAVORITES, null,
                book.addToFavorite(new ContentValues())) != -1;
    }

    public boolean removeFromFavorites(Book book) {
        return database.delete(TABLE_FAVORITES, ID + "=?",
                new String[]{String.valueOf(book.getId())}) == 1;
    }

    public void insert(Book book) {
        book.setId(database.insert(TABLE_BOOK, null,
                book.populate(new ContentValues())));
    }

    public void insert(List<Book> books) {
        for (Book book : books) {
            insert(book);
        }
    }

    public String[] getCursorColumns(SearchType category) {
        return categoryColumnMap.get(category);
    }

    public Cursor getSearchSuggestions(String filter) {
        if (searchHelper == null)
            searchHelper = new SearchHelper();
        SearchType category = searchCategoryMap.obtain(preferences
                .getString(
                        context.getString(R.string.pref_key_search_category),
                        context.getString(R.string.title)));
        searchHelper.prepare(categoryColumnMap.get(category)[1],
                getInCursor(category), filter);
        return searchHelper.getSuggestions();
    }

    public Cursor getInCursorByPriceRange(String[] range) {
        return database.query(TABLE_BOOK, allColumnsBook, PRICE + " >= ? AND "
                + PRICE + " <= ? ", range, null, null, null);
    }

    public Cursor getInCursor(SearchType category) {
        return database.query(true, TABLE_BOOK,
                categoryColumnMap.get(category), null, null,
                categoryColumnMap.get(category)[1], null, null, null);
    }

    public Cursor getAllInCursor() {
        return database.query(TABLE_BOOK, allColumnsBook, null, null, null,
                null, null);
    }

    public List<Book> getInList(SearchType category, String filter) {
        if (searchHelper == null)
            searchHelper = new SearchHelper();
        searchHelper.prepare(categoryColumnMap.get(category)[1],
                getAllInCursor(), filter);
        return searchHelper.findRelatedBooks();
    }

    /**
     * Use this method to quickly access book data stored in FAVORITES table
     * (e.g.- calculate locations of books)
     */
    public List<Book> getFavorites() {
        Cursor favorites = database.query(TABLE_FAVORITES, allColumnsFavorites,
                null, null, null, null, null);
        if (searchHelper == null)
            searchHelper = new SearchHelper();
        searchHelper.prepare("", favorites, null);
        return searchHelper.getFavorites();
    }

    /**
     * Special method to view favorite books in list. Needed because the
     * FAVORITES table doesn't contain full book data
     */
    public List<Book> getFavoritesForView() {
        Cursor favorites = database.query(TABLE_BOOK, allColumnsBook, FAVORITE
                + "=?", new String[]{"1"}, null, null, null);
        if (searchHelper == null)
            searchHelper = new SearchHelper();
        searchHelper.prepare("", favorites, null);
        return searchHelper.cursorToBookList();
    }

    public List<Book> getNewBooks() {
        Cursor new_books = database.query(TABLE_BOOK, allColumnsBook, IS_NEW
                + "=?", new String[]{"1"}, null, null, null);
        if (searchHelper == null)
            searchHelper = new SearchHelper();
        searchHelper.prepare("", new_books, null);
        return searchHelper.cursorToBookList();
    }

    public List<Book> getFavoritesByPublisher(String publisherNameEng) {
        Cursor result = database
                .query(TABLE_FAVORITES, allColumnsFavorites, PUBLISHER_ENGLISH
                                + "=?", new String[]{publisherNameEng},
                        null, null, null);
        if (searchHelper == null)
            searchHelper = new SearchHelper();
        searchHelper.prepare("", result, null);
        return searchHelper.getFavorites();
    }
}
