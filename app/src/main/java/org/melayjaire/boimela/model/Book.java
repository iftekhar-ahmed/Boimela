package org.melayjaire.boimela.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

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
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE;
import static org.melayjaire.boimela.data.BookDatabaseHelper.TITLE_ENGLISH;

public class Book implements Parcelable {

    private long id;
    private boolean isNew;
    private String title;
    private String titleInEnglish;
    private String author;
    private String authorInEnglish;
    private String category;
    private String publisher;
    private String publisherInEnglish;
    private String price;
    private String description;
    private double stallLatitude;
    private double stallLongitude;
    private boolean isFavorite;

    public Book(Parcel in) {
        id = in.readLong();
        isNew = in.readByte() != 0;
        title = in.readString();
        titleInEnglish = in.readString();
        author = in.readString();
        authorInEnglish = in.readString();
        category = in.readString();
        publisher = in.readString();
        publisherInEnglish = in.readString();
        price = in.readString();
        description = in.readString();
        stallLatitude = in.readDouble();
        stallLongitude = in.readDouble();
        isFavorite = in.readByte() != 0;
    }

    public Book() {}

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleInEnglish() {
        return titleInEnglish;
    }

    public void setTitleInEnglish(String titleInEnglish) {
        this.titleInEnglish = titleInEnglish;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorInEnglish() {
        return authorInEnglish;
    }

    public void setAuthorInEnglish(String authorInEnglish) {
        this.authorInEnglish = authorInEnglish;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherInEnglish() {
        return publisherInEnglish;
    }

    public void setPublisherInEnglish(String publisherInEnglish) {
        this.publisherInEnglish = publisherInEnglish;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStallLatitude() {
        return stallLatitude;
    }

    public void setStallLatitude(double stallLatitude) {
        this.stallLatitude = stallLatitude;
    }

    public double getStallLongitude() {
        return stallLongitude;
    }

    public void setStallLongitude(double stallLongitude) {
        this.stallLongitude = stallLongitude;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public ContentValues populate() {
        ContentValues values = new ContentValues();
        values.put(TITLE, getTitle().trim());
        values.put(TITLE_ENGLISH, getTitleInEnglish().trim());
        values.put(AUTHOR, getAuthor().trim());
        values.put(AUTHOR_ENGLISH, getAuthorInEnglish().trim());
        values.put(CATEGORY, getCategory().trim());
        values.put(PUBLISHER, getPublisher().trim());
        values.put(PUBLISHER_ENGLISH, getPublisherInEnglish().trim());
        values.put(PRICE, getPrice().trim());
        values.put(DESCRIPTION, getDescription().trim());
        values.put(STALL_LAT, getStallLatitude());
        values.put(STALL_LONG, getStallLongitude());
        values.put(FAVORITE, isFavorite() ? "1" : "0");
        values.put(IS_NEW, isNew() ? "1" : "0");
        return values;
    }

    public Book fromCursor(Cursor cursor) {
        setId(cursor.getLong(cursor.getColumnIndex(ID)));
        setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
        setTitleInEnglish(cursor
                .getString(cursor.getColumnIndex(TITLE_ENGLISH)));
        setAuthor(cursor.getString(cursor.getColumnIndex(AUTHOR)));
        setAuthorInEnglish(cursor.getString(cursor
                .getColumnIndex(AUTHOR_ENGLISH)));
        setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
        setPublisher(cursor.getString(cursor.getColumnIndex(PUBLISHER)));
        setPublisherInEnglish(cursor.getString(cursor
                .getColumnIndex(PUBLISHER_ENGLISH)));
        setPrice(cursor.getString(cursor.getColumnIndex(PRICE)));
        setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
        setStallLatitude(cursor.getDouble(cursor.getColumnIndex(STALL_LAT)));
        setStallLongitude(cursor.getDouble(cursor.getColumnIndex(STALL_LONG)));
        setFavorite(cursor.getInt(cursor.getColumnIndex(FAVORITE)) == 1);
        setNew(cursor.getInt(cursor.getColumnIndex(IS_NEW)) == 1);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeByte((byte) (isNew ? 1 : 0));
        out.writeString(title);
        out.writeString(titleInEnglish);
        out.writeString(author);
        out.writeString(authorInEnglish);
        out.writeString(category);
        out.writeString(publisher);
        out.writeString(publisherInEnglish);
        out.writeString(price);
        out.writeString(description);
        out.writeDouble(stallLatitude);
        out.writeDouble(stallLongitude);
        out.writeByte((byte) (isFavorite ? 1 : 0));
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
