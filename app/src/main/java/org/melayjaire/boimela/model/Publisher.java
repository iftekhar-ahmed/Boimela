package org.melayjaire.boimela.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import static org.melayjaire.boimela.data.BookDatabaseHelper.PUBLISHER;
import static org.melayjaire.boimela.data.BookDatabaseHelper.PUBLISHER_ENGLISH;
import static org.melayjaire.boimela.data.BookDatabaseHelper.STALL_LAT;
import static org.melayjaire.boimela.data.BookDatabaseHelper.STALL_LONG;

/**
 * Created by Iftekhar on 21/02/2015.
 */
public class Publisher implements Parcelable {

    private String name;
    private String nameInEnglish;
    private double stallLatitude;
    private double stallLongitude;

    public Publisher() {

    }

    public Publisher(Parcel in) {
        name = in.readString();
        nameInEnglish = in.readString();
        stallLatitude = in.readDouble();
        stallLongitude = in.readDouble();
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

    public Publisher fromCursor(Cursor cursor) {
        setName(cursor.getString(cursor.getColumnIndex(PUBLISHER)));
        setNameInEnglish(cursor.getString(cursor
                .getColumnIndex(PUBLISHER_ENGLISH)));
        setStallLatitude(cursor.getDouble(cursor.getColumnIndex(STALL_LAT)));
        setStallLongitude(cursor.getDouble(cursor.getColumnIndex(STALL_LONG)));
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
        out.writeDouble(stallLatitude);
        out.writeDouble(stallLongitude);
    }

    public static final Creator<Publisher> CREATOR = new Creator<Publisher>() {
        @Override
        public Publisher createFromParcel(Parcel source) {
            return new Publisher(source);
        }

        @Override
        public Publisher[] newArray(int size) {
            return new Publisher[size];
        }
    };
}
