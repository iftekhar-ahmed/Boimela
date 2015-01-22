package org.melayjaire.boimela.data;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.model.Book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BookShelf {

    private static BookShelf instance;
    private static final int COLUMN_COUNT = 12;
    private static final int resourceId = R.raw.books;

    private BookShelf() {
        books = new ArrayList<>();
    }

    public static BookShelf getInstance() {
        if (instance == null) {
            instance = new BookShelf();
        }
        return instance;
    }

    private List<Book> books;

    public List<Book> getBooks() {
        Log.d("bookshelf", "" + books.size() + " found");
        return books;
    }

    public synchronized void loadBooks(Resources resources) throws IOException {
        InputStream inputStream = resources.openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, ";");
                if (strings.length < COLUMN_COUNT)
                    continue;
                addBook(strings);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            reader.close();
        }
    }

    private synchronized void addBook(String[] data) {
        Book book = new Book();
        book.setTitle(data[0]);
        book.setTitleInEnglish(data[1]);
        book.setAuthor(data[2]);
        book.setAuthorInEnglish(data[3]);
        book.setPublisher(data[4]);
        book.setPublisherInEnglish(data[5]);
        book.setCategory(data[6]);
        book.setDescription(data[7]);
        book.setPrice((data[8]));
        book.setStallLatitude(Double.parseDouble(data[9]));
        book.setStallLongitude(Double.parseDouble(data[10]));
        book.setIsNew(Integer.parseInt(data[11]) == 1);
        books.add(book);
    }
}
