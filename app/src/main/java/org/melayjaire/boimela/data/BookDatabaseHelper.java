package org.melayjaire.boimela.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookDatabaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
	public static final String DB_NAME = "book_db.sqlite";
	public static final String TABLE_BOOK = "book";
	public static final String TABLE_FAVORITES = "favorites";
	public static final String ID = "_id";
	public static final String TITLE = "title";
	public static final String TITLE_ENGLISH = "title_english";
	public static final String AUTHOR = "author";
	public static final String AUTHOR_ENGLISH = "author_english";
	public static final String CATEGORY = "category";
	public static final String PUBLISHER = "publisher";
	public static final String PUBLISHER_ENGLISH = "publisher_english";
	public static final String PRICE = "price";
	public static final String DESCRIPTION = "description";
	public static final String STALL_LAT = "stall_latitude";
	public static final String STALL_LONG = "stall_longiitude";
	public static final String FAVORITE = "favorite";
	public static final String IS_NEW = "is_new";

	protected BookDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createBookTable(db);
	}

	private void createBookTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_BOOK + "(" + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + TITLE
				+ " TEXT, " + TITLE_ENGLISH + " TEXT, " + AUTHOR + " TEXT, "
				+ AUTHOR_ENGLISH + " TEXT, " + CATEGORY + " TEXT, " + PUBLISHER
				+ " TEXT, " + PUBLISHER_ENGLISH + " TEXT, " + PRICE + " TEXT, "
				+ DESCRIPTION + " TEXT, " + STALL_LAT + " REAL, " + STALL_LONG
				+ " REAL, " + FAVORITE + " INTEGER, " + IS_NEW + " INTEGER "
				+ ");");
		db.execSQL("CREATE TABLE " + TABLE_FAVORITES + "(" + ID
				+ " INTEGER PRIMARY KEY NOT NULL, " + TITLE + " TEXT, "
				+ PUBLISHER + " TEXT, " + PUBLISHER_ENGLISH + " TEXT, "
				+ STALL_LAT + " REAL, " + STALL_LONG + " REAL " + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(this.getClass().getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
		onCreate(db);
	}
}
