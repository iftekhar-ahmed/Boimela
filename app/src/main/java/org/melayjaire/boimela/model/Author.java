package org.melayjaire.boimela.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import static org.melayjaire.boimela.data.BookDatabaseHelper.AUTHOR;
import static org.melayjaire.boimela.data.BookDatabaseHelper.AUTHOR_ENGLISH;

/**
 * Created by Iftekhar on 2/24/2015.
 */
public class Author implements Parcelable {

    private String name;
    private String nameInEnglish;
    private long booksCount;

    public Author() {

    }

    public Author(Parcel in) {
        this.name = in.readString();
        this.nameInEnglish = in.readString();
        this.booksCount = in.readLong();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameInEnglish() {
        return nameInEnglish;
    }

    public void setNameInEnglish(String nameInEnglish) {
        this.nameInEnglish = nameInEnglish;
    }

    public long getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(long booksCount) {
        this.booksCount = booksCount;
    }

    public Author fromCursor(Cursor cursor) {
        setName(cursor.getString(cursor.getColumnIndex(AUTHOR)));
        setNameInEnglish(cursor.getString(cursor
                .getColumnIndex(AUTHOR_ENGLISH)));
        setBooksCount(0);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(nameInEnglish);
        out.writeLong(booksCount);
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel source) {
            return new Author(source);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}
